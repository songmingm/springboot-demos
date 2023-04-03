package com.example.lockdemo.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("stock")
public class Stock {

    @TableId
    private Long id;
    private String productCode;
    private String warehouse;
    private Integer count;
    private Integer version;
}
