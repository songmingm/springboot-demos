package com.example.bigfile.utils;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * minio 文件上传工具
 *
 * @author mmsong
 * @since 2023/1/31 18:05
 */
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    /**
     * 单文件上传
     */
    public void putObjectInBucket(String bucketName, MultipartFile file, HashMap<String, String> meta, String objectName) {
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .userMetadata(meta)
                    .build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }

    /**
     * 根据字节流上传，一般用于缩略图上传
     *
     * @param bucketName  文件桶
     * @param inputStream 文件流
     * @param size        流大小
     * @param objectName  文件名
     */
    public void putObjectInBucket(String bucketName, InputStream inputStream, long size, String objectName) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }


    /**
     * 图片预览
     */
    public String preview(String filename, String bucketName) {
        try {
            GetPresignedObjectUrlArgs objectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .method(Method.GET)
                    .build();
            return minioClient.getPresignedObjectUrl(objectUrlArgs);
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }

    /**
     * 查看某个桶内的文件信息
     */
    public List<Item> getObjectInBucket(String bucketName) {
        ListObjectsArgs listObjectsArgs = ListObjectsArgs
                .builder()
                .bucket(bucketName)
                .build();
        Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            throw new RuntimeException("Minio Files GetByBucket Error：" + e.getMessage());
        }
        return items;
    }

    /**
     * 删除某个文件
     */
    public void deleteObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }

    /**
     * 查看存储bucket是否存在
     */
    public Boolean isExistsBucket(String bucketName) {
        boolean isExist;
        try {
            isExist = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
        return isExist;
    }

    /**
     * 创建存储bucket
     */
    public void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }


    /**
     * 删除存储bucket
     */
    public void deleteBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }

    /**
     * 获取文件的元数据信息
     */
    public Map<String, String> getMetaData(String bucketName, String objectName) {
        try {
            StatObjectResponse statObject = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return statObject.userMetadata();
        } catch (Exception e) {
            throw new RuntimeException("Minio Error：" + e.getMessage());
        }
    }
}
