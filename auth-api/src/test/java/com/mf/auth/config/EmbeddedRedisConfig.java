package com.mf.auth.config;

import com.mf.auth.boot.config.properties.SpringRedisProperties;

import java.io.IOException;
import javax.annotation.PostConstruct;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisConfig {

    @Autowired
    public RedisConnectionFactory redisConnectionFactory;

    private RedisServer server;

    public EmbeddedRedisConfig(SpringRedisProperties properties) throws IOException {
        this.server = new RedisServer(properties.getPort());
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        server.start();
        redisConnectionFactory.getConnection().flushAll();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        server.stop();
    }
}