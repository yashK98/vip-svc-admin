package org.vip.admin.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.vip.admin.gateway.KeycloakTokenGateway;
import tools.jackson.databind.JsonNode;

import java.util.Map;

@Service
public class ClientRoleService {

    private final RestClient client;
    private final KeycloakTokenGateway tokenService;

    public ClientRoleService(@Qualifier("keycloak") RestClient client, KeycloakTokenGateway tokenService) {
        this.client = client;
        this.tokenService = tokenService;
    }

    public void createClientRole(String clientId, String role) {
        // 1. Get client UUID
        JsonNode response = client.get()
                .uri("/admin/realms/vip-admin/clients?clientId=" + clientId)
                .header("Authorization", "Bearer " + tokenService.getToken())
                .retrieve()
                .body(JsonNode.class);

        String uuid = response.get(0).get("id").asText();

        // 2. Create role
        client.post()
              .uri("/admin/realms/vip-admin/clients/" + uuid + "/roles")
              .header("Authorization", "Bearer " + tokenService.getToken())
              .body(Map.of("name", role))
              .retrieve()
              .toBodilessEntity();
    }
}
