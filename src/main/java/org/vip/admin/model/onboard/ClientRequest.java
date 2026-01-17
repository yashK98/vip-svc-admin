package org.vip.admin.model.onboard;

import lombok.Data;

@Data
public class ClientRequest {
    private String id;
    private String type;
    private String redirectUris;
}
