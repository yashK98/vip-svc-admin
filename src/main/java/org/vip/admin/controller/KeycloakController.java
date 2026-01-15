package org.vip.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vip.admin.model.KeycloakUser;
import org.vip.admin.model.RoleRequest;
import org.vip.admin.service.ClientRoleService;
import org.vip.admin.service.RealmRoleService;
import org.vip.admin.service.UserService;

@RestController("/roles")
public class KeycloakController {

    @Autowired
    private RealmRoleService realmRoleService;

    @Autowired
    private ClientRoleService clientRoleService;

    @Autowired
    private UserService userService;

    @PostMapping("/createRealmRole")
    public String createRealmRole(@RequestBody RoleRequest roleRequest){
        realmRoleService.createRealmRole(roleRequest);
        return "Realm Role Created";
    }

    @PostMapping("/createClientRole")
    private String createClientRole(@RequestParam("clientId") String clientId, @RequestParam("roleName") String roleName){
        clientRoleService.createClientRole(clientId, roleName);
        return "Client Role Created";
    }

    @PostMapping("/createUsers")
    private String createUsers(@RequestBody KeycloakUser user){
        userService.createUsers(user);
        return "";
    }
}
