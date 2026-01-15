package org.vip.admin.util;

import org.vip.admin.model.KeycloakUser;
import org.vip.admin.model.fga.FgaRequest;
import org.vip.admin.model.fga.User;
import org.vip.admin.model.fga.UserTuple;

import java.util.Collections;

public class FgaMapper {

    private static final String USER_PREFIX = "user:";

    public static FgaRequest toFgaRequest(KeycloakUser keycloakUser, String objectId) {

        if (keycloakUser == null) {
            throw new IllegalArgumentException("KeycloakUser cannot be null");
        }

        if (keycloakUser.getAttributes() == null
                || keycloakUser.getAttributes().getUserRole() == null
                || keycloakUser.getAttributes().getUserRole().isEmpty()) {
            throw new IllegalArgumentException("User role is mandatory");
        }

        User userTuple = new User();
        userTuple.setUser(USER_PREFIX + keycloakUser.getUsername().toLowerCase());
        userTuple.setRelation(keycloakUser.getAttributes().getUserRole().get(0));
        userTuple.setObject(objectId);

        UserTuple userTupleWrapper = new UserTuple();
        userTupleWrapper.setTupleKeys(Collections.singletonList(userTuple));

        FgaRequest fgaRequest = new FgaRequest();
        fgaRequest.setWrites(userTupleWrapper);

        return fgaRequest;
    }
}
