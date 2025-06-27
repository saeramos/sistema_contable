package com.contabilidad.model;

public enum TipoPartida {
    DEBE("Débito"),
    HABER("Crédito");
    
    private final String descripcion;
    
    TipoPartida(String descripcion) {
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