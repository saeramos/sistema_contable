package com.contabilidad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Contabilidad API")
                .description("""
                    ## Sistema de Contabilidad Completo
                    
                    Esta API proporciona funcionalidades completas para la gestión contable, incluyendo:
                    
                    ### 🏢 **Gestión de Terceros**
                    - Crear, leer, actualizar y eliminar terceros (clientes, proveedores, empleados)
                    - Búsqueda avanzada con filtros
                    - Validación de datos y duplicados
                    
                    ### 📊 **Cuentas Contables**
                    - Gestión de plan de cuentas
                    - Estados activo/inactivo
                    - Tipos de cuenta (Activo, Pasivo, Patrimonio, Ingreso, Gasto)
                    - Validación de saldos negativos
                    
                    ### 💰 **Transacciones**
                    - Registro de partidas contables
                    - Validación de cuentas activas
                    - Balance automático de partidas
                    - Historial de transacciones
                    
                    ### 📈 **Saldos y Reportes**
                    - Cálculo automático de saldos
                    - Verificación de balances
                    - Reportes en tiempo real
                    - Alertas de saldos negativos
                    
                    ### 🔒 **Seguridad**
                    - Cifrado AES-256 para datos sensibles
                    - Protección contra inyección SQL
                    - Validación de entrada
                    - Headers de seguridad
                    
                    ### 📋 **Características Técnicas**
                    - **Base de datos**: MySQL 8.0 con UTF-8
                    - **Backend**: Spring Boot 3.2 con Java 17
                    - **Frontend**: React 18 con TypeScript
                    - **Seguridad**: Spring Security con JWT
                    - **Documentación**: OpenAPI 3.0 (Swagger)
                    
                    ### 🚀 **Endpoints Principales**
                    
                    | Método | Endpoint | Descripción |
                    |--------|----------|-------------|
                    | GET | `/api/terceros` | Listar todos los terceros |
                    | POST | `/api/terceros` | Crear nuevo tercero |
                    | PUT | `/api/terceros/{id}` | Actualizar tercero |
                    | DELETE | `/api/terceros/{id}` | Eliminar tercero |
                    | GET | `/api/cuentas` | Listar cuentas contables |
                    | POST | `/api/cuentas` | Crear nueva cuenta |
                    | PUT | `/api/cuentas/{id}` | Actualizar cuenta |
                    | GET | `/api/transacciones` | Listar transacciones |
                    | POST | `/api/transacciones` | Crear transacción |
                    | GET | `/api/saldos` | Obtener saldos actuales |
                    
                    ### 🔐 **Autenticación**
                    
                    La API utiliza autenticación JWT. Para acceder a los endpoints protegidos:
                    
                    1. Obtener token de autenticación
                    2. Incluir en header: `Authorization: Bearer <token>`
                    
                    ### 📝 **Ejemplos de Uso**
                    
                    #### Crear Tercero
                    ```json
                    {
                      "nombre": "Juan Pérez",
                      "tipoDocumento": "CC",
                      "numeroDocumento": "12345678",
                      "email": "juan@ejemplo.com",
                      "telefono": "3001234567",
                      "direccion": "Calle 123 #45-67"
                    }
                    ```
                    
                    #### Crear Cuenta Contable
                    ```json
                    {
                      "codigo": "1100",
                      "nombre": "Caja",
                      "tipo": "ACTIVO",
                      "permiteSaldoNegativo": false,
                      "activo": true
                    }
                    ```
                    
                    #### Crear Transacción
                    ```json
                    {
                      "fecha": "2024-01-15",
                      "descripcion": "Pago de servicios",
                      "partidas": [
                        {
                          "cuentaContableId": 1,
                          "tipo": "DEBE",
                          "valor": 100000,
                          "descripcion": "Pago en efectivo"
                        },
                        {
                          "cuentaContableId": 2,
                          "tipo": "HABER",
                          "valor": 100000,
                          "descripcion": "Pago desde banco"
                        }
                      ]
                    }
                    ```
                    
                    ### ⚠️ **Validaciones Importantes**
                    
                    - **Terceros**: Número de documento único
                    - **Cuentas**: Código único, saldos negativos según configuración
                    - **Transacciones**: Balance de partidas, cuentas activas
                    - **Saldos**: Verificación automática de consistencia
                    
                    ### 📊 **Códigos de Respuesta**
                    
                    | Código | Descripción |
                    |--------|-------------|
                    | 200 | Operación exitosa |
                    | 201 | Recurso creado |
                    | 400 | Error de validación |
                    | 401 | No autorizado |
                    | 403 | Prohibido |
                    | 404 | No encontrado |
                    | 409 | Conflicto (duplicado) |
                    | 500 | Error interno del servidor |
                    
                    ### 🔧 **Configuración de Desarrollo**
                    
                    ```yaml
                    # application.yml
                    spring:
                      datasource:
                        url: jdbc:mysql://localhost:3307/contabilidad
                        username: root
                        password: password
                      jpa:
                        hibernate:
                          ddl-auto: update
                        show-sql: true
                    
                    # Swagger UI
                    springdoc:
                      swagger-ui:
                        path: /swagger-ui.html
                      api-docs:
                        path: /v3/api-docs
                    ```
                    
                    ### 📞 **Soporte**
                    
                    Para soporte técnico o reportar problemas:
                    - **Email**: soporte@contabilidad.com
                    - **Documentación**: [Wiki del Proyecto](https://github.com/contabilidad/wiki)
                    - **Issues**: [GitHub Issues](https://github.com/contabilidad/issues)
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("desarrollo@contabilidad.com")
                    .url("https://contabilidad.com")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
            )
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor de Desarrollo"),
                new Server()
                    .url("https://api.contabilidad.com")
                    .description("Servidor de Producción")
            ))
            .externalDocs(new ExternalDocumentation()
                .description("Documentación Completa del Sistema")
                .url("https://contabilidad.com/docs")
            )
            .tags(List.of(
                new Tag()
                    .name("Terceros")
                    .description("Gestión de terceros (clientes, proveedores, empleados)")
                    .externalDocs(new ExternalDocumentation()
                        .description("Guía de Terceros")
                        .url("https://contabilidad.com/docs/terceros")
                    ),
                new Tag()
                    .name("Cuentas Contables")
                    .description("Gestión del plan de cuentas contables")
                    .externalDocs(new ExternalDocumentation()
                        .description("Guía de Cuentas")
                        .url("https://contabilidad.com/docs/cuentas")
                    ),
                new Tag()
                    .name("Transacciones")
                    .description("Registro y gestión de transacciones contables")
                    .externalDocs(new ExternalDocumentation()
                        .description("Guía de Transacciones")
                        .url("https://contabilidad.com/docs/transacciones")
                    ),
                new Tag()
                    .name("Saldos")
                    .description("Consulta y verificación de saldos contables")
                    .externalDocs(new ExternalDocumentation()
                        .description("Guía de Saldos")
                        .url("https://contabilidad.com/docs/saldos")
                    ),
                new Tag()
                    .name("Seguridad")
                    .description("Endpoints relacionados con autenticación y seguridad")
                    .externalDocs(new ExternalDocumentation()
                        .description("Guía de Seguridad")
                        .url("https://contabilidad.com/docs/seguridad")
                    )
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token de autenticación. Incluir en formato: Bearer <token>")
                )
                .addSecuritySchemes("apiKey", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-API-Key")
                    .description("Clave API para acceso a endpoints protegidos")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
} 