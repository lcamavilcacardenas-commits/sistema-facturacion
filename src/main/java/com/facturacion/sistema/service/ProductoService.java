package com.facturacion.sistema.service;

import com.facturacion.sistema.model.Producto;
import com.facturacion.sistema.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // =======================================================================
    // MÉTODO DE INICIALIZACIÓN
    // =======================================================================
    @Transactional
    public void inicializarProductos() {
        if (productoRepository.count() == 0) {

            // Usando el constructor de 5 parámetros: (null/ID, nombre, descripcion, precio, stock)
            Producto prod1 = new Producto(
                    null,
                    "Laptop XYZ",
                    "Potente laptop para desarrollo y gaming.",
                    1200.00,
                    50
            );

            Producto prod2 = new Producto(
                    null,
                    "Mouse Óptico",
                    "Mouse ergonómico de alta precisión.",
                    15.50,
                    200
            );

            productoRepository.save(prod1);
            productoRepository.save(prod2);
        }
    }
}