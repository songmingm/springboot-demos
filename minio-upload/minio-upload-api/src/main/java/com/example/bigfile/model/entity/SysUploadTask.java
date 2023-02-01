package com.example.bigfile.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 文件上传任务实体
 *
 * @author mmsong
 * @since 2023/1/31 18:33
 */
@Data
@TableName("sys_upload_task")
@Accessors(chain = true)
public class SysUploadTask implements Serializable {

    private Long id;
    private String uploadId;
    /**
     * md5唯一标识
     */
    private String fileIdentifier;
    private String fileName;
    private String bucketName;
    /**
     * 文件key
     */
    private String objectKey;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 每个分片大小
     */
    private Long chunkSize;

    /**
     * 分片数量
     */
    private Integer chunkNum;
}
