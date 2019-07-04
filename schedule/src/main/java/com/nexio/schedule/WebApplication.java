package com.nexio.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"application.properties", "quartz.properties", "jdbc.properties"})
public class WebApplication {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    public static void main(String args[]){
        //執行SpringApplication
        SpringApplication.run(WebApplication.class, args);
    }

}
