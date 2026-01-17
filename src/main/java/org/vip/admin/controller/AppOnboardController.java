package org.vip.admin.controller;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vip.admin.service.RealmService;

@RestController
public class AppOnboardController {

    @Autowired
    private RealmService realmService;

    /*
    TODO:
        - Create Realm
        - Create Store in OpenFGA
        - Model Registration (Manual)
    */
    @GetMapping("/appOnboard")
    public String createRealm(@RequestParam("realm") String appName) throws FgaInvalidParameterException {
        realmService.createRealm(appName);
        return "Application Onboarded to Keycloak and OpenFGA";
    }
}
