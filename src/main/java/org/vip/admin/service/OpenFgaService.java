package org.vip.admin.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.vip.admin.model.fga.FgaRequest;

@Service
public class OpenFgaService {

    private final RestClient client;

    @Value("${openfga.store-id}")
    private String storeId;

    public OpenFgaService(@Qualifier("openfga") RestClient client) {
        this.client = client;
    }

    public void writeTuples(FgaRequest request) {
        client.post()
              .uri("/stores/{storeId}/write", storeId)
              .contentType(MediaType.APPLICATION_JSON)
              .body(request)
              .retrieve()
              .toBodilessEntity();
    }
}
