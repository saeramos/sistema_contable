package com.contabilidad.repository;

import com.contabilidad.model.CuentaContable;
import com.contabilidad.model.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaContableRepository extends JpaRepository<CuentaContable, Long> {
    
    /**
     * Buscar cuenta por código
     */
    Optional<CuentaContable> findByCodigo(String codigo);
    
    /**
     * Verificar si existe una cuenta con el código
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Buscar cuentas activas
     */
    List<CuentaContable> findByActivoTrue();
    
    /**
     * Buscar cuentas activas por tipo
     */
    List<CuentaContable> findByActivoTrueAndTipo(TipoCuenta tipo);
    
    /**
     * Buscar cuentas por nombre (búsqueda parcial)
     */
    List<CuentaContable> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Buscar cuentas activas por nombre (búsqueda parcial)
     */
    List<CuentaContable> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);
    
    /**
     * Calcular saldo de una cuenta específica
     * Suma de débitos - Suma de créditos
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN pc.tipo = 'DEBE' THEN pc.valor ELSE 0 END), 0) - " +
           "COALESCE(SUM(CASE WHEN pc.tipo = 'HABER' THEN pc.valor ELSE 0 END), 0) " +
           "FROM PartidaContable pc " +
           "WHERE pc.cuentaContable.id = :cuentaId")
    BigDecimal calcularSaldoPorCuenta(@Param("cuentaId") Long cuentaId);
    
    /**
     * Calcular saldos de todas las cuentas activas
     */
    @Query(value = "SELECT cc.*, " +
            "COALESCE(SUM(CASE WHEN pc.tipo = 'DEBE' THEN pc.valor ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN pc.tipo = 'HABER' THEN pc.valor ELSE 0 END), 0) " +
            "as saldo " +
            "FROM cuentas_contables cc " +
            "LEFT JOIN partidas_contables pc ON cc.id = pc.cuenta_id " +
            "WHERE cc.activo = 1 " +
            "GROUP BY cc.id, cc.codigo, cc.nombre, cc.tipo " +
            "ORDER BY cc.codigo", nativeQuery = true)
    List<Object[]> findAllWithSaldo();
    
    /**
     * Calcular saldo de una cuenta hasta una fecha específica
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN pc.tipo = 'DEBE' THEN pc.valor ELSE 0 END), 0) - " +
           "COALESCE(SUM(CASE WHEN pc.tipo = 'HABER' THEN pc.valor ELSE 0 END), 0) " +
           "FROM PartidaContable pc " +
           "WHERE pc.cuentaContable.id = :cuentaId " +
           "AND pc.transaccion.fecha <= :fecha")
    BigDecimal calcularSaldoPorCuentaHastaFecha(
            @Param("cuentaId") Long cuentaId, 
            @Param("fecha") java.time.LocalDate fecha);
    
    @Query(value = "SELECT cc.*, " +
            "COALESCE(SUM(CASE WHEN pc.tipo = 'DEBE' THEN pc.valor ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN pc.tipo = 'HABER' THEN pc.valor ELSE 0 END), 0) " +
            "as saldo " +
            "FROM cuentas_contables cc " +
            "LEFT JOIN partidas_contables pc ON cc.id = pc.cuenta_id " +
            "WHERE cc.id = :cuentaId " +
            "GROUP BY cc.id, cc.codigo, cc.nombre, cc.tipo", nativeQuery = true)
    Optional<Object[]> findByIdWithSaldo(@Param("cuentaId") Long cuentaId);
    
    @Query(value = "SELECT cc.*, " +
            "COALESCE(SUM(CASE WHEN pc.tipo = 'DEBE' THEN pc.valor ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN pc.tipo = 'HABER' THEN pc.valor ELSE 0 END), 0) " +
            "as saldo " +
            "FROM cuentas_contables cc " +
            "LEFT JOIN partidas_contables pc ON cc.id = pc.cuenta_id " +
            "WHERE cc.tipo = :tipo AND cc.activo = 1 " +
            "GROUP BY cc.id, cc.codigo, cc.nombre, cc.tipo " +
            "ORDER BY cc.codigo", nativeQuery = true)
    List<Object[]> findByTipoWithSaldo(@Param("tipo") String tipo);
} 