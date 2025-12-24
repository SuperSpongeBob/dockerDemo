package org.example.dockerdemo.chat.config;

import org.example.dockerdemo.chat.controller.ChatController;
import org.example.dockerdemo.chat.service.DeepSeekService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Chat 模块自动配置
 */
@Configuration
@EnableConfigurationProperties(DeepSeekProperties.class)
@ComponentScan(basePackageClasses = {ChatController.class, DeepSeekService.class})
public class ChatAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate chatRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
