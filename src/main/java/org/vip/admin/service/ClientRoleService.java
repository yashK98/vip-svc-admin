package org.vip.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vip.admin.gateway.KeycloakGateway;

@Service
public class ClientRoleService {

    @Autowired
    private KeycloakGateway tokenService;

    public void createClientRole(String clientId, String role) {
        tokenService.createClientRole(clientId, role);
    }
}
