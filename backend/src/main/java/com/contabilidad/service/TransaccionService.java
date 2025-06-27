package com.contabilidad.service;

import com.contabilidad.dto.TransaccionDTO;
import com.contabilidad.dto.request.TransaccionRequest;
import com.contabilidad.model.*;
import com.contabilidad.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransaccionService {
    
    @Autowired
    private TransaccionRepository transaccionRepository;
    
    @Autowired
    private TerceroService terceroService;
    
    @Autowired
    private CuentaContableService cuentaContableService;
    
    /**
     * Obtener todas las transacciones
     */
    public List<TransaccionDTO> getAllTransacciones() {
        List<Transaccion> transacciones = transaccionRepository.findAll();
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Obtener transacción por ID
     */
    public Optional<TransaccionDTO> getTransaccionById(Long id) {
        return transaccionRepository.findById(id)
                .map(TransaccionDTO::fromEntity);
    }
    
    /**
     * Obtener transacción por ID con partidas
     */
    public Optional<TransaccionDTO> getTransaccionByIdWithPartidas(Long id) {
        Transaccion transaccion = transaccionRepository.findByIdWithPartidas(id);
        if (transaccion != null) {
            return Optional.of(TransaccionDTO.fromEntity(transaccion));
        }
        return Optional.empty();
    }
    
    /**
     * Crear nueva transacción con validaciones
     */
    public TransaccionDTO createTransaccion(TransaccionRequest request) {
        // Validar tercero
        Optional<Tercero> tercero = terceroService.getTerceroEntityById(request.getTerceroId());
        if (tercero.isEmpty()) {
            throw new RuntimeException("El tercero con ID " + request.getTerceroId() + " no existe");
        }
        
        // Validar partidas
        validarPartidas(request.getPartidas());
        
        // Crear transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setTercero(tercero.get());
        transaccion.setFecha(request.getFecha());
        transaccion.setDescripcion(request.getDescripcion());
        
        // Crear y agregar partidas
        for (TransaccionRequest.PartidaRequest partidaRequest : request.getPartidas()) {
            Optional<CuentaContable> cuenta = cuentaContableService.getCuentaEntityById(partidaRequest.getCuentaContableId());
            if (cuenta.isEmpty()) {
                throw new RuntimeException("La cuenta con ID " + partidaRequest.getCuentaContableId() + " no existe");
            }
            
            // Validar que la cuenta esté activa
            cuentaContableService.validarCuentaActiva(partidaRequest.getCuentaContableId());
            
            PartidaContable partida = new PartidaContable();
            partida.setTransaccion(transaccion);
            partida.setCuentaContable(cuenta.get());
            partida.setTipo(partidaRequest.getTipo());
            partida.setValor(partidaRequest.getValor());
            
            transaccion.addPartida(partida);
        }
        
        // Validar saldos negativos antes de guardar
        validarSaldosNegativos(transaccion);
        
        Transaccion savedTransaccion = transaccionRepository.save(transaccion);
        return TransaccionDTO.fromEntity(savedTransaccion);
    }
    
    /**
     * Validar que las partidas estén balanceadas
     */
    private void validarPartidas(List<TransaccionRequest.PartidaRequest> partidas) {
        if (partidas == null || partidas.size() < 2) {
            throw new RuntimeException("Debe tener al menos 2 partidas (un débito y un crédito)");
        }
        
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;
        
        for (TransaccionRequest.PartidaRequest partida : partidas) {
            if (partida.getTipo() == TipoPartida.DEBE) {
                totalDebitos = totalDebitos.add(partida.getValor());
            } else if (partida.getTipo() == TipoPartida.HABER) {
                totalCreditos = totalCreditos.add(partida.getValor());
            }
        }
        
        if (totalDebitos.compareTo(totalCreditos) != 0) {
            throw new RuntimeException("Las partidas no están balanceadas. Total débitos: " + 
                    totalDebitos + ", Total créditos: " + totalCreditos);
        }
    }
    
    /**
     * Validar que no se generen saldos negativos no permitidos
     */
    private void validarSaldosNegativos(Transaccion transaccion) {
        for (PartidaContable partida : transaccion.getPartidas()) {
            CuentaContable cuenta = partida.getCuentaContable();
            
            // Solo validar si la cuenta no permite saldo negativo
            if (!cuenta.isPermiteSaldoNegativo()) {
                BigDecimal saldoActual = cuentaContableService.calcularSaldoCuenta(cuenta.getId());
                BigDecimal saldoProyectado = saldoActual;
                
                // Calcular el saldo proyectado después de la transacción
                if (partida.getTipo() == TipoPartida.DEBE) {
                    saldoProyectado = saldoActual.add(partida.getValor());
                } else if (partida.getTipo() == TipoPartida.HABER) {
                    saldoProyectado = saldoActual.subtract(partida.getValor());
                }
                
                // Si el saldo proyectado es negativo, rechazar la transacción
                if (saldoProyectado.compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("La transacción generaría un saldo negativo (" + 
                            saldoProyectado + ") en la cuenta " + cuenta.getCodigo() + " - " + 
                            cuenta.getNombre() + ", la cual no permite saldos negativos");
                }
            }
        }
    }
    
    /**
     * Buscar transacciones por tercero
     */
    public List<TransaccionDTO> getTransaccionesByTercero(Long terceroId) {
        List<Transaccion> transacciones = transaccionRepository.findByTerceroIdOrderByFechaDesc(terceroId);
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Buscar transacciones por fecha
     */
    public List<TransaccionDTO> getTransaccionesByFecha(LocalDate fecha) {
        List<Transaccion> transacciones = transaccionRepository.findByFechaOrderByFechaDesc(fecha);
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Buscar transacciones en un rango de fechas
     */
    public List<TransaccionDTO> getTransaccionesByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Transaccion> transacciones = transaccionRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Buscar transacciones por tercero y rango de fechas
     */
    public List<TransaccionDTO> getTransaccionesByTerceroAndRangoFechas(Long terceroId, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Transaccion> transacciones = transaccionRepository.findByTerceroIdAndFechaBetweenOrderByFechaDesc(terceroId, fechaInicio, fechaFin);
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Buscar transacciones con filtros opcionales
     */
    public List<TransaccionDTO> getTransaccionesWithFilters(Long terceroId, LocalDate fechaInicio, LocalDate fechaFin, String descripcion) {
        List<Transaccion> transacciones = transaccionRepository.findWithFilters(terceroId, fechaInicio, fechaFin, descripcion);
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Buscar transacciones por descripción
     */
    public List<TransaccionDTO> searchTransaccionesByDescripcion(String descripcion) {
        List<Transaccion> transacciones = transaccionRepository.findByDescripcionContainingIgnoreCaseOrderByFechaDesc(descripcion);
        return TransaccionDTO.fromEntityList(transacciones);
    }
    
    /**
     * Contar transacciones por tercero
     */
    public long countTransaccionesByTercero(Long terceroId) {
        return transaccionRepository.countByTerceroId(terceroId);
    }
    
    /**
     * Contar transacciones en un rango de fechas
     */
    public long countTransaccionesByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return transaccionRepository.countByFechaBetween(fechaInicio, fechaFin);
    }
    
    /**
     * Cambiar estado de una transacción
     */
    public TransaccionDTO cambiarEstadoTransaccion(Long id, String nuevoEstado) {
        Optional<Transaccion> transaccionOpt = transaccionRepository.findById(id);
        if (transaccionOpt.isEmpty()) {
            throw new RuntimeException("La transacción con ID " + id + " no existe");
        }
        
        Transaccion transaccion = transaccionOpt.get();
        
        // Validar el nuevo estado
        try {
            Transaccion.Estado estado = Transaccion.Estado.valueOf(nuevoEstado.toUpperCase());
            transaccion.setEstado(estado);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + nuevoEstado + ". Estados válidos: ACTIVA, ANULADA, PENDIENTE");
        }
        
        Transaccion savedTransaccion = transaccionRepository.save(transaccion);
        return TransaccionDTO.fromEntity(savedTransaccion);
    }
    
    /**
     * Obtener transacciones por estado
     */
    public List<TransaccionDTO> getTransaccionesByEstado(String estado) {
        try {
            Transaccion.Estado estadoEnum = Transaccion.Estado.valueOf(estado.toUpperCase());
            List<Transaccion> transacciones = transaccionRepository.findByEstadoOrderByFechaDesc(estadoEnum);
            return TransaccionDTO.fromEntityList(transacciones);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estado + ". Estados válidos: ACTIVA, ANULADA, PENDIENTE");
        }
    }
    
    /**
     * Anular una transacción
     */
    public TransaccionDTO anularTransaccion(Long id) {
        return cambiarEstadoTransaccion(id, "ANULADA");
    }
    
    /**
     * Reactivar una transacción
     */
    public TransaccionDTO reactivarTransaccion(Long id) {
        return cambiarEstadoTransaccion(id, "ACTIVA");
    }
    
    /**
     * Marcar transacción como pendiente
     */
    public TransaccionDTO marcarPendienteTransaccion(Long id) {
        return cambiarEstadoTransaccion(id, "PENDIENTE");
    }
} 