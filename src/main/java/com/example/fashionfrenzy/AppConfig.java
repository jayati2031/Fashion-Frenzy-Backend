package com.example.fashionfrenzy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ProductSearch productSearch() {
        return new ProductSearch();
    }
}
