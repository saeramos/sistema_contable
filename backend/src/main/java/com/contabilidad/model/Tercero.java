package com.contabilidad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Entity
@Table(name = "terceros", indexes = {
    @Index(name = "idx_numero_documento", columnList = "numero_documento", unique = true),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_activo", columnList = "activo")
})
public class Tercero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;
    
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(CC|CE|NIT|TI|PP|RC|DE|PA)$", message = "Tipo de documento no válido")
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;
    
    @NotBlank(message = "El número de documento es obligatorio")
    @Column(name = "numero_documento", nullable = false, length = 20, unique = true)
    private String numeroDocumento;
    
    @Email(message = "El formato del email no es válido")
    @Column(name = "email", length = 100)
    private String email;
    
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "El formato del teléfono no es válido")
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "direccion", length = 300)
    private String direccion;
    
    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "tercero", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones;
    
    // Constructores
    public Tercero() {}
    
    public Tercero(String nombre, String tipoDocumento, String numeroDocumento) {
        this.nombre = nombre;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public Boolean isActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public List<Transaccion> getTransacciones() {
        return transacciones;
    }
    
    public void setTransacciones(List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }
    
    @Override
    public String toString() {
        return "Tercero{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Tercero tercero = (Tercero) o;
        
        return id != null ? id.equals(tercero.id) : tercero.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 