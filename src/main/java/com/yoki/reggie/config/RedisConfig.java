package com.yoki.reggie.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-23 01:11
 */
@Configuration
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开启redisTemplate...");

        //为方便开发，一般直接使用<String,Object>
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        //连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        //定义json序列化器，序列化任意的对象
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        //String序列化配置
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //key使用String的序列化方式(key,value)
        template.setKeySerializer(stringRedisSerializer);
        //hash的key也使用String的序列化方式(key,<field,value>)
        template.setHashKeySerializer(stringRedisSerializer);
        //value使用json的序列化方式(key,value)
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash的value也使用json的序列化方式(key,<field,value>)
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }

}
