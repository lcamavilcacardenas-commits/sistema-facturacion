package com.facturacion.sistema.controller;

import com.facturacion.sistema.model.Cliente;
import com.facturacion.sistema.model.Factura;
import com.facturacion.sistema.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // Endpoint para SIMULAR la consulta a RENIEC/SUNAT
    @GetMapping("/cliente/{documento}")
    public ResponseEntity<?> buscarCliente(@PathVariable String documento) {
        try {
            Cliente cliente = facturaService.buscarOCrearCliente(documento);
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar cliente: " + e.getMessage());
        }
    }

    // Endpoint para CREAR una nueva factura
    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody Factura factura) {
        try {
            Factura nuevaFactura = facturaService.crearFactura(factura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo de error específico si un Producto no existe
            if (e.getMessage().contains("Producto no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la factura: " + e.getMessage());
        }
    }
}