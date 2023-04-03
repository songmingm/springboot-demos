package com.example.bigfile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bigfile.model.entity.SysUploadTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分片上传-分片任务记录(SysUploadTask)表数据库访问层
 *
 * @author mmsong
 * @since 2023/1/31 18:36
 */
@Mapper
public interface SysUploadTaskMapper extends BaseMapper<SysUploadTask> {
}
