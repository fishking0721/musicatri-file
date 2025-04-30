package org.fishking0721.oss.auth;

import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import org.fishking0721.oss.pojo.model.PermissionResponse;
import org.fishking0721.oss.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PermissionCacheService {
    private final RedisUtil redisUtil;
    public PermissionCacheService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private static final String PREFIX = "auth:permission:token:";
//    @Resource
//    private RedisTemplate<String, Object> redisTemplate;

    public PermissionResponse.Data getPermission(String token) {
        Object cached = redisUtil.get(PREFIX + token);
        if (cached == null) {
            redisUtil.cacheEmpty(PREFIX + token, 1); // 缓存空对象 1分钟
        }
        return (PermissionResponse.Data) cached;
    }

    public void cachePermission(String token, PermissionResponse.Data permissionData, long ttlMinutes) {
        redisUtil.set(PREFIX + token, permissionData, ttlMinutes);
//        redisTemplate.opsForValue().set(PREFIX + token, permissionData, ttlMinutes, TimeUnit.MINUTES);
    }

    public void clearPermission(String token) {
        redisUtil.delete(PREFIX + token);
    }
}
