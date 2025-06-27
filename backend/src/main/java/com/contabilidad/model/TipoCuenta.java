package com.contabilidad.model;

public enum TipoCuenta {
    ACTIVO("Activo"),
    PASIVO("Pasivo"),
    PATRIMONIO("Patrimonio"),
    INGRESO("Ingreso"),
    GASTO("Gasto");
    
    private final String descripcion;
    
    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
} 