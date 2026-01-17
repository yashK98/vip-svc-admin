package org.vip.admin.model;

import lombok.Data;

@Data
public class RealmRequest {
    private String realm;
    private Boolean enabled;
    private String displayName;
}
