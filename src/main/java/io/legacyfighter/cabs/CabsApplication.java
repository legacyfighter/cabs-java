package io.legacyfighter.cabs;

import io.legacyfighter.cabs.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class CabsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CabsApplication.class, args);
    }
}