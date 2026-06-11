package com.hotelpricing.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.JdkClientHttpRequestFactory;

/**
 * Customizes the RestClient used by Spring AI OpenAI.
 * Uses java.net.http.HttpClient (JDK 11+) to avoid HttpURLConnection
 * streaming-mode retry issues on 401, and sets read timeout to 300s
 * for long-running LLM responses.
 */
@Configuration
public class RestClientConfig {

    @Value("${app.http.connect-timeout:10s}")
    private Duration connectTimeout;

    @Value("${app.http.read-timeout:300s}")
    private Duration readTimeout;

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public RestClientCustomizer timeoutRestClientCustomizer() {
        return restClientBuilder -> {
            var httpClient = java.net.http.HttpClient.newBuilder()
                    .connectTimeout(connectTimeout)
                    .build();
            var factory = new JdkClientHttpRequestFactory(httpClient);
            factory.setReadTimeout(readTimeout);
            restClientBuilder.requestFactory(factory);
        };
    }
}
