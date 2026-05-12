package com.gitgames.craps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PlatformScoreReporter {

    private static final Logger LOG = Logger.getLogger(PlatformScoreReporter.class.getName());

    @Value("${platform.api.url:}")
    private String platformApiUrl;

    @Value("${platform.api.key:}")
    private String platformApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void reportGameResult(String userId, int score, ObjectNode metadata) {
        if (platformApiUrl.isBlank() || platformApiKey.isBlank()) return;
        if (userId == null) return;

        try {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("gameSlug", "craps");
            json.put("userId", userId);
            json.put("score", score);
            json.set("metadata", metadata);
            json.put("completedAt", Instant.now().toString());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(platformApiUrl + "/api/scores"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + platformApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(json)))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(r -> {
                        if (r.statusCode() >= 400) {
                            LOG.warning("Score report failed with status " + r.statusCode());
                        }
                    });
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to report score", e);
        }
    }
}
