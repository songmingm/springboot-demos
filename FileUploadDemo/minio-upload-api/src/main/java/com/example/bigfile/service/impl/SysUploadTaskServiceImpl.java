package com.example.bigfile.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bigfile.configs.MinioProperties;
import com.example.bigfile.constant.MinioConstant;
import com.example.bigfile.mapper.SysUploadTaskMapper;
import com.example.bigfile.model.dto.TaskInfoDTO;
import com.example.bigfile.model.dto.TaskRecordDTO;
import com.example.bigfile.model.entity.SysUploadTask;
import com.example.bigfile.model.param.InitTaskParam;
import com.example.bigfile.service.SysUploadTaskService;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分片上传-分片任务记录(SysUploadTask)表服务实现类
 *
 * @author mmsong
 * @since 2023/1/31 19:27
 */
@Service("sysUploadTaskService")
public class SysUploadTaskServiceImpl extends ServiceImpl<SysUploadTaskMapper, SysUploadTask> implements SysUploadTaskService {

    @Resource
    private MinioProperties minioProperties;

    @Resource
    private AmazonS3 amazonS3;

    @Resource
    private SysUploadTaskMapper sysUploadTaskMapper;

    @Override
    public SysUploadTask getByIdentifier(String identifier) {
        return sysUploadTaskMapper.selectOne(new QueryWrapper<SysUploadTask>().lambda().eq(SysUploadTask::getFileIdentifier, identifier));
    }

    @Override
    public TaskInfoDTO initTask(InitTaskParam param) {
        Date currentDate = new Date();
        String bucket = minioProperties.getBucket();
        String fileName = param.getFileName();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        String key = StrUtil.format("{}/{}.{}", DateUtil.format(currentDate, "YYYY-MM-dd"), IdUtil.randomUUID(), suffix);
        String contentType = MediaTypeFactory.getMediaType(key).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3
                .initiateMultipartUpload(new InitiateMultipartUploadRequest(bucket, key).withObjectMetadata(objectMetadata));
        String uploadId = initiateMultipartUploadResult.getUploadId();

        SysUploadTask task = new SysUploadTask();
        int chunkNum = (int) Math.ceil(param.getTotalSize() * 1.0 / param.getChunkSize());
        task.setBucketName(bucket)
                .setChunkNum(chunkNum)
                .setChunkSize(param.getChunkSize())
                .setTotalSize(param.getTotalSize())
                .setFileIdentifier(param.getIdentifier())
                .setFileName(param.getFileName())
                .setObjectKey(key)
                .setUploadId(uploadId);
        sysUploadTaskMapper.insert(task);
        return new TaskInfoDTO().setFinished(false)
                .setTaskRecord(TaskRecordDTO.convertFromEntity(task))
                .setPath(getPath(bucket, key));

    }

    @Override
    public String getPath(String bucket, String objectKey) {
        return StrUtil.format("{}/{}/{}", minioProperties.getEndpoint(), bucket, objectKey);
    }

    @Override
    public TaskInfoDTO getTaskInfo(String identifier) {
        SysUploadTask task = getByIdentifier(identifier);
        if (task == null)
            return null;

        TaskInfoDTO result = new TaskInfoDTO()
                .setFinished(true)
                .setTaskRecord(TaskRecordDTO.convertFromEntity(task))
                .setPath(getPath(task.getBucketName(), task.getObjectKey()));
        boolean doesObjectExist = amazonS3.doesObjectExist(task.getBucketName(), task.getObjectKey());

        if (!doesObjectExist) {
            // 未上传完，返回已上传的分片
            ListPartsRequest listPartsRequest = new ListPartsRequest(task.getBucketName(), task.getObjectKey(), task.getUploadId());
            PartListing partListing = amazonS3.listParts(listPartsRequest);
            result.setFinished(false).getTaskRecord().setExitPartList(partListing.getParts());
        }
        return result;
    }

    @Override
    public String genPreSignUploadUrl(String bucket, String objectKey, Map<String, String> params) {
        Date currentDate = new Date();
        Date expireDate = DateUtil.offsetMillisecond(currentDate, MinioConstant.PRE_SIGN_URL_EXPIRE.intValue());
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectKey)
                .withExpiration(expireDate).withMethod(HttpMethod.PUT);
        if (params != null) {
            params.forEach(request::addRequestParameter);
        }
        URL preSignedUrl = amazonS3.generatePresignedUrl(request);
        return preSignedUrl.toString();
    }

    @Override
    public void merge(String identifier) {
        SysUploadTask task = getByIdentifier(identifier);
        if (task == null) {
            throw new RuntimeException("分片任务不存");
        }

        ListPartsRequest listPartsRequest = new ListPartsRequest(task.getBucketName(), task.getObjectKey(), task.getUploadId());
        PartListing partListing = amazonS3.listParts(listPartsRequest);
        List<PartSummary> parts = partListing.getParts();
            if (!task.getChunkNum().equals(parts.size())) {
                // 已上传分块数量与记录中的数量不对应，不能合并分块
                throw new RuntimeException("分片缺失，请重新上传");
            }
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest()
                .withUploadId(task.getUploadId())
                .withKey(task.getObjectKey())
                .withBucketName(task.getBucketName())
                .withPartETags(parts.stream().map(partSummary -> new PartETag(partSummary.getPartNumber(), partSummary.getETag())).collect(Collectors.toList()));
        CompleteMultipartUploadResult result = amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
    }

}

