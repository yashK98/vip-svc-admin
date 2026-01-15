package org.vip.admin.gateway;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.vip.admin.model.KeycloakUser;
import org.vip.admin.model.RoleRequest;
import tools.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.Map;

@Service
public class KeycloakGateway {

    private final RestClient client;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client_id}")
    private String clientid;

    @Value("${keycloak.secret}")
    private String secret;

    public KeycloakGateway(@Qualifier("keycloak") RestClient client) {
        this.client = client;
    }

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

    public void createRealmRole(RoleRequest roleRequest) {
        client.post()
                .uri("/admin/realms/{realm}/roles", realm)
                .header("Authorization", "Bearer " + this.getToken())
                .body(Map.of("name", roleRequest.getName()))
                .retrieve()
                .toBodilessEntity();
    }

    public void createClientRole(String clientId, String role) {
        // 1. Get client UUID
        JsonNode response = client.get()
                .uri("/admin/realms/{realm}/clients?clientId={clientId}", realm, clientId)
                .header("Authorization", "Bearer " + this.getToken())
                .retrieve()
                .body(JsonNode.class);

        String uuid = response.get(0).get("id").asText();

        // 2. Create role
        client.post()
                .uri("/admin/realms/{realm}/clients/{uuid}/roles", realm, uuid)
                .header("Authorization", "Bearer " + this.getToken())
                .body(Map.of("name", role))
                .retrieve()
                .toBodilessEntity();
    }

    public void createUser(KeycloakUser user){
        user.getAttributes().setTenantId(Collections.singletonList(realm));
        client.post()
                .uri("/admin/realms/{realm}/users", realm)
                .header("Authorization", "Bearer "+ this.getToken())
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }
}
