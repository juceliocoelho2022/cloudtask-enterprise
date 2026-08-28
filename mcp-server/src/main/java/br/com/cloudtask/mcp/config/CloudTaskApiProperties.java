package br.com.cloudtask.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudtask.api")
public record CloudTaskApiProperties(
        String baseUrl,
        String token
) {
}
