package com.modoria.shared.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public String checkHealth() {
        return "✅ Modoria backend is running successfully!";
    }
}
