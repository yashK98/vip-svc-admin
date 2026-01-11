package org.vip.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.vip.admin.model.fga.FgaRequest;
import org.vip.admin.service.OpenFgaService;

@Slf4j
@RestController
public class VIPUserController {

    @Autowired
    private OpenFgaService openFgaService;

    @PostMapping("/createUser")
    public void createUser(@RequestBody FgaRequest request){
        openFgaService.writeTuples(request);
    }
}
