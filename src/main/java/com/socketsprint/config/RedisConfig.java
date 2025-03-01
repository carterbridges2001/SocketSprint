package com.socketsprint.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Value("${app.redis.stream.key}")
    private String streamKey;

    @Value("${app.redis.stream.consumer-group}")
    private String consumerGroup;

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext.<String, String>newSerializationContext(new StringRedisSerializer())
                        .build()
        );
    }

    @PostConstruct
    public void createConsumerGroup() {
        reactiveRedisTemplate.opsForStream()
                .createGroup(streamKey, consumerGroup)
                .onErrorResume(e -> {
                    // Group might already exist, which is fine
                    return reactiveRedisTemplate.opsForStream().groups(streamKey)
                            .filter(g -> g.groupName().equals(consumerGroup))
                            .hasElements()
                            .map(exists -> {
                                if (!exists) {
                                    throw new RuntimeException("Failed to create consumer group");
                                }
                                return "OK";
                            });
                })
                .subscribe();
    }
}
