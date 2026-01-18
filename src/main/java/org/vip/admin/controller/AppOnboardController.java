package org.vip.admin.controller;

import dev.openfga.sdk.errors.FgaInvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vip.admin.model.onboard.AppOnboardRequest;
import org.vip.admin.model.onboard.Tenant;
import org.vip.admin.service.AppOnboardService;

@Slf4j
@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.POST})
public class AppOnboardController {

    @Autowired
    private AppOnboardService appOnboardService;

    @PostMapping(value = "/appOnboard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createRealm(@RequestPart("appOnboardRequest") AppOnboardRequest appOnboardRequest, @RequestPart(value = "file", required = false) MultipartFile file) throws FgaInvalidParameterException {
        appOnboardService.appOnboard(appOnboardRequest, file);
        return "Application Onboarded to Keycloak and OpenFGA";
    }

    @PostMapping(value = "/tenantOnboard")
    public String tenantOnboard(@RequestBody Tenant appOnboardRequest) throws FgaInvalidParameterException {
        appOnboardService.tenantOnboard(appOnboardRequest);
        return "Application Onboarded to Keycloak and OpenFGA";
    }

    @PostMapping("/stores/{storeId}/models")
    public String uploadModel(@PathVariable String storeId, @RequestParam("file") MultipartFile file) throws Exception {
        appOnboardService.uploadModel(storeId, file);
        return "Model Registered to OpenFGA";
    }
}
