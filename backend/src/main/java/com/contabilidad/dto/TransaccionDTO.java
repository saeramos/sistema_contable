package com.contabilidad.dto;

import com.contabilidad.model.Transaccion;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TransaccionDTO {
    private Long id;
    private Long terceroId;
    private String terceroNombre;
    private String terceroDocumento;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private String descripcion;
    private String estado;
    private List<PartidaContableDTO> partidas;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;
    private boolean balanceado;
    
    // Constructors
    public TransaccionDTO() {}
    
    public TransaccionDTO(Transaccion transaccion) {
        this.id = transaccion.getId();
        
        // Manejar tercero que puede ser null
        if (transaccion.getTercero() != null) {
            this.terceroId = transaccion.getTercero().getId();
            this.terceroNombre = transaccion.getTercero().getNombre();
            this.terceroDocumento = transaccion.getTercero().getNumeroDocumento();
        } else {
            this.terceroId = null;
            this.terceroNombre = null;
            this.terceroDocumento = null;
        }
        
        this.fecha = transaccion.getFecha();
        this.descripcion = transaccion.getDescripcion();
        this.estado = transaccion.getEstado() != null ? transaccion.getEstado().name() : "ACTIVA";
        
        if (transaccion.getPartidas() != null) {
            this.partidas = transaccion.getPartidas().stream()
                    .map(PartidaContableDTO::fromEntity)
                    .collect(Collectors.toList());
            
            // Calcular totales
            this.totalDebitos = transaccion.getPartidas().stream()
                    .filter(p -> p.getTipo().name().equals("DEBE"))
                    .map(p -> p.getValor())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.totalCreditos = transaccion.getPartidas().stream()
                    .filter(p -> p.getTipo().name().equals("HABER"))
                    .map(p -> p.getValor())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            this.balanceado = this.totalDebitos.compareTo(this.totalCreditos) == 0;
        }
    }
    
    // Static factory method
    public static TransaccionDTO fromEntity(Transaccion transaccion) {
        return new TransaccionDTO(transaccion);
    }
    
    public static List<TransaccionDTO> fromEntityList(List<Transaccion> transacciones) {
        return transacciones.stream()
                .map(TransaccionDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTerceroId() {
        return terceroId;
    }
    
    public void setTerceroId(Long terceroId) {
        this.terceroId = terceroId;
    }
    
    public String getTerceroNombre() {
        return terceroNombre;
    }
    
    public void setTerceroNombre(String terceroNombre) {
        this.terceroNombre = terceroNombre;
    }
    
    public String getTerceroDocumento() {
        return terceroDocumento;
    }
    
    public void setTerceroDocumento(String terceroDocumento) {
        this.terceroDocumento = terceroDocumento;
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
    
    public List<PartidaContableDTO> getPartidas() {
        return partidas;
    }
    
    public void setPartidas(List<PartidaContableDTO> partidas) {
        this.partidas = partidas;
    }
    
    public BigDecimal getTotalDebitos() {
        return totalDebitos;
    }
    
    public void setTotalDebitos(BigDecimal totalDebitos) {
        this.totalDebitos = totalDebitos;
    }
    
    public BigDecimal getTotalCreditos() {
        return totalCreditos;
    }
    
    public void setTotalCreditos(BigDecimal totalCreditos) {
        this.totalCreditos = totalCreditos;
    }
    
    public boolean isBalanceado() {
        return balanceado;
    }
    
    public void setBalanceado(boolean balanceado) {
        this.balanceado = balanceado;
    }
} 