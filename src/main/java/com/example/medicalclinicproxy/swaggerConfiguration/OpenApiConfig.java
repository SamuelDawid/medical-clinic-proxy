package com.example.medicalclinicproxy.swaggerConfiguration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI medicalClinicProxyOpenAPI(){
        return new OpenAPI().info(
                new Info().title("Medical Clinic Appointments Proxy API")
                        .version("1.0")
                        .description("API for managing patients and doctors appointments at medical clinic")
        );
    }
}
