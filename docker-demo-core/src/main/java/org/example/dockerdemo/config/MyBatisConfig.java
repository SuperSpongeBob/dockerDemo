package org.example.dockerdemo.config;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {

    @Bean
    public Interceptor logicDeleteInterceptor() {
        return new LogicDeleteInterceptor();
    }

    @Bean
    public Interceptor autoFillInterceptor() {
        return new AutoFillInterceptor();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        };
    }
}
