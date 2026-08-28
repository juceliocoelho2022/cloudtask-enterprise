package br.com.cloudtask.mcp;

import br.com.cloudtask.mcp.config.CloudTaskApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CloudTaskApiProperties.class)
public class CloudTaskMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudTaskMcpApplication.class, args);
    }
}
