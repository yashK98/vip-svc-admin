package org.vip.admin.model.onboard;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppOnboardRequest {
    private String id;
    private String displayName;
    private boolean enabled;
    private Map<String, String> attributes;
    private List<ClientRequest> clients;
}