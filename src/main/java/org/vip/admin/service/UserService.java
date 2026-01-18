package org.vip.admin.service;

import dev.openfga.sdk.api.client.model.ClientReadRequest;
import dev.openfga.sdk.api.client.model.ClientReadResponse;
import dev.openfga.sdk.api.model.TupleKey;
import org.springframework.beans.factory.annotation.Value;
import org.vip.admin.model.fga.UserDetailsResponse;
import tools.jackson.databind.JsonNode;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.Store;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vip.admin.gateway.KeycloakGateway;
import org.vip.admin.model.KeycloakUser;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserService {

    @Autowired
    private KeycloakGateway keycloakGateway;

    @Autowired
    private OpenFgaClient openFgaClient;

    @Value("${openfga.api-url}")
    private String openFgaUrl;

    // Adding users to Keycloak and openfga
    public void createUsers(KeycloakUser user, String appName) throws FgaInvalidParameterException {
        log.info("User Request :: {}, App Name :: {}", user, appName);
        keycloakGateway.createUser(user, appName);
        /*
        findStoreIdByName(appName).thenAccept(storeId -> {
            log.info("For the AppName :: {}, storeId :: {}", appName, storeId.get());
            try {
                this.addUserToDocument(user.getUsername(),
                        user.getAttributes().getUserRole().get(0),
                        "document123",
                        storeId.get());
            } catch (FgaInvalidParameterException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
           log.info("Error occured while searching for AppName :: {}, Exception :: {}", appName, ex);
           return null;
        });
        */
    }

    public CompletableFuture<Optional<String>> findStoreIdByName(String appName) throws FgaInvalidParameterException {
        return openFgaClient.listStores()
                .thenApply(response -> response.getStores().stream()
                        .filter(store -> store.getName().equals(appName))
                        .map(Store::getId)
                        .findFirst());
    }

    public void addUserToDocument(String username, String role, String documentId, String storeId) throws FgaInvalidParameterException {
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(openFgaUrl)
                .storeId(storeId);

        OpenFgaClient dynamicClient = new OpenFgaClient(config);

        // 1. Prepare the tuple key
        // OpenFGA uses the format type:id
        ClientTupleKey tupleKey = new ClientTupleKey()
                .user("user:" + username)
                .relation(role)
                ._object("document:" + documentId);

        // 3. Create the write request
        ClientWriteRequest request = new ClientWriteRequest()
                .writes(List.of(tupleKey));

        // 4. Execute the call
        dynamicClient.write(request).thenAccept(response -> {
            System.out.println("Relationship successfully written for user: " + username);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public CompletableFuture<UserDetailsResponse> getUserDetails(String username, String appName) throws FgaInvalidParameterException {
        // Run both network calls in parallel
        CompletableFuture<JsonNode> keycloakFuture = CompletableFuture.supplyAsync(() ->
                keycloakGateway.getUser(username, appName));

        CompletableFuture<ClientReadResponse> fgaFuture = getUserFromFga(username, appName);

        // Combine results when both are ready
        return keycloakFuture.thenCombine(fgaFuture, (keycloakNode, fgaResponse) ->
                mapToUserDetails(keycloakNode, fgaResponse)
        );
    }

    private CompletableFuture<ClientReadResponse> getUserFromFga(String username, String appName) throws FgaInvalidParameterException {
        return findStoreIdByName(appName).thenCompose(storeId -> {
            String id = storeId.orElseThrow(() -> new RuntimeException("Store not found: " + appName));

            return executeFgaRead(id, username);
        });
    }

    private CompletableFuture<ClientReadResponse> executeFgaRead(String storeId, String username) {
        try {
            // Re-use or create client with specific store context
            ClientConfiguration config = new ClientConfiguration().apiUrl(openFgaUrl).storeId(storeId);
            OpenFgaClient dynamicClient = new OpenFgaClient(config);

            ClientReadRequest request = new ClientReadRequest().user("user:" + username)._object("document:document123");
            return dynamicClient.read(request);
        } catch (FgaInvalidParameterException e) {
            return CompletableFuture.failedFuture(e);
        }
    }


    private UserDetailsResponse mapToUserDetails(JsonNode keycloakNode, ClientReadResponse fgaResponse) {
        // Keycloak search API returns an array; extract the first user object
        JsonNode user = keycloakNode.isArray() ? keycloakNode.get(0) : keycloakNode;

        // Extract unique relations (roles) from FGA tuples
        List<String> roles = fgaResponse.getTuples().stream()
                .map(tuple -> tuple.getKey().getRelation())
                .distinct()
                .toList();

        // Extract the specific objects (resources) the user has access to
        List<String> objects = fgaResponse.getTuples().stream()
                .map(tuple -> tuple.getKey().getObject())
                .toList();

        // Build the final response object using Lombok Builder
        return UserDetailsResponse.builder()
                .id(user.path("id").asText())
                .username(user.path("username").asText())
                .firstName(user.path("firstName").asText())
                .lastName(user.path("lastName").asText())
                .email(user.path("email").asText())
                .enabled(user.path("enabled").asBoolean())
                .roles(roles)
                .authorizedObjects(objects)
                .build();
    }
}
