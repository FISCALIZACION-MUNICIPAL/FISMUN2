package com.laslajitas.fiscalizacion.service;

import com.laslajitas.fiscalizacion.enums.EstadoTramite;
import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.entity.Tramite;
import com.laslajitas.fiscalizacion.repository.TramiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TramiteService {

    @Autowired
    private TramiteRepository tramitesRepository;

    // ─── Consultas generales ───────────────────────────────────────────────────

    /**
     * Lista todos los trámites de un tipo, ordenados por fecha descendente.
     * Usa query directa en BD, sin cargar todos los registros a memoria.
     */
    public List<Tramite> findByTipo(TipoTramite tipo) {
        return tramitesRepository.findByTipoOrderByFechaDesc(tipo);
    }

    /**
     * Lista trámites de un tipo filtrados por estado.
     */
    public List<Tramite> findByTipoYEstado(TipoTramite tipo, EstadoTramite estado) {
        return tramitesRepository.findByTipoAndEstadoOrderByFechaDesc(tipo, estado);
    }

    /**
     * Busca trámites de un tipo por nombre de solicitante (parcial, sin distinguir mayúsculas).
     */
    public List<Tramite> buscarPorSolicitante(TipoTramite tipo, String texto) {
        if (texto == null || texto.isBlank()) {
            return findByTipo(tipo);
        }
        return tramitesRepository.findByTipoAndSolicitanteContainingIgnoreCaseOrderByFechaDesc(tipo, texto);
    }

    /**
     * Búsqueda global por solicitante o DNI (para un buscador general).
     */
    public List<Tramite> buscarGlobal(String texto) {
        if (texto == null || texto.isBlank()) {
            return tramitesRepository.findTop5ByOrderByFechaDesc();
        }
        return tramitesRepository.buscarPorTexto(texto);
    }

    public Tramite findById(Long id) {
        return tramitesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trámite no encontrado con id: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        Tramite tramite = findById(id);
        tramitesRepository.delete(tramite);
    }

    // ─── Guardado por tipo ─────────────────────────────────────────────────────

    @Transactional
    public Tramite guardarHabilitacion(Tramite tramite) {
        tramite.setTipo(TipoTramite.COMERCIAL);
        if (tramite.getId() != null) {
            tramite.setFecha(findById(tramite.getId()).getFecha());
        } else {
            if (tramite.getEstado() == null) tramite.setEstado(EstadoTramite.INSPECCION_PENDIENTE);
            tramite.setFecha(LocalDate.now());
        }
        Tramite guardado = tramitesRepository.save(tramite);
        verificarYCrearNotificacion(guardado);
        return guardado;
    }

    @Transactional
    public Tramite guardarAlcohol(Tramite tramite) {
        tramite.setTipo(TipoTramite.ALCOHOL);
        if (tramite.getId() != null) {
            tramite.setFecha(findById(tramite.getId()).getFecha());
        } else {
            if (tramite.getEstado() == null) tramite.setEstado(EstadoTramite.PENDIENTE);
            tramite.setFecha(LocalDate.now());
            tramite.setFechaVencimiento(LocalDate.now().plusYears(1));
        }
        Tramite guardado = tramitesRepository.save(tramite);
        verificarYCrearNotificacion(guardado);
        return guardado;
    }

    @Transactional
    public Tramite guardarEvento(Tramite tramite) {
        tramite.setTipo(TipoTramite.EVENTO);
        if (tramite.getId() != null) {
            tramite.setFecha(findById(tramite.getId()).getFecha());
        } else {
            if (tramite.getEstado() == null) tramite.setEstado(EstadoTramite.PENDIENTE);
            tramite.setFecha(LocalDate.now());
        }
        Tramite guardado = tramitesRepository.save(tramite);
        verificarYCrearNotificacion(guardado);
        return guardado;
    }

    @Transactional
    public Tramite guardarMulta(Tramite tramite) {
        tramite.setTipo(TipoTramite.MULTA);
        if (tramite.getId() != null) {
            tramite.setFecha(findById(tramite.getId()).getFecha());
        } else {
            if (tramite.getEstado() == null) tramite.setEstado(EstadoTramite.PENDIENTE);
            tramite.setFecha(LocalDate.now());
        }
        Tramite guardado = tramitesRepository.save(tramite);
        verificarYCrearNotificacion(guardado);
        return guardado;
    }

    @Transactional
    public Tramite guardarNotificacion(Tramite tramite) {
        tramite.setTipo(TipoTramite.NOTIFICACION);
        if (tramite.getId() != null) {
            tramite.setFecha(findById(tramite.getId()).getFecha());
        } else {
            if (tramite.getEstado() == null) tramite.setEstado(EstadoTramite.PENDIENTE);
            tramite.setFecha(LocalDate.now());
        }
        return tramitesRepository.save(tramite);
    }

    // ─── Lógica de negocio ─────────────────────────────────────────────────────

    /**
     * Si un trámite pasa a FINALIZADO, genera automáticamente una notificación
     * para el solicitante. Ambas operaciones van en la misma transacción.
     */
    @Transactional
    public void verificarYCrearNotificacion(Tramite tramite) {
        if (tramite.getEstado() == EstadoTramite.FINALIZADO) {
            Tramite notificacion = new Tramite();
            notificacion.setTipo(TipoTramite.NOTIFICACION);
            notificacion.setSolicitante(tramite.getSolicitante());
            notificacion.setDni(tramite.getDni());
            notificacion.setEmail(tramite.getEmail());
            notificacion.setTelefono(tramite.getTelefono());
            notificacion.setLocalidad(tramite.getLocalidad());
            notificacion.setDescripcion("Su trámite de tipo " + tramite.getTipo()
                    + " ha finalizado. Por favor pase a retirar su certificado.");
            notificacion.setEstado(EstadoTramite.PENDIENTE);
            notificacion.setFecha(LocalDate.now());
            tramitesRepository.save(notificacion);
        }
    }

    /**
     * Devuelve todos los trámites cuya fecha de vencimiento ya pasó
     * y no están finalizados. Útil para alertas o tareas programadas.
     */
    public List<Tramite> findVencidos() {
        return tramitesRepository.findVencidos();
    }

    // ─── Estadísticas para dashboard y reportes ────────────────────────────────

    public long countHabilitacionesPendientes() {
        return tramitesRepository.countByEstado(EstadoTramite.INSPECCION_PENDIENTE);
    }

    public long countTramitesAlcoholDemorados() {
        return tramitesRepository.countByTipoAndEstado(TipoTramite.ALCOHOL, EstadoTramite.EN_POLICIA);
    }

    public long countEventosProximos() {
        return tramitesRepository.findProximosEventosByTipo(TipoTramite.EVENTO).size();
    }

    public Tramite getProximoEvento() {
        List<Tramite> eventos = tramitesRepository.findProximosEventosByTipo(TipoTramite.EVENTO);
        return eventos.isEmpty() ? null : eventos.get(0);
    }

    public List<Tramite> getRecentActivity() {
        return tramitesRepository.findTop5ByOrderByFechaDesc();
    }

    public long countTramitesByTipo(TipoTramite tipo) {
        return tramitesRepository.countByTipo(tipo);
    }

    public long countTramitesByEstado(EstadoTramite estado) {
        return tramitesRepository.countByEstado(estado);
    }

    public BigDecimal sumMontoMultasFinalizadas() {
        return tramitesRepository.sumMontoMultasFinalizadas();
    }
}