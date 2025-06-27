package com.contabilidad.repository;

import com.contabilidad.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    
    /**
     * Buscar transacciones por tercero
     */
    List<Transaccion> findByTerceroIdOrderByFechaDesc(Long terceroId);
    
    /**
     * Buscar transacciones por fecha
     */
    List<Transaccion> findByFechaOrderByFechaDesc(LocalDate fecha);
    
    /**
     * Buscar transacciones en un rango de fechas
     */
    List<Transaccion> findByFechaBetweenOrderByFechaDesc(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Buscar transacciones por tercero y rango de fechas
     */
    List<Transaccion> findByTerceroIdAndFechaBetweenOrderByFechaDesc(
            Long terceroId, LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Buscar transacciones por descripción (búsqueda parcial)
     */
    List<Transaccion> findByDescripcionContainingIgnoreCaseOrderByFechaDesc(String descripcion);
    
    /**
     * Buscar transacciones por estado
     */
    List<Transaccion> findByEstadoOrderByFechaDesc(Transaccion.Estado estado);
    
    /**
     * Contar transacciones por tercero
     */
    long countByTerceroId(Long terceroId);
    
    /**
     * Contar transacciones en un rango de fechas
     */
    long countByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Buscar transacciones con sus partidas (eager loading)
     */
    @Query("SELECT DISTINCT t FROM Transaccion t " +
           "LEFT JOIN FETCH t.partidas p " +
           "LEFT JOIN FETCH p.cuentaContable " +
           "WHERE t.id = :id")
    Transaccion findByIdWithPartidas(@Param("id") Long id);
    
    /**
     * Buscar transacciones con filtros opcionales
     */
    @Query("SELECT t FROM Transaccion t " +
           "WHERE (:terceroId IS NULL OR t.tercero.id = :terceroId) " +
           "AND (:fechaInicio IS NULL OR t.fecha >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR t.fecha <= :fechaFin) " +
           "AND (:descripcion IS NULL OR LOWER(t.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) " +
           "ORDER BY t.fecha DESC")
    List<Transaccion> findWithFilters(
            @Param("terceroId") Long terceroId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("descripcion") String descripcion);
} 