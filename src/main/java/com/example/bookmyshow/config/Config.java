package com.example.bookmyshow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;


@Configuration
public class Config {

    @Bean
    public Jedis jedisConnectionFactory() {
        Jedis jedis = new Jedis("redis://localhost:6379");
        //jedis.flushAll();
        return jedis;
    }
}
