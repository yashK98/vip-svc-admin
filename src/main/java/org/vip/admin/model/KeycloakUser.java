package org.vip.admin.model;

import lombok.Data;

import java.util.List;

@Data
public class KeycloakUser {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled=true;
    private Attributes attributes;
    private List<String> groups;

    @Data
    public static class Attributes {
        private List<String> userRole;
    }
}
