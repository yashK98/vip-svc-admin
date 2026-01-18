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

    public void createClient(String realm,String appurl) {
        // Define the client configuration in JSON format
        String jsonBody = """
                {
                   "clientId": "web-ui-client",
                   "name": "",
                   "description": "",
                   "rootUrl": "",
                   "adminUrl": "",
                   "baseUrl": "",
                   "surrogateAuthRequired": false,
                   "enabled": true,
                   "alwaysDisplayInConsole": false,
                   "clientAuthenticatorType": "none",
                   "redirectUris": ["http://localhost:5173/*"],
                   "webOrigins": ["http://localhost:5173"],
                   "notBefore": 0,
                   "bearerOnly": false,
                   "consentRequired": false,
                   "standardFlowEnabled": true,
                   "implicitFlowEnabled": false,
                   "directAccessGrantsEnabled": true,
                   "serviceAccountsEnabled": false,
                   "publicClient": true,
                   "frontchannelLogout": false,
                   "protocol": "openid-connect",
                   "attributes": {
                     "logout.confirmation.enabled": "false",
                     "realm_client": "false",
                     "oidc.ciba.grant.enabled": "false",
                     "client.secret.creation.time": "1768654867",
                     "backchannel.logout.session.required": "true",
                     "standard.token.exchange.enabled": "false",
                     "oauth2.device.authorization.grant.enabled": "false",
                     "display.on.consent.screen": "false",
                     "use.jwks.url": "false",
                     "pkce.code.challenge.method": "S256",
                     "backchannel.logout.revoke.offline.tokens": "false",
                     "dpop.bound.access.tokens": "false",
                     "login_theme": "",
                     "consent.screen.text": "",
                     "backchannel.logout.url": "",
                     "post.logout.redirect.uris": "http://localhost:5173/*"
                   },
                   "authenticationFlowBindingOverrides": {},
                   "fullScopeAllowed": true,
                   "nodeReRegistrationTimeout": -1,
                   "defaultClientScopes": [
                     "web-origins",
                     "acr",
                     "roles",
                     "profile",
                     "basic",
                     "email"
                   ],
                   "optionalClientScopes": [
                     "address",
                     "phone",
                     "organization",
                     "offline_access",
                     "microprofile-jwt"
                   ],
                   "access": {
                     "view": true,
                     "configure": true,
                     "manage": true
                   },
                   "authorizationServicesEnabled": false
                }
                """;
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

    public JsonNode getUser(String username, String appName){
        return client.get()
                .uri("/admin/realms/{realm}/users?username={username}", appName, username)
                .header(AUTHORIZATION, BEARER + this.getToken())
                .retrieve()
                .body(JsonNode.class);
    }
}
