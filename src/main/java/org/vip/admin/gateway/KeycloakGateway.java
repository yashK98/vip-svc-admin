package org.vip.admin.gateway;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.vip.admin.model.KeycloakUser;
import org.vip.admin.model.RealmRequest;
import tools.jackson.databind.JsonNode;

import static org.vip.admin.util.AdminConstants.*;

@Service
public class KeycloakGateway {

    private final RestClient client;

    @Value("${keycloak.client_id}")
    private String clientId;

    @Value("${keycloak.secret}")
    private String secret;

    public KeycloakGateway(@Qualifier("keycloak") RestClient client) {
        this.client = client;
    }

    public String getToken() {
        return client.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials" +
                        "&client_id="+ clientId +
                        "&client_secret="+ secret)
                .retrieve()
                .body(JsonNode.class)
                .get(ACCESS_TOKEN)
                .asText();
    }

    public void createRealm(RealmRequest realm){
        client.post()
                .uri("/admin/realms")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + this.getToken())
                .body(realm)
                .retrieve()
                .toBodilessEntity();
    }

    public void createClient(String realm,String appUrl) {
        // 1. Sanitize appUrl to remove trailing slash for consistency if needed
        String baseAppUrl = appUrl.endsWith("/") ? appUrl.substring(0, appUrl.length() - 1) : appUrl;

        // 2. Use %s placeholders for dynamic values
        String jsonBody = """
            {
               "clientId": "web-ui-client",
               "enabled": true,
               "clientAuthenticatorType": "none",
               "redirectUris": ["%1$s/*"],
               "webOrigins": ["%1$s"],
               "standardFlowEnabled": true,
               "directAccessGrantsEnabled": true,
               "publicClient": true,
               "protocol": "openid-connect",
               "attributes": {
                 "pkce.code.challenge.method": "S256",
                 "post.logout.redirect.uris": "%1$s/*"
               },
               "defaultClientScopes": ["web-origins", "roles", "profile", "email"]
            }
            """.formatted(baseAppUrl);
        client.post()
                .uri("/admin/realms/{realm}/clients", realm)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + this.getToken())
                .body(jsonBody)
                .retrieve()
                .toBodilessEntity();
    }

    public void createGroup(String realm){
        String jsonBody = """
            {
                "name": "%s"
            }
            """.formatted(realm + "_INTERNAL");
        client.post()
                .uri("/admin/realms/{realm}/groups", realm)
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, BEARER + this.getToken())
                .body(jsonBody)
                .retrieve()
                .toBodilessEntity();
    }

    public void createUser(KeycloakUser user, String realm){
        client.post()
                .uri("/admin/realms/{realm}/users", realm)
                .header("Authorization", "Bearer "+ this.getToken())
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }

    public JsonNode getUser(String username, String appName){
        return client.get()
                .uri("/admin/realms/{realm}/users?username={username}", appName, username)
                .header(AUTHORIZATION, BEARER + this.getToken())
                .retrieve()
                .body(JsonNode.class);
    }
}
