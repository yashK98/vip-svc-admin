package org.vip.admin.service;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.CreateStoreResponse;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vip.admin.gateway.KeycloakGateway;
import org.vip.admin.model.RealmRequest;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RealmService {

    @Autowired
    private KeycloakGateway keycloakGateway;

    @Autowired
    private OpenFgaClient openFgaClient;

    public void createRealm(String appName) throws FgaInvalidParameterException {
        keycloakGateway.createRealm(this.getRealmRequest(appName));
        createNewStore(appName).thenAccept(storeId -> {
            log.info("Successfully created OpenFGA store for app: {} with ID: {}", appName, storeId);
        }).exceptionally(ex -> {
            log.error("Failed to create OpenFGA store for app: {}", appName, ex);
            return null;
        });
    }

    public CompletableFuture<String> createNewStore(String storeName) throws FgaInvalidParameterException {
        CreateStoreRequest request = new CreateStoreRequest().name(storeName);
        return openFgaClient.createStore(request)
                .thenApply(CreateStoreResponse::getId);
    }

    private RealmRequest getRealmRequest(String realm){
        RealmRequest realmRequest = new RealmRequest();
        realmRequest.setRealm(realm);
        realmRequest.setEnabled(true);
        realmRequest.setDisplayName(realm);
        return realmRequest;
    }
}
