package com.contabilidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "partidas_contables")
public class PartidaContable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La transacción es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;
    
    @NotNull(message = "La cuenta contable es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaContable cuentaContable;
    
    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPartida tipo;
    
    @NotNull(message = "El valor es obligatorio")
    @Positive(message = "El valor debe ser positivo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    
    // Constructors
    public PartidaContable() {}
    
    public PartidaContable(Transaccion transaccion, CuentaContable cuentaContable, TipoPartida tipo, BigDecimal valor) {
        this.transaccion = transaccion;
        this.cuentaContable = cuentaContable;
        this.tipo = tipo;
        this.valor = valor;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Transaccion getTransaccion() {
        return transaccion;
    }
    
    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }
    
    public CuentaContable getCuentaContable() {
        return cuentaContable;
    }
    
    public void setCuentaContable(CuentaContable cuentaContable) {
        this.cuentaContable = cuentaContable;
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
    
    @Override
    public String toString() {
        return "PartidaContable{" +
                "id=" + id +
                ", transaccion=" + (transaccion != null ? transaccion.getId() : "null") +
                ", cuentaContable=" + (cuentaContable != null ? cuentaContable.getCodigo() : "null") +
                ", tipo=" + tipo +
                ", valor=" + valor +
                '}';
    }
} 