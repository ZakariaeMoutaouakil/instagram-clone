package com.instagram.demo.utils;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonWrapper {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
