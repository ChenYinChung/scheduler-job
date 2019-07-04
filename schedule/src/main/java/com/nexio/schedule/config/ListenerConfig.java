package com.nexio.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerConfig {

    @Bean
    public QuartJobSchedulingListener applicationStartListener(){
        return new QuartJobSchedulingListener();
    }
}