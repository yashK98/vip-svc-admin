package org.vip.admin.service;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.CreateStoreResponse;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelResponse;
import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vip.admin.gateway.KeycloakGateway;
import org.vip.admin.model.RealmRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.vip.admin.model.onboard.AppOnboardRequest;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AppOnboardService {

    @Autowired
    private KeycloakGateway keycloakGateway;

    @Autowired
    private OpenFgaClient openFgaClient;

    @Value("${openfga.api-url}")
    private String openFgaUrl;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public void appOnboard(AppOnboardRequest appOnboardRequest, MultipartFile file) throws FgaInvalidParameterException {
        keycloakGateway.createRealm(this.getRealmRequest(appOnboardRequest.getId()));
        appOnboardRequest.getClients().stream().forEach(client -> keycloakGateway.createClient(appOnboardRequest.getId(), client.getId()));
        createNewStore(appOnboardRequest.getId()).thenAccept(storeId -> {
            log.info("Successfully created OpenFGA store for app: {} with ID: {}", appOnboardRequest.getId(), storeId);
            try {
                if(!file.isEmpty()){
                    this.uploadModel(storeId, file);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            log.error("Failed to create OpenFGA store for app: {}", appOnboardRequest.getId(), ex);
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

    public CompletableFuture<String> uploadModel(String storeId, MultipartFile file) throws Exception {

        // 1. Read JSON file bytes and parse to OpenFGA Request Object
        byte[] bytes = file.getBytes();
        WriteAuthorizationModelRequest request = objectMapper.readValue(bytes, WriteAuthorizationModelRequest.class);

        log.info("Registering new model for store: {}", storeId);

        // 2. Initialize a client for this specific store
        // (Since storeId is dynamic, we create a contextual client)
        ClientConfiguration config = new ClientConfiguration()
                .apiUrl(openFgaUrl)
                .storeId(storeId);

        OpenFgaClient client = new OpenFgaClient(config);

        // 3. Write the model and return the new Authorization Model ID
        return client.writeAuthorizationModel(request)
                .thenApply(WriteAuthorizationModelResponse::getAuthorizationModelId)
                .thenApply(id -> {
                    log.info("Model registered successfully. ID: {}", id);
                    return id;
                });
    }
}
