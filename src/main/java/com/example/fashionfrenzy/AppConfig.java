package com.example.fashionfrenzy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining Spring beans.
 */
@Configuration
// public class starts from here
public class AppConfig {

    /**
     * Bean definition method for creating a ProductSearch instance.
     *
     * @return ProductSearch instance
     */
    @Bean
    public ProductSearch productSearch() {
        return new ProductSearch();
    }
}
