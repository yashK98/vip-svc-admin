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

    public void createClient(String realm, String clientId) {
        // Define the client configuration in JSON format
        String jsonBody = """
        {
            "clientId": "%s",
            "enabled": true,
            "protocol": "openid-connect",
            "publicClient": false,
            "redirectUris": []
        }
        """.formatted(clientId);
        client.post()
                .uri("/admin/realms/{realm}/clients", realm)
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
}
