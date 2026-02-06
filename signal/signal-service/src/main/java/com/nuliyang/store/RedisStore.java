package com.nuliyang.store;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisStore {

    private final StringRedisTemplate stringRedisTemplate;


    public void add(String key, String value, long timeout){
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void add(String key, String value){
        stringRedisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
    }

    public void createRoom(String key, String value){
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void delete(String key){
        stringRedisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public void expire(String key, long timeout) {
        // 专门用来重置过期时间，不会影响已经存在的 Value
        stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public void expire(String key) {
        // 专门用来重置过期时间，不会影响已经存在的 Value
        stringRedisTemplate.expire(key, 60, TimeUnit.SECONDS);
    }

}
