package org.vip.admin.controller;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vip.admin.model.KeycloakUser;
import org.vip.admin.model.fga.UserDetailsResponse;
import org.vip.admin.service.UserService;

import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET})
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

    @GetMapping("/getUser")
    private UserDetailsResponse getUser(@RequestParam("username") String username, @RequestParam("appName") String appName) throws FgaInvalidParameterException, ExecutionException, InterruptedException {
        return userService.getUserDetails(username, appName).get();
    }
}
