package com.facturacion.sistema.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroComprobante; // Ejemplo: F001-0000001

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDateTime fechaEmision = LocalDateTime.now();

    private String tipoComprobante; // Ejemplo: "FACTURA" o "BOLETA"

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double igv; // Impuesto General a las Ventas

    @Column(nullable = false)
    private Double total;

    // Relación con los detalles de la factura
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles;
}