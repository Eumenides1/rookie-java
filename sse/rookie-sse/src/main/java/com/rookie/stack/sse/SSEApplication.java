package com.rookie.stack.sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author eumenides
 * @description
 * @date 2024/12/3
 */
@SpringBootApplication
@EnableScheduling
public class SSEApplication {
    public static void main(String[] args) {
        SpringApplication.run(SSEApplication.class, args);
    }
}
