package com.example.bigfile.model.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 分片任务初始化参数
 *
 * @author mmsong
 * @since 2023/1/31 18:38
 */
@Data
public class InitTaskParam {

    /**
     * 文件唯一标识(MD5)
     */
    @NotBlank(message = "文件标识不能为空")
    private String identifier;

    /**
     * 文件大小（byte）
     */
    @NotNull(message = "文件大小不能为空")
    private Long totalSize;

    /**
     * 分片大小（byte）
     */
    @NotNull(message = "分片大小不能为空")
    private Long chunkSize;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;
}
