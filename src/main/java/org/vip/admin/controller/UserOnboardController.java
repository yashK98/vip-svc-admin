package org.vip.admin.controller;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vip.admin.model.KeycloakUser;
import org.vip.admin.service.UserService;

@RestController
public class UserOnboardController {

    @Autowired
    private UserService userService;

    /*
        TODO:
            - Onboard User to Keycloak to specific Realm
            - Onboard User to OpenFGA based on appName
    */
    @PostMapping("/userOnboard")
    private String createUsers(@RequestBody KeycloakUser user, @RequestParam("appName") String appName) throws FgaInvalidParameterException {
        userService.createUsers(user, appName);
        return "User Added to Keycloak and OpenFGA";
    }
}
