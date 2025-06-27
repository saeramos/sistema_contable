package com.contabilidad.service;

import com.contabilidad.dto.TerceroDTO;
import com.contabilidad.dto.request.TerceroRequest;
import com.contabilidad.model.Tercero;
import com.contabilidad.repository.TerceroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TerceroService {
    
    @Autowired
    private TerceroRepository terceroRepository;
    
    /**
     * Obtener todos los terceros
     */
    public List<TerceroDTO> getAllTerceros() {
        return terceroRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener tercero por ID
     */
    public Optional<TerceroDTO> getTerceroById(Long id) {
        return terceroRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    /**
     * Crear nuevo tercero
     */
    public TerceroDTO createTercero(TerceroRequest request) {
        // Validar que el número de documento sea único
        if (terceroRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new IllegalArgumentException("El número de documento ya existe");
        }
        
        Tercero tercero = new Tercero();
        tercero.setNombre(request.getNombre());
        tercero.setTipoDocumento(request.getTipoDocumento());
        tercero.setNumeroDocumento(request.getNumeroDocumento());
        tercero.setEmail(request.getEmail());
        tercero.setTelefono(request.getTelefono());
        tercero.setDireccion(request.getDireccion());
        tercero.setActivo(true);
        
        Tercero savedTercero = terceroRepository.save(tercero);
        return convertToDTO(savedTercero);
    }
    
    /**
     * Actualizar tercero
     */
    public Optional<TerceroDTO> updateTercero(Long id, TerceroRequest request) {
        return terceroRepository.findById(id)
                .map(tercero -> {
                    // Validar que el número de documento sea único (excepto para el mismo tercero)
                    if (!tercero.getNumeroDocumento().equals(request.getNumeroDocumento()) &&
                        terceroRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
                        throw new IllegalArgumentException("El número de documento ya existe");
                    }
                    
                    tercero.setNombre(request.getNombre());
                    tercero.setTipoDocumento(request.getTipoDocumento());
                    tercero.setNumeroDocumento(request.getNumeroDocumento());
                    tercero.setEmail(request.getEmail());
                    tercero.setTelefono(request.getTelefono());
                    tercero.setDireccion(request.getDireccion());
                    
                    Tercero updatedTercero = terceroRepository.save(tercero);
                    return convertToDTO(updatedTercero);
                });
    }
    
    /**
     * Eliminar tercero
     */
    public boolean deleteTercero(Long id) {
        Optional<Tercero> terceroOpt = terceroRepository.findById(id);
        if (terceroOpt.isPresent()) {
            Tercero tercero = terceroOpt.get();
            
            // Verificar si tiene transacciones asociadas
            if (tercero.getTransacciones() != null && !tercero.getTransacciones().isEmpty()) {
                throw new IllegalStateException("No se puede eliminar un tercero con transacciones asociadas");
            }
            
            terceroRepository.delete(tercero);
            return true;
        }
        return false;
    }
    
    /**
     * Buscar terceros por nombre
     */
    public List<TerceroDTO> searchTerceros(String query) {
        String searchQuery = "%" + query.toLowerCase() + "%";
        return terceroRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNumeroDocumentoContaining(searchQuery)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar terceros por tipo de documento
     */
    public List<TerceroDTO> getTercerosByTipoDocumento(String tipoDocumento) {
        List<Tercero> terceros = terceroRepository.findByTipoDocumento(tipoDocumento);
        return terceros.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar terceros con transacciones en un rango de fechas
     */
    public List<TerceroDTO> getTercerosConTransaccionesEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Tercero> terceros = terceroRepository.findTercerosConTransaccionesEnRango(fechaInicio, fechaFin);
        return terceros.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Verificar si existe un tercero
     */
    public boolean existsTercero(Long id) {
        return terceroRepository.existsById(id);
    }
    
    /**
     * Obtener tercero por ID para uso interno
     */
    public Optional<Tercero> getTerceroEntityById(Long id) {
        return terceroRepository.findById(id);
    }
    
    /**
     * Activar tercero
     */
    public Optional<TerceroDTO> activateTercero(Long id) {
        return terceroRepository.findById(id)
                .map(tercero -> {
                    tercero.setActivo(true);
                    Tercero updatedTercero = terceroRepository.save(tercero);
                    return convertToDTO(updatedTercero);
                });
    }
    
    /**
     * Desactivar tercero
     */
    public Optional<TerceroDTO> deactivateTercero(Long id) {
        return terceroRepository.findById(id)
                .map(tercero -> {
                    tercero.setActivo(false);
                    Tercero updatedTercero = terceroRepository.save(tercero);
                    return convertToDTO(updatedTercero);
                });
    }
    
    private TerceroDTO convertToDTO(Tercero tercero) {
        TerceroDTO dto = new TerceroDTO();
        dto.setId(tercero.getId());
        dto.setNombre(tercero.getNombre());
        dto.setTipoDocumento(tercero.getTipoDocumento());
        dto.setNumeroDocumento(tercero.getNumeroDocumento());
        dto.setEmail(tercero.getEmail());
        dto.setTelefono(tercero.getTelefono());
        dto.setDireccion(tercero.getDireccion());
        dto.setActivo(tercero.isActivo());
        return dto;
    }
} 