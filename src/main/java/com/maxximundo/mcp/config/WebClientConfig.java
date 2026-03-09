package com.maxximundo.mcp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${lhia.api.base-url}")
    private String lhiaBaseUrl;

    @Value("${maxximundo.api.base-url}")
    private String maxximundoBaseUrl;

    @Bean
    public WebClient lhiaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(lhiaBaseUrl)
                .build();
    }

    @Bean
    public WebClient maxximundoWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(maxximundoBaseUrl)
                .build();
    }
}
