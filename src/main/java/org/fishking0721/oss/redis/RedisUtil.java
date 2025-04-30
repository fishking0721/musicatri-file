package org.fishking0721.oss.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Object EMPTY_OBJECT = new Object();

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //基础操作
    public void set(String key, Object value, long expireMinutes) {
        try {
            redisTemplate.opsForValue().set(key, value, expireMinutes, TimeUnit.MINUTES);
        } catch (DataAccessException e) {
            log.warn("[Redis] set key failed: {}", key, e);
        }
    }

    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (EMPTY_OBJECT.equals(value)) return null; // 命中了空对象
            return value;
        } catch (DataAccessException e) {
            log.warn("[Redis] get key failed: {}", key, e);
            return null;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (DataAccessException e) {
            log.warn("[Redis] delete key failed: {}", key, e);
        }
    }

    //支持缓存空对象防止穿透
    public void cacheEmpty(String key, long expireMinutes) {
        set(key, EMPTY_OBJECT, expireMinutes);
    }
    public boolean isEmptyValue(Object obj) {
        return EMPTY_OBJECT.equals(obj);
    }

    //支持 getOrLoad 模式
    public <T> T getOrLoad(String key, long ttlMinutes, Supplier<T> dbLoader) {
        try {
            Object cache = get(key);
            if (cache != null) return (T) cache;

            T loaded = dbLoader.get();
            if (loaded != null) {
                set(key, loaded, ttlMinutes);
                return loaded;
            } else {
                cacheEmpty(key, ttlMinutes);
                return null;
            }
        } catch (Exception e) {
            log.warn("[Redis] getOrLoad failed: {}", key, e);
            return dbLoader.get();
        }
    }

    //分布式锁
    public boolean tryLock(String lockKey, String lockValue, long expireSeconds) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (DataAccessException e) {
            log.warn("[Redis] tryLock failed: {}", lockKey, e);
            return false;
        }
    }

    public void unlock(String lockKey, String expectedValue) {
        try {
            redisTemplate.execute((RedisCallback<Object>) connection -> {
                byte[] keyBytes = ((StringRedisSerializer) redisTemplate.getKeySerializer()).serialize(lockKey);
                byte[] valueBytes = ((GenericJackson2JsonRedisSerializer) redisTemplate.getValueSerializer()).serialize(expectedValue);
                byte[] actualValue = connection.get(keyBytes);
                if (actualValue != null && java.util.Arrays.equals(actualValue, valueBytes)) {
                    connection.del(keyBytes);
                }
                return null;
            });
        } catch (Exception e) {
            log.warn("[Redis] unlock failed: {}", lockKey, e);
        }
    }
}

