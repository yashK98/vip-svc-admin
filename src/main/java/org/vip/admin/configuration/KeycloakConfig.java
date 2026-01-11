package org.vip.admin.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.url}")
    private String keycloakBaseUrl;

    @Bean
    @Qualifier("keycloak")
    public RestClient keycloakRestClient() {
        return RestClient.builder()
                .baseUrl(keycloakBaseUrl)
                .build();
    }
}
