package com.modoria.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProfileController {
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/profile")
    public Map<String, String> getActiveProfile(){
        return Map.of("active Profile", activeProfile);
    }
}
