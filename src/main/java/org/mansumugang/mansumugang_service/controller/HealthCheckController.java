package org.mansumugang.mansumugang_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/server")
public class HealthCheckController {
    @Value("${serverName}") // application.yml의 값들을 매핑시킴
    private String serverName;
    @Value("${server.env}")
    private String env;
    private Integer visitedCount = 0;

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getServerInfo() {
        visitedCount++;

        Map<String, String> serverInfo = new HashMap<>();
        serverInfo.put("ServerName:", serverName);
        serverInfo.put("env:", env);

        return ResponseEntity.ok(serverInfo);
    }

    @GetMapping("/env")
    public String getEnv() {
        return env;
    }
}

