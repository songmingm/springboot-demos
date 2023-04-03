package com.example.lockdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lockdemo.pojo.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {


    @Update("update stock set count=count-1 where id = #{id} and count>=1")
    int deductOneSQL(Long id);

    @Select("select * from stock where id = #{id} for update")
    List<Stock> selectStock(Long id);
}
