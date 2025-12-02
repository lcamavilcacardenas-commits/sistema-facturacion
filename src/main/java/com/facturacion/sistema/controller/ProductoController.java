package com.facturacion.sistema.controller;

import com.facturacion.sistema.model.Producto;
import com.facturacion.sistema.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Endpoint para obtener todos los productos.
     * Requiere cualquier usuario autenticado.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Producto> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    /**
     * Endpoint para guardar un nuevo producto.
     * Solo permitido para usuarios con rol 'admin'.
     */
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.guardarProducto(producto);
        return ResponseEntity.ok(nuevoProducto);
    }
}