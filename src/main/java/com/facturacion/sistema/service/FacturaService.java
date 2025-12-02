package com.facturacion.sistema.service;

import com.facturacion.sistema.model.Cliente;
import com.facturacion.sistema.model.DetalleFactura;
import com.facturacion.sistema.model.Factura;
import com.facturacion.sistema.model.Producto;
import com.facturacion.sistema.repository.ClienteRepository;
import com.facturacion.sistema.repository.FacturaRepository;
import com.facturacion.sistema.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaService {

    private static final double IGV_RATE = 0.18; // 18% para Perú (a modo de ejemplo)

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository; // Asumo que ya existe

    /**
     * SIMULACIÓN DE CONSULTA EXTERNA (RENIEC/SUNAT)
     * Busca un cliente por su documento. Si no existe, crea un cliente de prueba
     * para simular la obtención de datos de una API externa.
     * @param documento DNI o RUC.
     * @return Cliente encontrado o simulado.
     */
    @Transactional
    public Cliente buscarOCrearCliente(String documento) {
        return clienteRepository.findByDocumentoIdentidad(documento)
                .orElseGet(() -> {
                    // Si no lo encuentra, SIMULAMOS que lo obtuvimos de la API de RENIEC/SUNAT
                    Cliente nuevoCliente = new Cliente();
                    nuevoCliente.setDocumentoIdentidad(documento);

                    if (documento.length() == 8) { // Ejemplo DNI (8 dígitos)
                        nuevoCliente.setTipoDocumento("BOLETA / DNI");
                        nuevoCliente.setNombreRazonSocial("Cliente DNI Generico " + documento.substring(4));
                        nuevoCliente.setDireccion("Av. Simulada #123");
                    } else if (documento.length() == 11) { // Ejemplo RUC (11 dígitos)
                        nuevoCliente.setTipoDocumento("FACTURA / RUC");
                        nuevoCliente.setNombreRazonSocial("Empresa RUC Generica S.A.C.");
                        nuevoCliente.setDireccion("Calle RUC Principal #456");
                    } else {
                        throw new IllegalArgumentException("Documento no válido. Debe ser DNI (8) o RUC (11).");
                    }

                    return clienteRepository.save(nuevoCliente);
                });
    }

    /**
     * Crea y guarda una nueva factura, calculando los totales e IGV.
     * NOTA: Este método recibe una Factura pre-poblada con Cliente y Detalles.
     */
    @Transactional
    public Factura crearFactura(Factura factura) {
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle.");
        }

        double subtotal = 0.0;

        // 1. Recalcula los totales de línea y el subtotal general
        for (DetalleFactura detalle : factura.getDetalles()) {
            // Asegúrate de que el producto exista para obtener el precio
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado. ID: " + detalle.getProducto().getId()));

            detalle.setPrecioUnitario(producto.getPrecio());
            double totalLinea = detalle.getCantidad() * detalle.getPrecioUnitario();
            detalle.setTotalLinea(totalLinea);
            detalle.setFactura(factura); // Asocia el detalle a la factura

            subtotal += totalLinea;
        }

        // 2. Calcula IGV y Total
        double igvCalculado = subtotal * IGV_RATE;
        double totalCalculado = subtotal + igvCalculado;

        // 3. Establece los valores en la factura
        factura.setSubtotal(subtotal);
        factura.setIgv(igvCalculado);
        factura.setTotal(totalCalculado);
        factura.setFechaEmision(LocalDateTime.now());

        // 4. Genera un número de comprobante simple (para ejemplo)
        factura.setNumeroComprobante(generarNumeroComprobante(factura.getTipoComprobante()));

        // 5. Guarda la factura y sus detalles
        return facturaRepository.save(factura);
    }

    private String generarNumeroComprobante(String tipo) {
        String prefijo = tipo.equalsIgnoreCase("FACTURA") ? "F001" : "B001";
        // En una app real se buscaría el último número, aquí usamos un UUID simple
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefijo + "-" + sufijo;
    }
}