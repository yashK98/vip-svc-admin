package org.vip.admin.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.vip.admin.gateway.KeycloakTokenGateway;
import org.vip.admin.model.RoleRequest;

import java.util.Map;

@Service
public class RealmRoleService {

    private final RestClient client;
    private final KeycloakTokenGateway tokenService;

    public RealmRoleService(@Qualifier("keycloak") RestClient client, KeycloakTokenGateway tokenService) {
        this.client = client;
        this.tokenService = tokenService;
    }

    public void createRealmRole(RoleRequest roleRequest) {
        client.post()
                .uri("/admin/realms/vip-admin/roles")
                .header("Authorization", "Bearer " + tokenService.getToken())
                .body(Map.of("name", roleRequest.getName()))
                .retrieve()
                .toBodilessEntity();
    }
}
