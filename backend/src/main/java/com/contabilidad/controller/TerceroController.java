package com.contabilidad.controller;

import com.contabilidad.dto.TerceroDTO;
import com.contabilidad.dto.request.TerceroRequest;
import com.contabilidad.service.TerceroService;
import com.contabilidad.util.EncryptionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/terceros")
@Tag(name = "Terceros", description = "Gestión de terceros (clientes, proveedores, empleados)")
@SecurityRequirement(name = "bearerAuth")
public class TerceroController {
    
    @Autowired
    private TerceroService terceroService;
    
    @Autowired
    private EncryptionUtil encryptionUtil;
    
    @GetMapping
    @Operation(
        summary = "Listar todos los terceros",
        description = "Obtiene una lista completa de todos los terceros registrados en el sistema. " +
                     "Incluye información básica como nombre, tipo de documento, email y estado activo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de terceros obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class),
                examples = @ExampleObject(
                    name = "Terceros Ejemplo",
                    value = """
                    [
                      {
                        "id": 1,
                        "nombre": "Juan Pérez",
                        "tipoDocumento": "CC",
                        "numeroDocumento": "12345678",
                        "email": "juan@ejemplo.com",
                        "telefono": "3001234567",
                        "direccion": "Calle 123 #45-67",
                        "activo": true
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TerceroDTO>> getAllTerceros() {
        try {
            List<TerceroDTO> terceros = terceroService.getAllTerceros();
            return ResponseEntity.ok(terceros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener tercero por ID",
        description = "Obtiene la información detallada de un tercero específico por su ID único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tercero encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Tercero no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TerceroDTO> getTerceroById(
        @Parameter(description = "ID único del tercero", example = "1")
        @PathVariable Long id
    ) {
        try {
            return terceroService.getTerceroById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @Operation(
        summary = "Crear nuevo tercero",
        description = "Crea un nuevo tercero en el sistema. Valida que el número de documento sea único " +
                     "y que todos los campos requeridos estén presentes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Tercero creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de Validación",
                    value = """
                    {
                      "timestamp": "2024-01-15T10:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "El número de documento ya existe"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "409", description = "Conflicto - Número de documento duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TerceroDTO> createTercero(
        @Parameter(
            description = "Datos del tercero a crear",
            required = true,
            content = @Content(
                schema = @Schema(implementation = TerceroRequest.class),
                examples = @ExampleObject(
                    name = "Tercero Ejemplo",
                    value = """
                    {
                      "nombre": "Juan Pérez",
                      "tipoDocumento": "CC",
                      "numeroDocumento": "12345678",
                      "email": "juan@ejemplo.com",
                      "telefono": "3001234567",
                      "direccion": "Calle 123 #45-67"
                    }
                    """
                )
            )
        )
        @Valid @RequestBody TerceroRequest request
    ) {
        try {
            // Validar entrada segura
            if (!encryptionUtil.isSafeInput(request.getNombre()) ||
                !encryptionUtil.isSafeInput(request.getEmail()) ||
                !encryptionUtil.isSafeInput(request.getDireccion())) {
                return ResponseEntity.badRequest().build();
            }

            // Sanitizar entrada
            request.setNombre(encryptionUtil.sanitizeInput(request.getNombre()));
            request.setEmail(encryptionUtil.sanitizeInput(request.getEmail()));
            request.setDireccion(encryptionUtil.sanitizeInput(request.getDireccion()));

            TerceroDTO tercero = terceroService.createTercero(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(tercero);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar tercero existente",
        description = "Actualiza la información de un tercero existente. Solo se pueden modificar " +
                     "campos específicos, manteniendo la integridad de los datos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tercero actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Tercero no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto - Número de documento duplicado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TerceroDTO> updateTercero(
        @Parameter(description = "ID único del tercero", example = "1")
        @PathVariable Long id,
        @Parameter(
            description = "Datos actualizados del tercero",
            required = true,
            content = @Content(schema = @Schema(implementation = TerceroRequest.class))
        )
        @Valid @RequestBody TerceroRequest request
    ) {
        try {
            // Validar entrada segura
            if (!encryptionUtil.isSafeInput(request.getNombre()) ||
                !encryptionUtil.isSafeInput(request.getEmail()) ||
                !encryptionUtil.isSafeInput(request.getDireccion())) {
                return ResponseEntity.badRequest().build();
            }

            // Sanitizar entrada
            request.setNombre(encryptionUtil.sanitizeInput(request.getNombre()));
            request.setEmail(encryptionUtil.sanitizeInput(request.getEmail()));
            request.setDireccion(encryptionUtil.sanitizeInput(request.getDireccion()));

            return terceroService.updateTercero(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar tercero",
        description = "Elimina un tercero del sistema. Esta operación es irreversible y " +
                     "puede afectar transacciones relacionadas."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tercero eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Tercero no encontrado"),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar - Tercero tiene transacciones"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteTercero(
        @Parameter(description = "ID único del tercero", example = "1")
        @PathVariable Long id
    ) {
        try {
            boolean deleted = terceroService.deleteTercero(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    @Operation(
        summary = "Buscar terceros",
        description = "Busca terceros por nombre, email o número de documento. " +
                     "Soporta búsqueda parcial y es case-insensitive."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Búsqueda completada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<TerceroDTO>> searchTerceros(
        @Parameter(description = "Término de búsqueda (nombre, email o documento)", example = "Juan")
        @RequestParam String query
    ) {
        try {
            // Validar entrada segura
            if (!encryptionUtil.isSafeInput(query)) {
                return ResponseEntity.badRequest().build();
            }

            String sanitizedQuery = encryptionUtil.sanitizeInput(query);
            List<TerceroDTO> terceros = terceroService.searchTerceros(sanitizedQuery);
            return ResponseEntity.ok(terceros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/activate")
    @Operation(
        summary = "Activar tercero",
        description = "Activa un tercero que estaba inactivo, permitiendo su uso en nuevas transacciones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tercero activado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Tercero no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TerceroDTO> activateTercero(
        @Parameter(description = "ID único del tercero", example = "1")
        @PathVariable Long id
    ) {
        try {
            return terceroService.activateTercero(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/deactivate")
    @Operation(
        summary = "Desactivar tercero",
        description = "Desactiva un tercero, impidiendo su uso en nuevas transacciones " +
                     "pero manteniendo el historial existente."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tercero desactivado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TerceroDTO.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Tercero no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<TerceroDTO> deactivateTercero(
        @Parameter(description = "ID único del tercero", example = "1")
        @PathVariable Long id
    ) {
        try {
            return terceroService.deactivateTercero(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/tipo-documento/{tipoDocumento}")
    @Operation(summary = "Obtener terceros por tipo de documento")
    public ResponseEntity<List<TerceroDTO>> getTercerosByTipoDocumento(@PathVariable String tipoDocumento) {
        List<TerceroDTO> terceros = terceroService.getTercerosByTipoDocumento(tipoDocumento);
        return ResponseEntity.ok(terceros);
    }
    
    @GetMapping("/con-transacciones")
    @Operation(summary = "Obtener terceros con transacciones en un rango de fechas")
    public ResponseEntity<List<TerceroDTO>> getTercerosConTransaccionesEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<TerceroDTO> terceros = terceroService.getTercerosConTransaccionesEnRango(fechaInicio, fechaFin);
        return ResponseEntity.ok(terceros);
    }
} 