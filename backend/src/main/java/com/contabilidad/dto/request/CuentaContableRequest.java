package com.contabilidad.dto.request;

import com.contabilidad.model.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CuentaContableRequest {
    
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    private String codigo;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotNull(message = "El tipo es obligatorio")
    private TipoCuenta tipo;
    
    private boolean permiteSaldoNegativo = false;
    
    private boolean activo = true;
    
    // Constructors
    public CuentaContableRequest() {}
    
    public CuentaContableRequest(String codigo, String nombre, TipoCuenta tipo, boolean permiteSaldoNegativo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.permiteSaldoNegativo = permiteSaldoNegativo;
        this.activo = true;
    }
    
    // Getters and Setters
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
} 