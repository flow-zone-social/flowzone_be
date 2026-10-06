package com.fsocial.auth.internal.service;

import com.fsocial.auth.internal.service.BanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BanServiceImpl implements BanService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void ban(String token) {
        redisTemplate.opsForValue().set("banned:" + token, token, 1, TimeUnit.DAYS);
    }

    @Override
    public void unBan(String token) {
        redisTemplate.delete("banned:" + token);
    }
}
