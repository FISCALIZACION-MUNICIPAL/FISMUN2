package com.laslajitas.fiscalizacion.enums;

/**
 * Estados posibles de un trámite según el documento de delimitación de proyecto.
 * Cada grupo de estados corresponde a un proceso funcional del sistema.
 */
public enum EstadoTramite {

    // ─── Estados generales (aplican a todos los procesos) ─────────────────────
    PENDIENTE,              // Trámite ingresado, aún no procesado
    EN_PROCESO,             // Trámite siendo gestionado internamente
    FINALIZADO,             // Trámite concluido exitosamente
    VENCIDA,                // Fecha de vencimiento superada sin resolución

    // ─── Proceso 1: Habilitación Comercial ────────────────────────────────────
    INSPECCION_PENDIENTE,   // Requiere inspección física del local
    DEUDA_PENDIENTE,        // Tiene deuda en Rentas, se bloquea el avance
    APTO,                   // Local aprobado por Obras Públicas
    NO_APTO,                // Local rechazado por Obras Públicas

    // ─── Proceso 2: Habilitación por Venta de Alcohol ─────────────────────────
    EN_POLICIA,             // Expediente enviado a la Policía Provincial (caja negra)
    APROBADO_POLICIA,       // Policía otorgó el permiso de venta de alcohol
    RECHAZADO_POLICIA,      // Policía denegó el permiso de venta de alcohol

    // ─── Proceso 3: Habilitación de Eventos ───────────────────────────────────
    PENDIENTE_BOMBEROS,     // Esperando certificado antisiniestral de Bomberos
    APTO_BOMBEROS,          // Bomberos aprobó el lugar del evento
    NO_APTO_BOMBEROS,       // Bomberos rechazó el lugar del evento

    // ─── Proceso 4: Multas ────────────────────────────────────────────────────
    ACTA_CARGADA,           // Acta de infracción digitalizada, pendiente de derivación
    DERIVADO_JUZGADO,       // Expediente enviado al Tribunal de Faltas

    // ─── Proceso 5: Notificaciones Oficiales ──────────────────────────────────
    DILIGENCIADA,           // Cédula entregada exitosamente al destinatario
    NO_DILIGENCIADA,        // No se pudo entregar la cédula (ausente, dirección incorrecta)
    PLAZO_VENCIDO           // El plazo administrativo venció sin respuesta del notificado
}