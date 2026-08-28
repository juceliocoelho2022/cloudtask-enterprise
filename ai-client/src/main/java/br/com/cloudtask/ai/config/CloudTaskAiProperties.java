package br.com.cloudtask.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudtask.ai")
public record CloudTaskAiProperties(String timeZone) {

    public CloudTaskAiProperties {
        if (timeZone == null || timeZone.isBlank()) {
            timeZone = "America/Sao_Paulo";
        }
    }
}
