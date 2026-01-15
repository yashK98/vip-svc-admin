package org.vip.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vip.admin.gateway.KeycloakGateway;
import org.vip.admin.model.RoleRequest;

@Service
public class RealmRoleService {

    @Autowired
    private KeycloakGateway keycloakGateway;

    public void createRealmRole(RoleRequest roleRequest) {
        keycloakGateway.createRealmRole(roleRequest);
    }
}
