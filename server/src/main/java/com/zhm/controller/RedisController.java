package com.zhm.controller;

import com.zhm.util.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/redis")
public class RedisController {

    @Resource
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/value",method = RequestMethod.GET)
    public Result setValue(@RequestParam(value = "key") String key,@RequestParam(value = "value") String value){
        redisTemplate.opsForValue().set(key,value);
        return Result.sendSuccess("操作redis成功",redisTemplate.opsForValue().get(key));
    }
}
