package com.example.bigfile.model.dto;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author mmsong
 * @since 2023/1/31 19:52
 */

@Data
@Accessors(chain = true)
public class ObjectInfo {

    /**
     * 所在桶
     */
    private String bucket;

    /**
     * 对象的key
     */
    private String objectKey;

    /**
     * 文件地址
     */
    private String path;
}

