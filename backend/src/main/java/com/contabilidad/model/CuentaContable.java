package com.contabilidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "cuentas_contables")
public class CuentaContable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    @Column(nullable = false, length = 10, unique = true)
    private String codigo;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCuenta tipo;
    
    @Column(name = "permite_saldo_negativo", nullable = false)
    private boolean permiteSaldoNegativo = false;
    
    /**
     * Campo agregado para la mejora: Control de cuentas activas/inactivas
     * Solo las cuentas activas pueden ser utilizadas en transacciones
     */
    @Column(nullable = false)
    private boolean activo = true;
    
    @OneToMany(mappedBy = "cuentaContable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartidaContable> partidas;
    
    // Constructors
    public CuentaContable() {}
    
    public CuentaContable(String codigo, String nombre, TipoCuenta tipo, boolean permiteSaldoNegativo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.permiteSaldoNegativo = permiteSaldoNegativo;
        this.activo = true; // Por defecto activa
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public TipoCuenta getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoCuenta tipo) {
        this.tipo = tipo;
    }
    
    public boolean isPermiteSaldoNegativo() {
        return permiteSaldoNegativo;
    }
    
    public void setPermiteSaldoNegativo(boolean permiteSaldoNegativo) {
        this.permiteSaldoNegativo = permiteSaldoNegativo;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public List<PartidaContable> getPartidas() {
        return partidas;
    }
    
    public void setPartidas(List<PartidaContable> partidas) {
        this.partidas = partidas;
    }
    
    @Override
    public String toString() {
        return "CuentaContable{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", permiteSaldoNegativo=" + permiteSaldoNegativo +
                ", activo=" + activo +
                '}';
    }
} 