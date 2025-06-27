package com.contabilidad.repository;

import com.contabilidad.model.Tercero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerceroRepository extends JpaRepository<Tercero, Long> {
    
    /**
     * Buscar tercero por número de documento
     */
    Optional<Tercero> findByNumeroDocumento(String numeroDocumento);
    
    /**
     * Verificar si existe un tercero con el número de documento
     */
    boolean existsByNumeroDocumento(String numeroDocumento);
    
    /**
     * Buscar terceros por nombre (búsqueda parcial, case-insensitive)
     */
    List<Tercero> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Buscar terceros por nombre, email o número de documento
     */
    @Query("SELECT t FROM Tercero t WHERE " +
           "LOWER(t.nombre) LIKE LOWER(:query) OR " +
           "LOWER(t.email) LIKE LOWER(:query) OR " +
           "LOWER(t.numeroDocumento) LIKE LOWER(:query)")
    List<Tercero> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNumeroDocumentoContaining(
        @Param("query") String query);
    
    /**
     * Buscar terceros activos
     */
    List<Tercero> findByActivoTrue();
    
    /**
     * Buscar terceros inactivos
     */
    List<Tercero> findByActivoFalse();
    
    /**
     * Contar terceros activos
     */
    long countByActivoTrue();
    
    /**
     * Contar terceros inactivos
     */
    long countByActivoFalse();
    
    /**
     * Buscar terceros por tipo de documento
     */
    List<Tercero> findByTipoDocumento(String tipoDocumento);
    
    /**
     * Buscar terceros que tengan transacciones en un rango de fechas
     */
    @Query("SELECT DISTINCT t FROM Tercero t " +
           "JOIN t.transacciones tr " +
           "WHERE tr.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY t.nombre")
    List<Tercero> findTercerosConTransaccionesEnRango(
            @Param("fechaInicio") java.time.LocalDate fechaInicio,
            @Param("fechaFin") java.time.LocalDate fechaFin);
} 