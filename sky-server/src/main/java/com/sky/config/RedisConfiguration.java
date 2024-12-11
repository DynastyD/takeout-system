package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.validation.constraints.Size;

/**
 * @ClassName: RedisConfiguration
 * @Package:com.sky.config
 * @Description:
 * @author: Zihao
 * @date: 2024/12/11 - 14:08
 */
@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("Begin to create redis template object");
        //Set the redis connection factory object
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //Set the redis serializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
