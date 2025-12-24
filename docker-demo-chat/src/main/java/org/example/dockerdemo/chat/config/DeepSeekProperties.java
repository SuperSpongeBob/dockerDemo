package org.example.dockerdemo.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DeepSeek 配置属性
 */
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {

    private Api api = new Api();
    private Prompt prompt = new Prompt();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
    }

    public static class Api {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Prompt {
        private String system;
        private String background;

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }
    }
}
