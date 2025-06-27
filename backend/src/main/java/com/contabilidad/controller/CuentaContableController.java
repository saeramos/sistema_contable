package com.contabilidad.controller;

import com.contabilidad.dto.CuentaContableDTO;
import com.contabilidad.dto.request.CuentaContableRequest;
import com.contabilidad.model.TipoCuenta;
import com.contabilidad.service.CuentaContableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas Contables", description = "Gestión del plan de cuentas")
@CrossOrigin(origins = "*")
public class CuentaContableController {
    
    @Autowired
    private CuentaContableService cuentaContableService;
    
    @GetMapping
    @Operation(summary = "Obtener todas las cuentas contables")
    public ResponseEntity<List<CuentaContableDTO>> getAllCuentas() {
        List<CuentaContableDTO> cuentas = cuentaContableService.getAllCuentas();
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/activas")
    @Operation(summary = "Obtener solo cuentas activas")
    public ResponseEntity<List<CuentaContableDTO>> getCuentasActivas() {
        List<CuentaContableDTO> cuentas = cuentaContableService.getCuentasActivas();
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    public ResponseEntity<CuentaContableDTO> getCuentaById(@PathVariable Long id) {
        return cuentaContableService.getCuentaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Crear nueva cuenta contable")
    public ResponseEntity<CuentaContableDTO> createCuenta(@Valid @RequestBody CuentaContableRequest request) {
        try {
            CuentaContableDTO cuenta = cuentaContableService.createCuenta(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta contable")
    public ResponseEntity<CuentaContableDTO> updateCuenta(@PathVariable Long id, @Valid @RequestBody CuentaContableRequest request) {
        try {
            return cuentaContableService.updateCuenta(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar cuenta")
    public ResponseEntity<CuentaContableDTO> activarCuenta(@PathVariable Long id) {
        return cuentaContableService.activarCuenta(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar cuenta")
    public ResponseEntity<CuentaContableDTO> desactivarCuenta(@PathVariable Long id) {
        return cuentaContableService.desactivarCuenta(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuenta contable")
    public ResponseEntity<Void> deleteCuenta(@PathVariable Long id) {
        boolean deleted = cuentaContableService.deleteCuenta(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar cuentas por nombre")
    public ResponseEntity<List<CuentaContableDTO>> searchCuentasByNombre(@RequestParam String nombre) {
        List<CuentaContableDTO> cuentas = cuentaContableService.searchCuentasByNombre(nombre);
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/search/activas")
    @Operation(summary = "Buscar cuentas activas por nombre")
    public ResponseEntity<List<CuentaContableDTO>> searchCuentasActivasByNombre(@RequestParam String nombre) {
        List<CuentaContableDTO> cuentas = cuentaContableService.searchCuentasActivasByNombre(nombre);
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener cuentas por tipo")
    public ResponseEntity<List<CuentaContableDTO>> getCuentasByTipo(@PathVariable TipoCuenta tipo) {
        List<CuentaContableDTO> cuentas = cuentaContableService.getCuentasByTipo(tipo);
        return ResponseEntity.ok(cuentas);
    }
} 