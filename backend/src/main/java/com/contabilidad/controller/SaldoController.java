package com.contabilidad.controller;

import com.contabilidad.dto.CuentaContableDTO;
import com.contabilidad.service.CuentaContableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/saldos")
@Tag(name = "Saldos", description = "Gestión y consulta de saldos contables")
@CrossOrigin(origins = "*")
public class SaldoController {
    
    @Autowired
    private CuentaContableService cuentaContableService;
    
    @GetMapping
    @Operation(summary = "Obtener saldos de todas las cuentas activas")
    public ResponseEntity<List<CuentaContableDTO>> getSaldosTodasLasCuentas() {
        List<CuentaContableDTO> saldos = cuentaContableService.getSaldosTodasLasCuentas();
        return ResponseEntity.ok(saldos);
    }
    
    @GetMapping("/{cuentaId}")
    @Operation(summary = "Obtener saldo de una cuenta específica")
    public ResponseEntity<CuentaContableDTO> getSaldoCuenta(@PathVariable Long cuentaId) {
        return cuentaContableService.getCuentaConSaldo(cuentaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{cuentaId}/valor")
    @Operation(summary = "Obtener solo el valor del saldo de una cuenta")
    public ResponseEntity<BigDecimal> getValorSaldoCuenta(@PathVariable Long cuentaId) {
        BigDecimal saldo = cuentaContableService.calcularSaldoCuenta(cuentaId);
        return ResponseEntity.ok(saldo);
    }
    
    @GetMapping("/{cuentaId}/hasta-fecha")
    @Operation(summary = "Obtener saldo de una cuenta hasta una fecha específica")
    public ResponseEntity<BigDecimal> getSaldoCuentaHastaFecha(
            @PathVariable Long cuentaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        BigDecimal saldo = cuentaContableService.calcularSaldoCuentaHastaFecha(cuentaId, fecha);
        return ResponseEntity.ok(saldo);
    }
    
    @GetMapping("/{cuentaId}/validar-saldo-negativo")
    @Operation(summary = "Validar si una cuenta permite saldo negativo")
    public ResponseEntity<Boolean> permiteSaldoNegativo(@PathVariable Long cuentaId) {
        boolean permite = cuentaContableService.permiteSaldoNegativo(cuentaId);
        return ResponseEntity.ok(permite);
    }
    
    @GetMapping("/{cuentaId}/validar-activa")
    @Operation(summary = "Validar si una cuenta está activa")
    public ResponseEntity<Boolean> isCuentaActiva(@PathVariable Long cuentaId) {
        boolean activa = cuentaContableService.isCuentaActiva(cuentaId);
        return ResponseEntity.ok(activa);
    }
} 