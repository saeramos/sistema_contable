package com.contabilidad.controller;

import com.contabilidad.dto.TransaccionDTO;
import com.contabilidad.dto.request.TransaccionRequest;
import com.contabilidad.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@Tag(name = "Transacciones", description = "Gestión de transacciones contables")
@CrossOrigin(origins = "*")
public class TransaccionController {
    
    @Autowired
    private TransaccionService transaccionService;
    
    @GetMapping
    @Operation(summary = "Obtener todas las transacciones")
    public ResponseEntity<List<TransaccionDTO>> getAllTransacciones() {
        List<TransaccionDTO> transacciones = transaccionService.getAllTransacciones();
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener transacción por ID")
    public ResponseEntity<TransaccionDTO> getTransaccionById(@PathVariable Long id) {
        return transaccionService.getTransaccionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/detalle")
    @Operation(summary = "Obtener transacción por ID con partidas")
    public ResponseEntity<TransaccionDTO> getTransaccionByIdWithPartidas(@PathVariable Long id) {
        return transaccionService.getTransaccionByIdWithPartidas(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Crear nueva transacción")
    public ResponseEntity<TransaccionDTO> createTransaccion(@Valid @RequestBody TransaccionRequest request) {
        try {
            TransaccionDTO transaccion = transaccionService.createTransaccion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/tercero/{terceroId}")
    @Operation(summary = "Obtener transacciones por tercero")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByTercero(@PathVariable Long terceroId) {
        List<TransaccionDTO> transacciones = transaccionService.getTransaccionesByTercero(terceroId);
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/fecha/{fecha}")
    @Operation(summary = "Obtener transacciones por fecha")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<TransaccionDTO> transacciones = transaccionService.getTransaccionesByFecha(fecha);
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/rango-fechas")
    @Operation(summary = "Obtener transacciones en un rango de fechas")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<TransaccionDTO> transacciones = transaccionService.getTransaccionesByRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/tercero/{terceroId}/rango-fechas")
    @Operation(summary = "Obtener transacciones por tercero y rango de fechas")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByTerceroAndRangoFechas(
            @PathVariable Long terceroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<TransaccionDTO> transacciones = transaccionService.getTransaccionesByTerceroAndRangoFechas(terceroId, fechaInicio, fechaFin);
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/filtros")
    @Operation(summary = "Obtener transacciones con filtros opcionales")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesWithFilters(
            @RequestParam(required = false) Long terceroId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String descripcion) {
        List<TransaccionDTO> transacciones = transaccionService.getTransaccionesWithFilters(terceroId, fechaInicio, fechaFin, descripcion);
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar transacciones por descripción")
    public ResponseEntity<List<TransaccionDTO>> searchTransaccionesByDescripcion(@RequestParam String descripcion) {
        List<TransaccionDTO> transacciones = transaccionService.searchTransaccionesByDescripcion(descripcion);
        return ResponseEntity.ok(transacciones);
    }
    
    @GetMapping("/tercero/{terceroId}/count")
    @Operation(summary = "Contar transacciones por tercero")
    public ResponseEntity<Long> countTransaccionesByTercero(@PathVariable Long terceroId) {
        long count = transaccionService.countTransaccionesByTercero(terceroId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/rango-fechas/count")
    @Operation(summary = "Contar transacciones en un rango de fechas")
    public ResponseEntity<Long> countTransaccionesByRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        long count = transaccionService.countTransaccionesByRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener transacciones por estado")
    public ResponseEntity<List<TransaccionDTO>> getTransaccionesByEstado(@PathVariable String estado) {
        try {
            List<TransaccionDTO> transacciones = transaccionService.getTransaccionesByEstado(estado);
            return ResponseEntity.ok(transacciones);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una transacción")
    public ResponseEntity<TransaccionDTO> cambiarEstadoTransaccion(
            @PathVariable Long id, 
            @RequestParam String nuevoEstado) {
        try {
            TransaccionDTO transaccion = transaccionService.cambiarEstadoTransaccion(id, nuevoEstado);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular una transacción")
    public ResponseEntity<TransaccionDTO> anularTransaccion(@PathVariable Long id) {
        try {
            TransaccionDTO transaccion = transaccionService.anularTransaccion(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar una transacción")
    public ResponseEntity<TransaccionDTO> reactivarTransaccion(@PathVariable Long id) {
        try {
            TransaccionDTO transaccion = transaccionService.reactivarTransaccion(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/pendiente")
    @Operation(summary = "Marcar transacción como pendiente")
    public ResponseEntity<TransaccionDTO> marcarPendienteTransaccion(@PathVariable Long id) {
        try {
            TransaccionDTO transaccion = transaccionService.marcarPendienteTransaccion(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 