package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Controller
public class MainController {

    private static final int THREADS_SIZE = 20;
    private static final int CAPACITY = 1;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayStartPage() {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(THREADS_SIZE, THREADS_SIZE, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(CAPACITY));
//        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        Date a = new Date();
        System.out.println(a.getTime());
        for (Integer i = 0; i < 100000; ) {
            pool.execute(new Thread(new TestRedis(i++)));
        }
        System.out.println(new Date().getTime() - a.getTime());
        pool.shutdown();
        return "321";
    }

    private class TestRedis extends Thread {
        private Integer key;

        TestRedis(Integer key) {
            this.key = key;
        }

        @Override
        public void run() {
            stringRedisTemplate.opsForValue().set(key.toString(), UUID.randomUUID().toString());
        }
    }
}