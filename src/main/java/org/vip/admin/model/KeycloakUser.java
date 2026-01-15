package org.vip.admin.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class KeycloakUser {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled=true;
    private Attributes attributes;

    @Data
    public static class Attributes {
        private List<String> tenantId;
        private List<String> userRole;
        private List<String> status = Collections.singletonList("active");
    }
}
