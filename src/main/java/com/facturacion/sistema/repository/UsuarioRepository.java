package com.facturacion.sistema.repository;

import com.facturacion.sistema.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Buscar por correo para el login (JWT)
    Optional<Usuario> findByCorreo(String correo);
}