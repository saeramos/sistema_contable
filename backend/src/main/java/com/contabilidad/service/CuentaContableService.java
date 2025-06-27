package com.contabilidad.service;

import com.contabilidad.dto.CuentaContableDTO;
import com.contabilidad.dto.request.CuentaContableRequest;
import com.contabilidad.model.CuentaContable;
import com.contabilidad.model.TipoCuenta;
import com.contabilidad.repository.CuentaContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CuentaContableService {
    
    @Autowired
    private CuentaContableRepository cuentaContableRepository;
    
    /**
     * Obtener todas las cuentas contables
     */
    public List<CuentaContableDTO> getAllCuentas() {
        List<CuentaContable> cuentas = cuentaContableRepository.findAll();
        return CuentaContableDTO.fromEntityList(cuentas);
    }
    
    /**
     * Obtener solo cuentas activas
     */
    public List<CuentaContableDTO> getCuentasActivas() {
        List<CuentaContable> cuentas = cuentaContableRepository.findByActivoTrue();
        return CuentaContableDTO.fromEntityList(cuentas);
    }
    
    /**
     * Obtener cuenta por ID
     */
    public Optional<CuentaContableDTO> getCuentaById(Long id) {
        return cuentaContableRepository.findById(id)
                .map(CuentaContableDTO::fromEntity);
    }
    
    /**
     * Crear nueva cuenta contable
     */
    public CuentaContableDTO createCuenta(CuentaContableRequest request) {
        // Validar que el código no exista
        if (cuentaContableRepository.existsByCodigo(request.getCodigo())) {
            throw new RuntimeException("Ya existe una cuenta con el código: " + request.getCodigo());
        }
        
        CuentaContable cuenta = new CuentaContable();
        cuenta.setCodigo(request.getCodigo());
        cuenta.setNombre(request.getNombre());
        cuenta.setTipo(request.getTipo());
        cuenta.setPermiteSaldoNegativo(request.isPermiteSaldoNegativo());
        cuenta.setActivo(request.isActivo());
        
        CuentaContable savedCuenta = cuentaContableRepository.save(cuenta);
        return CuentaContableDTO.fromEntity(savedCuenta);
    }
    
    /**
     * Actualizar cuenta contable
     */
    public Optional<CuentaContableDTO> updateCuenta(Long id, CuentaContableRequest request) {
        return cuentaContableRepository.findById(id)
                .map(cuenta -> {
                    // Validar que el código no exista en otra cuenta
                    Optional<CuentaContable> existingCuenta = cuentaContableRepository.findByCodigo(request.getCodigo());
                    if (existingCuenta.isPresent() && !existingCuenta.get().getId().equals(id)) {
                        throw new RuntimeException("Ya existe una cuenta con el código: " + request.getCodigo());
                    }
                    
                    cuenta.setCodigo(request.getCodigo());
                    cuenta.setNombre(request.getNombre());
                    cuenta.setTipo(request.getTipo());
                    cuenta.setPermiteSaldoNegativo(request.isPermiteSaldoNegativo());
                    cuenta.setActivo(request.isActivo());
                    
                    CuentaContable updatedCuenta = cuentaContableRepository.save(cuenta);
                    return CuentaContableDTO.fromEntity(updatedCuenta);
                });
    }
    
    /**
     * Activar cuenta
     */
    public Optional<CuentaContableDTO> activarCuenta(Long id) {
        return cuentaContableRepository.findById(id)
                .map(cuenta -> {
                    cuenta.setActivo(true);
                    CuentaContable updatedCuenta = cuentaContableRepository.save(cuenta);
                    return CuentaContableDTO.fromEntity(updatedCuenta);
                });
    }
    
    /**
     * Desactivar cuenta
     */
    public Optional<CuentaContableDTO> desactivarCuenta(Long id) {
        return cuentaContableRepository.findById(id)
                .map(cuenta -> {
                    cuenta.setActivo(false);
                    CuentaContable updatedCuenta = cuentaContableRepository.save(cuenta);
                    return CuentaContableDTO.fromEntity(updatedCuenta);
                });
    }
    
    /**
     * Eliminar cuenta contable
     */
    public boolean deleteCuenta(Long id) {
        if (cuentaContableRepository.existsById(id)) {
            cuentaContableRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Buscar cuentas por nombre
     */
    public List<CuentaContableDTO> searchCuentasByNombre(String nombre) {
        List<CuentaContable> cuentas = cuentaContableRepository.findByNombreContainingIgnoreCase(nombre);
        return CuentaContableDTO.fromEntityList(cuentas);
    }
    
    /**
     * Buscar cuentas activas por nombre
     */
    public List<CuentaContableDTO> searchCuentasActivasByNombre(String nombre) {
        List<CuentaContable> cuentas = cuentaContableRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre);
        return CuentaContableDTO.fromEntityList(cuentas);
    }
    
    /**
     * Obtener cuentas por tipo
     */
    public List<CuentaContableDTO> getCuentasByTipo(TipoCuenta tipo) {
        List<CuentaContable> cuentas = cuentaContableRepository.findByActivoTrueAndTipo(tipo);
        return CuentaContableDTO.fromEntityList(cuentas);
    }
    
    /**
     * Calcular saldo de una cuenta específica
     */
    public BigDecimal calcularSaldoCuenta(Long cuentaId) {
        return cuentaContableRepository.calcularSaldoPorCuenta(cuentaId);
    }
    
    /**
     * Obtener saldo de cuenta con información completa
     */
    public Optional<CuentaContableDTO> getCuentaConSaldo(Long cuentaId) {
        return cuentaContableRepository.findById(cuentaId)
                .map(cuenta -> {
                    BigDecimal saldo = cuentaContableRepository.calcularSaldoPorCuenta(cuentaId);
                    return CuentaContableDTO.fromEntityWithSaldo(cuenta, saldo);
                });
    }
    
    /**
     * Obtener saldos de todas las cuentas activas
     */
    public List<CuentaContableDTO> getSaldosTodasLasCuentas() {
        List<Object[]> resultados = cuentaContableRepository.findAllWithSaldo();
        
        return resultados.stream()
                .map(resultado -> {
                    Long id = (Long) resultado[0];
                    String codigo = (String) resultado[1];
                    String nombre = (String) resultado[2];
                    // Convertir el campo a String de forma robusta
                    TipoCuenta tipo = TipoCuenta.valueOf(resultado[3].toString());
                    Boolean permiteSaldoNegativo = (Boolean) resultado[4];
                    Boolean activo = (Boolean) resultado[5];
                    BigDecimal saldo = (BigDecimal) resultado[6];
                    
                    CuentaContable cuenta = new CuentaContable();
                    cuenta.setId(id);
                    cuenta.setCodigo(codigo);
                    cuenta.setNombre(nombre);
                    cuenta.setTipo(tipo);
                    cuenta.setPermiteSaldoNegativo(permiteSaldoNegativo);
                    cuenta.setActivo(activo);
                    
                    return CuentaContableDTO.fromEntityWithSaldo(cuenta, saldo);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calcular saldo de una cuenta hasta una fecha específica
     */
    public BigDecimal calcularSaldoCuentaHastaFecha(Long cuentaId, LocalDate fecha) {
        return cuentaContableRepository.calcularSaldoPorCuentaHastaFecha(cuentaId, fecha);
    }
    
    /**
     * Verificar si una cuenta está activa
     */
    public boolean isCuentaActiva(Long cuentaId) {
        return cuentaContableRepository.findById(cuentaId)
                .map(CuentaContable::isActivo)
                .orElse(false);
    }
    
    /**
     * Verificar si una cuenta permite saldo negativo
     */
    public boolean permiteSaldoNegativo(Long cuentaId) {
        return cuentaContableRepository.findById(cuentaId)
                .map(CuentaContable::isPermiteSaldoNegativo)
                .orElse(false);
    }
    
    /**
     * Obtener cuenta por ID para uso interno
     */
    public Optional<CuentaContable> getCuentaEntityById(Long id) {
        return cuentaContableRepository.findById(id);
    }
    
    /**
     * Validar que una cuenta existe y está activa
     */
    public void validarCuentaActiva(Long cuentaId) {
        Optional<CuentaContable> cuenta = cuentaContableRepository.findById(cuentaId);
        if (cuenta.isEmpty()) {
            throw new RuntimeException("La cuenta con ID " + cuentaId + " no existe");
        }
        if (!cuenta.get().isActivo()) {
            throw new RuntimeException("La cuenta " + cuenta.get().getCodigo() + " - " + 
                    cuenta.get().getNombre() + " está inactiva y no puede ser utilizada en transacciones");
        }
    }
} 