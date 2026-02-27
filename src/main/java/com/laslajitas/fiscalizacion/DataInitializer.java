package com.laslajitas.fiscalizacion;

import com.laslajitas.fiscalizacion.enums.EstadoTramite;
import com.laslajitas.fiscalizacion.enums.Rol;
import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.entity.Tramite;
import com.laslajitas.fiscalizacion.entity.Usuario;
import com.laslajitas.fiscalizacion.repository.TramiteRepository;
import com.laslajitas.fiscalizacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TramiteRepository tramiteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        inicializarUsuarios();
        inicializarTramites();
    }

    // ─── Usuarios ──────────────────────────────────────────────────────────────

    private void inicializarUsuarios() {
        if (usuarioRepository.count() > 0) return;

        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setEmail("admin@laslajitas.gob.ar");
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);

        System.out.println("Usuario admin creado: admin / admin123");
    }

    // ─── Trámites de prueba ────────────────────────────────────────────────────

    private void inicializarTramites() {
        if (tramiteRepository.count() > 0) return;

        tramiteRepository.saveAll(List.of(
                crearHabilitacion("Juan Pérez (Kiosco El Paso)",
                        "Habilitación comercial", EstadoTramite.INSPECCION_PENDIENTE,
                        LocalDate.now().minusDays(2), "Av. San Martín 123", "40m2",
                        false, true, true),

                crearHabilitacion("Librería Central",
                        "Renovación anual", EstadoTramite.FINALIZADO,
                        LocalDate.now().minusDays(1), "Calle Belgrano 45", "80m2",
                        true, true, true),

                crearHabilitacion("Despensa Los Amigos",
                        "Habilitación nueva", EstadoTramite.INSPECCION_PENDIENTE,
                        LocalDate.now(), "Barrio Obrero Mz C", "30m2",
                        true, true, false),

                crearAlcohol("Bar La Esquina",
                        "Renovación licencia alcohol", EstadoTramite.EN_POLICIA,
                        LocalDate.now().minusDays(4), "Bar", "20:00 - 04:00"),

                crearAlcohol("Vinería El Tonel",
                        "Licencia nueva", EstadoTramite.EN_POLICIA,
                        LocalDate.now().minusDays(35), "Vinería", "09:00 - 22:00"),

                crearAlcohol("Club Social",
                        "Permiso eventual", EstadoTramite.EN_POLICIA,
                        LocalDate.now().minusDays(32), "Club Social", "10:00 - 02:00"),

                crearAlcohol("Restaurante El Sol",
                        "Renovación", EstadoTramite.EN_POLICIA,
                        LocalDate.now().minusDays(5), "Restaurante", "12:00 - 00:00"),

                crearEvento("Festival del Maíz",
                        "Permiso para evento masivo", EstadoTramite.PENDIENTE,
                        LocalDate.of(2026, 6, 15), "Plaza Central"),

                crearEvento("Fiesta de la Cerveza",
                        "Evento privado", EstadoTramite.PENDIENTE,
                        LocalDate.of(2026, 7, 20), "Predio Ferial"),

                crearEvento("Feria de Artesanos",
                        "Uso de espacio público", EstadoTramite.PENDIENTE,
                        LocalDate.of(2026, 8, 22), "Peatonal San Martín"),

                crearMulta("Terreno Baldío (Calle San Martín 400)",
                        "Multa por falta de limpieza", EstadoTramite.VENCIDA,
                        LocalDate.now().minusDays(9), LocalDate.now().minusDays(2),
                        "ACT-001", "Art. 45 Ord. 1234", BigDecimal.valueOf(15000.00)),

                crearMulta("Ferretería El Martillo",
                        "Multa por obstrucción de vía pública", EstadoTramite.ACTA_CARGADA,
                        LocalDate.now().minusDays(3), null,
                        "ACT-002", "Art. 62 Ord. 1234", BigDecimal.valueOf(8000.00)),

                crearNotificacion("Supermercado El Ahorro",
                        "Notificación por ruidos molestos", EstadoTramite.DILIGENCIADA,
                        LocalDate.now().minusDays(10), "Av. San Martín 800",
                        "CED-001", "García Roberto"),

                crearNotificacion("Taller Mecánico Tito",
                        "Aviso de vencimiento de habilitación", EstadoTramite.PENDIENTE,
                        LocalDate.now().minusDays(1), "Calle Salta 200",
                        "CED-002", "López Ana")
        ));

        System.out.println("Datos de prueba inicializados!");
    }

    // ─── Métodos auxiliares de construcción ───────────────────────────────────

    private Tramite crearHabilitacion(String solicitante, String descripcion,
                                      EstadoTramite estado, LocalDate fecha, String direccion,
                                      String dimensiones, Boolean esPropietario,
                                      Boolean tieneElectricidad, Boolean tieneGas) {
        Tramite t = new Tramite();
        t.setTipo(TipoTramite.COMERCIAL);
        t.setSolicitante(solicitante);
        t.setDescripcion(descripcion);
        t.setEstado(estado);
        t.setFecha(fecha);
        t.setDireccionLocal(direccion);
        t.setDimensiones(dimensiones);
        t.setEsPropietario(esPropietario);
        t.setTieneElectricidad(tieneElectricidad);
        t.setTieneGas(tieneGas);
        return t;
    }

    private Tramite crearAlcohol(String solicitante, String descripcion,
                                 EstadoTramite estado, LocalDate fecha,
                                 String categoria, String horarioVenta) {
        Tramite t = new Tramite();
        t.setTipo(TipoTramite.ALCOHOL);
        t.setSolicitante(solicitante);
        t.setDescripcion(descripcion);
        t.setEstado(estado);
        t.setFecha(fecha);
        t.setFechaVencimiento(fecha.plusYears(1));
        t.setCategoria(categoria);
        t.setHorarioVenta(horarioVenta);
        // Simular que salió hacia la Policía el mismo día del ingreso
        if (estado == EstadoTramite.EN_POLICIA) {
            t.setFechaSalidaPolicia(fecha);
        }
        return t;
    }

    private Tramite crearEvento(String solicitante, String descripcion,
                                EstadoTramite estado, LocalDate fechaEvento, String lugar) {
        Tramite t = new Tramite();
        t.setTipo(TipoTramite.EVENTO);
        t.setSolicitante(solicitante);
        t.setDescripcion(descripcion);
        t.setEstado(estado);
        t.setFecha(LocalDate.now());
        t.setFechaEvento(fechaEvento);
        t.setLugarEvento(lugar);
        return t;
    }

    private Tramite crearMulta(String solicitante, String descripcion,
                               EstadoTramite estado, LocalDate fecha, LocalDate fechaVencimiento,
                               String numeroActa, String articulo, BigDecimal monto) {
        Tramite t = new Tramite();
        t.setTipo(TipoTramite.MULTA);
        t.setSolicitante(solicitante);
        t.setDescripcion(descripcion);
        t.setEstado(estado);
        t.setFecha(fecha);
        t.setFechaVencimiento(fechaVencimiento);
        t.setNumeroActa(numeroActa);
        t.setArticuloOrdenanza(articulo);
        t.setMonto(monto);
        return t;
    }

    private Tramite crearNotificacion(String solicitante, String descripcion,
                                      EstadoTramite estado, LocalDate fecha, String direccion,
                                      String numeroCedula, String notificador) {
        Tramite t = new Tramite();
        t.setTipo(TipoTramite.NOTIFICACION);
        t.setSolicitante(solicitante);
        t.setDescripcion(descripcion);
        t.setEstado(estado);
        t.setFecha(fecha);
        t.setDireccionLocal(direccion);
        t.setNumeroCedula(numeroCedula);
        t.setNotificador(notificador);
        t.setFechaLimitePlazo(fecha.plusDays(15));
        return t;
    }
}