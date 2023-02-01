package com.example.bigfile.controller;

import com.example.bigfile.model.dto.Result;
import com.example.bigfile.model.dto.TaskInfoDTO;
import com.example.bigfile.model.entity.SysUploadTask;
import com.example.bigfile.model.param.InitTaskParam;
import com.example.bigfile.service.SysUploadTaskService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 分片上传-分片任务记录(SysUploadTask)表控制层
 *
 * @author mmsong
 * @since 2023/1/31 18:15
 */
@RestController
@RequestMapping("v1/minio/tasks")
public class MinioUploadTaskController {

    /**
     * 服务对象
     */
    @Resource
    private SysUploadTaskService sysUploadTaskService;


    /**
     * 获取上传进度
     * @param identifier 文件md5
     */
    @GetMapping("{identifier}")
    public Result<TaskInfoDTO> taskInfo (@PathVariable("identifier") String identifier) {
        return Result.ok(sysUploadTaskService.getTaskInfo(identifier));
    }

    /**
     * 创建一个上传任务
     */
    @PostMapping
    public Result<TaskInfoDTO> initTask (@Valid @RequestBody InitTaskParam param, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        return Result.ok(sysUploadTaskService.initTask(param));
    }

    /**
     * 获取每个分片的预签名上传地址
     */
    @GetMapping("/{identifier}/{partNumber}")
    public Result<?> preSignUploadUrl (@PathVariable("identifier") String identifier, @PathVariable("partNumber") Integer partNumber) {
        SysUploadTask task = sysUploadTaskService.getByIdentifier(identifier);
        if (task == null) {
            return Result.error("分片任务不存在");
        }
        Map<String, String> params = new HashMap<>();
        params.put("partNumber", partNumber.toString());
        params.put("uploadId", task.getUploadId());
        return Result.ok(sysUploadTaskService.genPreSignUploadUrl(task.getBucketName(), task.getObjectKey(), params));
    }

    /**
     * 合并分片
     */
    @PostMapping("/merge/{identifier}")
    public Result<?> merge (@PathVariable("identifier") String identifier) {
        sysUploadTaskService.merge(identifier);
        return Result.ok();
    }

}
