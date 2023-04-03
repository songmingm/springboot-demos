package com.example.bigfile.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.amazonaws.services.s3.model.PartSummary;
import com.example.bigfile.model.entity.SysUploadTask;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author mmsong
 * @since 2023/1/31 18:41
 */
@Data
@Accessors(chain = true)
public class TaskRecordDTO extends SysUploadTask {

    /**
     * 已上传完的分片
     */
    private List<PartSummary> exitPartList;

    public static TaskRecordDTO convertFromEntity(SysUploadTask task) {
        TaskRecordDTO dto = new TaskRecordDTO();
        BeanUtil.copyProperties(task, dto);
        return dto;
    }
}
