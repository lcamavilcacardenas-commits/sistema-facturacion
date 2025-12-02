package com.facturacion.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que simula el DNI o RUC. Usaremos este campo para la "consulta".
    @Column(unique = true, nullable = false)
    private String documentoIdentidad;

    @Column(nullable = false)
    private String nombreRazonSocial;

    private String direccion;

    private String tipoDocumento; // Ejemplo: "DNI" o "RUC"
}