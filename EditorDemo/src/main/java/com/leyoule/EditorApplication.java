package com.leyoule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author 45000
 */
@SpringBootApplication
public class EditorApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EditorApplication.class);
    }
    /**
     * port: 11001
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(EditorApplication.class);
    }
}
