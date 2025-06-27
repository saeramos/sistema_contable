package com.contabilidad;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Sistema de Contabilidad API",
        version = "1.0.0",
        description = "API REST para gestión de transacciones contables, terceros y cuentas contables",
        contact = @Contact(
            name = "Equipo de Desarrollo",
            email = "desarrollo@contabilidad.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    )
)
public class SistemaContabilidadApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SistemaContabilidadApplication.class, args);
    }
} 