package com.contabilidad.dto;

import com.contabilidad.model.CuentaContable;
import com.contabilidad.model.TipoCuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CuentaContableDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private TipoCuenta tipo;
    private boolean permiteSaldoNegativo;
    private boolean activo;
    private BigDecimal saldo;
    private String colorSaldo; // Verde: positivo, Rojo: negativo, Gris: cero
    
    // Constructors
    public CuentaContableDTO() {}
    
    public CuentaContableDTO(CuentaContable cuenta) {
        this.id = cuenta.getId();
        this.codigo = cuenta.getCodigo();
        this.nombre = cuenta.getNombre();
        this.tipo = cuenta.getTipo();
        this.permiteSaldoNegativo = cuenta.isPermiteSaldoNegativo();
        this.activo = cuenta.isActivo();
    }
    
    public CuentaContableDTO(CuentaContable cuenta, BigDecimal saldo) {
        this(cuenta);
        this.saldo = saldo;
        this.colorSaldo = determinarColorSaldo(saldo);
    }
    
    // Static factory methods
    public static CuentaContableDTO fromEntity(CuentaContable cuenta) {
        return new CuentaContableDTO(cuenta);
    }
    
    public static CuentaContableDTO fromEntityWithSaldo(CuentaContable cuenta, BigDecimal saldo) {
        return new CuentaContableDTO(cuenta, saldo);
    }
    
    public static List<CuentaContableDTO> fromEntityList(List<CuentaContable> cuentas) {
        return cuentas.stream()
                .map(CuentaContableDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Helper method to determine saldo color
    private String determinarColorSaldo(BigDecimal saldo) {
        if (saldo == null) return "gris";
        int comparacion = saldo.compareTo(BigDecimal.ZERO);
        if (comparacion > 0) return "verde";
        if (comparacion < 0) return "rojo";
        return "gris";
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
    
    public BigDecimal getSaldo() {
        return saldo;
    }
    
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
        this.colorSaldo = determinarColorSaldo(saldo);
    }
    
    public String getColorSaldo() {
        return colorSaldo;
    }
    
    public void setColorSaldo(String colorSaldo) {
        this.colorSaldo = colorSaldo;
    }
} 