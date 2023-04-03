package com.example.bigfile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.bigfile.model.dto.TaskInfoDTO;
import com.example.bigfile.model.entity.SysUploadTask;
import com.example.bigfile.model.param.InitTaskParam;

import java.util.Map;

/**
 * 分片上传-分片任务记录(SysUploadTask)表服务接口
 *
 * @author mmsong
 * @since 2023/1/31 18:31
 */
public interface SysUploadTaskService extends IService<SysUploadTask> {

    /**
     * 根据md5标识获取分片上传任务
     */
    SysUploadTask getByIdentifier(String identifier);

    /**
     * 初始化一个任务
     */
    TaskInfoDTO initTask(InitTaskParam param);

    /**
     * 获取文件地址
     */
    String getPath(String bucket, String objectKey);

    /**
     * 获取上传进度
     */
    TaskInfoDTO getTaskInfo(String identifier);

    /**
     * 生成预签名上传url
     *
     * @param bucket    桶名
     * @param objectKey 对象的key
     * @param params    额外的参数
     */
    String genPreSignUploadUrl(String bucket, String objectKey, Map<String, String> params);

    /**
     * 合并分片
     */
    void merge(String identifier);

}
