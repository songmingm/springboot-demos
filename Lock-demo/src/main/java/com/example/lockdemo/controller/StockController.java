package com.example.lockdemo.controller;

import com.example.lockdemo.service.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stock")
public class StockController {

    @Autowired
    private StockServiceImpl stockService;

    @GetMapping("deduct/{id}")
    public void deduct(@PathVariable Long id) {
        this.stockService.deduct6(id);
    }

    @GetMapping("count/{id}")
    public String count(@PathVariable Long id) {
        Integer stockCount = this.stockService.getStockCount(id);
        return "商品库存：" + stockCount;
    }
}
