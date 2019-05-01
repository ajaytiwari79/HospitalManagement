package com.kairos.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by anil on 10/8/17.
 */

@Configuration
@PropertySource({"classpath:application-${spring.profiles.active}.properties"})
public class RedisConfig {

    @Value("${spring.redis.hostname}")
    private String redisHostName;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String password;


    @Bean
    public RedisTemplate<String, Map<String, String>> redisTemplateUser(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Map<String, String>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisConnectionFactory redisConnectionFactory() throws UnknownHostException {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setPassword(RedisPassword.of(password));
        return new JedisConnectionFactory(standaloneConfiguration);
    }

}
