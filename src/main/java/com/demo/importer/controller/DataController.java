package com.demo.importer.controller;

import com.demo.importer.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class DataController {


    @Autowired
    private DataService dataService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String sendMessage(@RequestBody String message, @RequestHeader int partition) {
        return dataService.sendMessage(message,partition);
    }
}
