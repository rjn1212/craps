package com.gitgames.craps.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/platform")
public class PlatformController {

    private final Instant startTime = Instant.now();

    @GetMapping("/health")
    public Map<String, Object> health() {
        long uptime = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        return Map.of(
            "status", "ok",
            "game", "craps",
            "version", "1.0.0",
            "uptime", uptime
        );
    }
}
