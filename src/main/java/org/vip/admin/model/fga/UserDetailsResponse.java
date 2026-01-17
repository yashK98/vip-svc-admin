package org.vip.admin.model.fga;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserDetailsResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    
    // This will hold the 'relation' values from OpenFGA (e.g., ["editor"])
    private List<String> roles;
    
    // This holds the full object context from OpenFGA (e.g., ["document:document123"])
    private List<String> authorizedObjects;
}