package com.facturacion.sistema;

import com.facturacion.sistema.service.UsuarioService;
import com.facturacion.sistema.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaApplication implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    public static void main(String[] args) {
        SpringApplication.run(SistemaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Ejecuta la inicialización de usuarios y productos
        usuarioService.inicializarUsuarios();
        productoService.inicializarProductos();
    }
}