package com.facturacion.sistema.controller;

// CORREGIDO: Importa directamente desde 'model' sin el subpaquete 'auth'
import com.facturacion.sistema.model.AuthRequest;

// CORREGIDO: Importa directamente desde 'model' sin el subpaquete 'auth'
import com.facturacion.sistema.model.AuthResponse;

// CORREGIDO: Importa directamente desde 'service' sin el subpaquete 'auth'
import com.facturacion.sistema.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Asegúrate de que el método 'login' existe en AuthService y devuelve AuthResponse
        return ResponseEntity.ok(authService.login(request));
    }
}