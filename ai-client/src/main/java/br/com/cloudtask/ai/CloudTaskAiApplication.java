package br.com.cloudtask.ai;

import br.com.cloudtask.ai.config.CloudTaskAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CloudTaskAiProperties.class)
public class CloudTaskAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudTaskAiApplication.class, args);
    }
}
