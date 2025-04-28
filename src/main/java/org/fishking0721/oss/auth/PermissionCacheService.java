package org.fishking0721.oss.auth;

import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import org.fishking0721.oss.pojo.model.PermissionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PermissionCacheService {

//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public PermissionCacheService(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "auth:permission:token:";

    public PermissionResponse.Data getPermission(String token) {
        return (PermissionResponse.Data) redisTemplate.opsForValue().get(PREFIX + token);
    }

    public void cachePermission(String token, PermissionResponse.Data permissionData, long ttlMinutes) {
        redisTemplate.opsForValue().set(PREFIX + token, permissionData, ttlMinutes, TimeUnit.MINUTES);
    }

    public void clearPermission(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
