package com.example.bigfile.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author mmsong
 * @since 2023/1/31 18:41
 */
@Data
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfoDTO {

    /**
     * 是否完成上传（是否已经合并分片）
     */
    private boolean finished;

    /**
     * 文件地址
     */
    private String path;

    /**
     * 上传记录
     */
    private TaskRecordDTO taskRecord;
}
