package org.vip.admin.service;

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

    // Adding users to Keycloak and openfga
    public void createUsers(KeycloakUser user, String appName) throws FgaInvalidParameterException {
        log.info("User Request :: {}, App Name :: {}", user, appName);
        keycloakGateway.createUser(user, appName);
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
                .apiUrl("http://localhost:8080")
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
}
