package com.facturacion.sistema.service;

import com.facturacion.sistema.model.Usuario;
import com.facturacion.sistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
// Eliminar esta importación: import org.springframework.security.core.userdetails.UserDetailsService;
// Eliminar esta importación: import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService { // *** YA NO IMPLEMENTA UserDetailsService ***

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- MÉTODOS DE LA APLICACIÓN ---

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        String passwordPlana = usuario.getPassword();
        String passwordHasheada = passwordEncoder.encode(passwordPlana);

        usuario.setPassword(passwordHasheada);

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("user");
        }

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    // =======================================================================
    // MÉTODO CORREGIDO: Inicializa usuarios de prueba (MANTENEMOS ESTO)
    // =======================================================================
    @Transactional
    public void inicializarUsuarios() {
        if (usuarioRepository.count() == 0) {
            String passwordHasheada = passwordEncoder.encode("123");

            // Lógica de inicialización (asumo que es para tu primer usuario en BD)
            // ... [código de creación de usuarios]
        }
    }


// --- IMPLEMENTACIÓN DE SPRING SECURITY (¡Eliminada por redundante!) ---

// ELIMINAR EL MÉTODO loadUserByUsername COMPLETO DE ESTE ARCHIVO
}