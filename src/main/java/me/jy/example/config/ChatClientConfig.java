package me.jy.example.config;

import org.springframework.ai.chat.client.ChatClientCustomizer;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClientCustomizer chatClientCustomizer() {
        return builder -> builder.defaultAdvisors(new SimpleLoggerAdvisor());
    }
}
