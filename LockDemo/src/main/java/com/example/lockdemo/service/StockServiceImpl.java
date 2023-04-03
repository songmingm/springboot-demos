package com.example.lockdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.lockdemo.mapper.StockMapper;
import com.example.lockdemo.pojo.Stock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class StockServiceImpl {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 不加锁的情况
     * 例如库存数量为5000
     * 当事务A查询到库存数量时（5000），开始扣减库存（4999）
     * 此时事务B也查询库存数量（5000），开始扣减库存（4999），并比事务A先提交（此时库存数量设置为4999）
     * 事务A在提交事务，设置库存（4999），但是实际扣减了两次，库存应为（4998），造成了超卖
     */
    public void deduct0(Long id) {
        Stock stock = this.stockMapper.selectById(id);
        stock.setCount(stock.getCount() - 1);
        this.stockMapper.updateById(stock);
    }

    /**
     * 第一种：
     * JVM本地锁 ReentrantLock
     */
    public void deduct1(Long id) {
        lock.lock();
        try {
            LambdaQueryWrapper<Stock> select = Wrappers.<Stock>lambdaQuery()
                    .eq(Stock::getId, id);
            Stock stock = this.stockMapper.selectOne(select);
            if (stock.getCount() > 0) {
                stock.setCount(stock.getCount() - 1);
                this.stockMapper.updateById(stock);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 第二种：
     * 使用一条sql实现：update stock set count=count-1 where id = #{id} and count>=1
     * 原理是在修改库存时才获取库存的数量，保证查询库存和扣减库存的原子性
     */
    public void deduct2(Long id) {
        this.stockMapper.deductOneSQL(id);
    }

    /**
     * 第三种：
     * 悲观锁实现（select ... for update）
     * 在数据库事务执行的过程中，就会锁定查询出来的数据，其他事务将不能再对其进行读写，单个请求直至数据库事务完成，才会释放这个锁
     * 注意：使用悲观锁必须关闭MySQL的自动提交属性 set autocommit=0
     * 这里开始手动事务
     */
    @Transactional
    public void deduct3(Long id) {
        List<Stock> stocks = this.stockMapper.selectStock(id);
        // 选择合适的库存，这里直接取集合第一个数据
        Stock stock = stocks.get(0);
        if (stock != null && stock.getCount() > 0) {
            stock.setCount(stock.getCount() - 1);
            this.stockMapper.updateById(stock);
        }
    }

    /**
     * 第四种：
     * 使用乐观锁（借助于时间戳，version版本号字段 CAS机制）
     * 这里使用手动事务，会导致事务连接超时
     */
    public void deduct4(Long id) {
        LambdaQueryWrapper<Stock> select = Wrappers.<Stock>lambdaQuery()
                .eq(Stock::getId, id);
        List<Stock> stocks = this.stockMapper.selectList(select);
        Stock stock = stocks.get(0);
        if (stock != null && stock.getCount() > 0) {
            stock.setCount(stock.getCount() - 1);
            Integer version = stock.getVersion();
            stock.setVersion(version + 1);
            LambdaUpdateWrapper<Stock> update = Wrappers.<Stock>lambdaUpdate()
                    .eq(Stock::getVersion, version)
                    .eq(Stock::getId, id);
            int flag = this.stockMapper.update(stock, update);
            if (flag == 0) {
                // 更新失败重试
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.deduct4(id);
            }
        }
    }

    /**
     * 第五种：
     * 使用redis实现setnx实现
     * 线程进来设置lock值，只有lock不存在时可以设置成功
     * 成功即相当于拿到这个lock锁，可以执行扣减操作，未获取锁的线程休眠等待其他线程释放锁再次争抢
     */
    public void deduct5(Long id) {
        String uuid = UUID.randomUUID().toString();
        // 未获取锁的进程在这里睡眠等待
        while (Boolean.FALSE.equals(this.stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS))) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            String stock = this.stringRedisTemplate.opsForValue().get("stock");
            if (ObjectUtils.isNotEmpty(stock)) {
                int count = Integer.parseInt(stock);
                if (count > 0) {
                    this.stringRedisTemplate.opsForValue().decrement("stock");
                }
            }
        } finally {
            // 判断是否是是自己的锁，防止误删除
            // 这里使用Lua脚本保证查询锁和删除锁之间的原子性
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                    "then return redis.call('del',KEYS[1]) " +
                    "else return 0 " +
                    "end";
            DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(script, Boolean.class);
            List<String> keys = Arrays.asList("lock");
            this.stringRedisTemplate.execute(redisScript, keys, uuid);
        }
    }

    /**
     * 第六种：redisson
     */
    public void deduct6(Long id) {
        RLock lock = this.redissonClient.getFairLock("lock");
        lock.lock();
        try {
            String stock = this.stringRedisTemplate.opsForValue().get("stock");
            if (ObjectUtils.isNotEmpty(stock)) {
                int count = Integer.parseInt(stock);
                if (count > 0)
                    this.stringRedisTemplate.opsForValue().decrement("stock");

            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查询商品库存数量
     */
    public Integer getStockCount(Long id) {

        LambdaQueryWrapper<Stock> select = Wrappers.<Stock>lambdaQuery()
                .select(Stock::getCount)
                .eq(Stock::getId, id);
        Stock s = this.stockMapper.selectOne(select);
        return s.getCount();
    }

}
