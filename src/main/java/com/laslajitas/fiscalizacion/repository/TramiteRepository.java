package com.laslajitas.fiscalizacion.repository;

import com.laslajitas.fiscalizacion.enums.EstadoTramite;
import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.entity.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    List<Tramite> findByTipoOrderByFechaDesc(TipoTramite tipo);

    List<Tramite> findByTipoAndSolicitanteContainingIgnoreCaseOrderByFechaDesc(
            TipoTramite tipo, String solicitante);

    List<Tramite> findByTipoAndDniContainingOrderByFechaDesc(
            TipoTramite tipo, String dni);

    List<Tramite> findByTipoAndEstadoOrderByFechaDesc(TipoTramite tipo, EstadoTramite estado);


    long countByEstado(EstadoTramite estado);

    long countByTipo(TipoTramite tipo);

    long countByTipoAndEstado(TipoTramite tipo, EstadoTramite estado);


    List<Tramite> findTop5ByOrderByFechaDesc();

    @Query("SELECT t FROM Tramite t WHERE t.tipo = :tipo AND t.fecha >= CURRENT_DATE ORDER BY t.fecha ASC")
    List<Tramite> findProximosEventosByTipo(@Param("tipo") TipoTramite tipo);


    @Query("SELECT t FROM Tramite t WHERE t.fechaVencimiento IS NOT NULL AND t.fechaVencimiento < CURRENT_DATE AND t.estado != 'FINALIZADO'")
    List<Tramite> findVencidos();

    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Tramite t WHERE t.tipo = 'MULTA' AND t.estado = 'FINALIZADO' AND t.monto IS NOT NULL")
    BigDecimal sumMontoMultasFinalizadas();

    @Query("SELECT t FROM Tramite t WHERE " +
            "LOWER(t.solicitante) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "t.dni LIKE CONCAT('%', :texto, '%') " +
            "ORDER BY t.fecha DESC")
    List<Tramite> buscarPorTexto(@Param("texto") String texto);
}