package com.facturacion.sistema.config;

import com.facturacion.sistema.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    // Inyecta el repositorio para buscar usuarios en la base de datos
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Define cómo Spring Security buscará los detalles del usuario.
     * Asume que la clase 'Usuario.java' implementa 'UserDetails'.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Usa una expresión lambda que busca en el repositorio por correo.
        return username -> usuarioRepository.findByCorreo(username)
                // Si el usuario no existe, lanza la excepción estándar de Spring Security.
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
    }

    /**
     * Define el mecanismo de autenticación (comparar credenciales).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 1. Le dice dónde buscar al usuario.
        authProvider.setUserDetailsService(userDetailsService());

        // 2. Le dice cómo comparar la contraseña codificada.
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Expone el AuthenticationManager para ser usado en AuthService.java.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el codificador de contraseñas. DEBE coincidir con el usado en la BD (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}