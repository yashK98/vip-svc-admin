package org.vip.admin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vip.admin.gateway.KeycloakGateway;
import org.vip.admin.gateway.OpenFgaGateway;
import org.vip.admin.model.KeycloakUser;
import org.vip.admin.util.FgaMapper;

@Slf4j
@Service
public class UserService {

    @Autowired
    private KeycloakGateway keycloakGateway;

    @Autowired
    private OpenFgaGateway openFgaGateway;

    // Adding users to Keycloak and openfga
    public void createUsers(KeycloakUser user){
        log.info("User Request :: {}", user);
        keycloakGateway.createUser(user);
        openFgaGateway.writeTuples(FgaMapper.toFgaRequest(user, "document:123"));
    }
}
