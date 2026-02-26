package com.laslajitas.fiscalizacion.entity;

import com.laslajitas.fiscalizacion.enums.EstadoTramite;
import com.laslajitas.fiscalizacion.enums.TipoTramite;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "tramites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTramite tipo;

    @Column(nullable = false)
    private String solicitante;

    @Column(length = 20)
    private String dni;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String localidad;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTramite estado;

    @Column(nullable = false)
    private LocalDate fecha;

    private LocalDate fechaVencimiento;

    // Campos específicos para Habilitación Comercial
    @Column(length = 255)
    private String direccionLocal;

    @Column(length = 50)
    private String dimensiones;

    private Boolean esPropietario; // true = Propietario, false = Inquilino

    private Boolean tieneElectricidad;

    private Boolean tieneGas;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    // Campos específicos para Alcohol
    @Column(length = 50)
    private String categoria;

    @Column(length = 100)
    private String horarioVenta;
}
