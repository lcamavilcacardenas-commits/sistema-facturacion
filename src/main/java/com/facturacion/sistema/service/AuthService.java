package com.facturacion.sistema.service;

import com.facturacion.sistema.model.AuthRequest;
import com.facturacion.sistema.model.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService; // Asegúrate de que tienes esto

    public AuthResponse login(AuthRequest request) {

        // 1. Intentar autenticar las credenciales (lanza excepción si falla)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );

        // 2. Si la autenticación es exitosa, cargar el usuario y generar el token
        // NOTA: userDetails es la clase que implementa UserDetails (probablemente Usuario.java)
        UserDetails user = userDetailsService.loadUserByUsername(request.getCorreo());

        // Asumiendo que JwtService tiene un método llamado generateToken
        String token = jwtService.getToken(user);

        // 3. Devolver la respuesta (requiere Lombok @Builder en AuthResponse.java)
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}