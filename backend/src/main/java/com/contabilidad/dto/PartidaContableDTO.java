package com.contabilidad.dto;

import com.contabilidad.model.PartidaContable;
import com.contabilidad.model.TipoPartida;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PartidaContableDTO {
    private Long id;
    private Long cuentaContableId;
    private String cuentaContableCodigo;
    private String cuentaContableNombre;
    private TipoPartida tipo;
    private BigDecimal valor;
    
    // Constructors
    public PartidaContableDTO() {}
    
    public PartidaContableDTO(PartidaContable partida) {
        this.id = partida.getId();
        this.cuentaContableId = partida.getCuentaContable().getId();
        this.cuentaContableCodigo = partida.getCuentaContable().getCodigo();
        this.cuentaContableNombre = partida.getCuentaContable().getNombre();
        this.tipo = partida.getTipo();
        this.valor = partida.getValor();
    }
    
    // Static factory method
    public static PartidaContableDTO fromEntity(PartidaContable partida) {
        return new PartidaContableDTO(partida);
    }
    
    public static List<PartidaContableDTO> fromEntityList(List<PartidaContable> partidas) {
        return partidas.stream()
                .map(PartidaContableDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCuentaContableId() {
        return cuentaContableId;
    }
    
    public void setCuentaContableId(Long cuentaContableId) {
        this.cuentaContableId = cuentaContableId;
    }
    
    public String getCuentaContableCodigo() {
        return cuentaContableCodigo;
    }
    
    public void setCuentaContableCodigo(String cuentaContableCodigo) {
        this.cuentaContableCodigo = cuentaContableCodigo;
    }
    
    public String getCuentaContableNombre() {
        return cuentaContableNombre;
    }
    
    public void setCuentaContableNombre(String cuentaContableNombre) {
        this.cuentaContableNombre = cuentaContableNombre;
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