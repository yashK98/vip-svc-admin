package org.vip.admin.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenFgaConfig {

    @Value("${openfga.url}")
    private String openfgaBaseUrl;

    @Bean
    @Qualifier("openfga")
    public RestClient openFgaClient() {
        return RestClient.builder()
                .baseUrl(openfgaBaseUrl)
                .build();
    }
}
