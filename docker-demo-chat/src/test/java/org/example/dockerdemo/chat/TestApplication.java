package org.example.dockerdemo.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 测试用Spring Boot应用配置
 */
@SpringBootApplication(scanBasePackages = {
    "org.example.dockerdemo.chat",
    "org.example.dockerdemo.config"
})
@EntityScan(basePackages = {
    "org.example.dockerdemo.chat.entity",
    "org.example.dockerdemo.entity"
})
@EnableJpaRepositories(basePackages = {
    "org.example.dockerdemo.chat.repository"
})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
