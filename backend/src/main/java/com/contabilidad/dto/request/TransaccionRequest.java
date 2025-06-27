package com.contabilidad.dto.request;

import com.contabilidad.model.TipoPartida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransaccionRequest {
    
    private Long terceroId; // Opcional
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;
    
    private String estado = "ACTIVA"; // Por defecto ACTIVA
    
    @Valid
    @NotNull(message = "Las partidas son obligatorias")
    @Size(min = 2, message = "Debe tener al menos 2 partidas (un débito y un crédito)")
    private List<PartidaRequest> partidas;
    
    // Constructors
    public TransaccionRequest() {}
    
    public TransaccionRequest(Long terceroId, LocalDate fecha, String descripcion, List<PartidaRequest> partidas) {
        this.terceroId = terceroId;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.partidas = partidas;
    }
    
    // Getters and Setters
    public Long getTerceroId() {
        return terceroId;
    }
    
    public void setTerceroId(Long terceroId) {
        this.terceroId = terceroId;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public List<PartidaRequest> getPartidas() {
        return partidas;
    }
    
    public void setPartidas(List<PartidaRequest> partidas) {
        this.partidas = partidas;
    }
    
    // Inner class for PartidaRequest
    public static class PartidaRequest {
        
        @NotNull(message = "La cuenta contable es obligatoria")
        private Long cuentaContableId;
        
        @NotNull(message = "El tipo es obligatorio")
        private TipoPartida tipo;
        
        @NotNull(message = "El valor es obligatorio")
        @Positive(message = "El valor debe ser positivo")
        private BigDecimal valor;
        
        // Constructors
        public PartidaRequest() {}
        
        public PartidaRequest(Long cuentaContableId, TipoPartida tipo, BigDecimal valor) {
            this.cuentaContableId = cuentaContableId;
            this.tipo = tipo;
            this.valor = valor;
        }
        
        // Getters and Setters
        public Long getCuentaContableId() {
            return cuentaContableId;
        }
        
        public void setCuentaContableId(Long cuentaContableId) {
            this.cuentaContableId = cuentaContableId;
        }
        
        public TipoPartida getTipo() {
            return tipo;
        }
        
        public void setTipo(TipoPartida tipo) {
            this.tipo = tipo;
        }
        
        public BigDecimal getValor() {
            return valor;
        }
        
        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }
} 