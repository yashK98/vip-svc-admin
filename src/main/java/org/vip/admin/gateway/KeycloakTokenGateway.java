package org.vip.admin.gateway;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class KeycloakTokenGateway {

    private final RestClient client;

    public KeycloakTokenGateway(@Qualifier("openfga") RestClient client) {
        this.client = client;
    }

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.realm}")
    private String clientid;

    @Value("${keycloak.secret}")
    private String secret;

    public String getToken() {
        return client.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials" +
                        "&client_id="+ clientid +
                        "&client_secret="+ secret)
                .retrieve()
                .body(JsonNode.class)
                .get("access_token")
                .asText();
    }
}
