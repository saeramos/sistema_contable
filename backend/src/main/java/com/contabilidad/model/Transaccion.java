package com.contabilidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transacciones")
public class Transaccion {
    
    public enum Estado {
        ACTIVA, ANULADA, PENDIENTE
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El tercero es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id", nullable = false)
    private Tercero tercero;
    
    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVA;
    
    @OneToMany(mappedBy = "transaccion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PartidaContable> partidas = new ArrayList<>();
    
    // Constructors
    public Transaccion() {}
    
    public Transaccion(Tercero tercero, LocalDate fecha, String descripcion) {
        this.tercero = tercero;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }
    
    // Helper methods for managing partidas
    public void addPartida(PartidaContable partida) {
        partidas.add(partida);
        partida.setTransaccion(this);
    }
    
    public void removePartida(PartidaContable partida) {
        partidas.remove(partida);
        partida.setTransaccion(null);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Tercero getTercero() {
        return tercero;
    }
    
    public void setTercero(Tercero tercero) {
        this.tercero = tercero;
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
    
    public Estado getEstado() {
        return estado;
    }
    
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    public List<PartidaContable> getPartidas() {
        return partidas;
    }
    
    public void setPartidas(List<PartidaContable> partidas) {
        this.partidas = partidas;
    }
    
    @Override
    public String toString() {
        return "Transaccion{" +
                "id=" + id +
                ", tercero=" + (tercero != null ? tercero.getNombre() : "null") +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", partidas=" + partidas.size() +
                '}';
    }
} 