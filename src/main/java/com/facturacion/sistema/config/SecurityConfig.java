package com.facturacion.sistema.config;

// Importaciones estándar de Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Importaciones para manejar autoridades y roles
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;

// Importaciones para CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// Importación de tu filtro JWT
import com.facturacion.sistema.config.JwtAuthenticationFilter;

/**
 * Clase de configuración de seguridad principal de Spring Security.
 * Habilita la seguridad web, el soporte a @PreAuthorize y configura JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Inyección de dependencias
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // --- NUEVO BEAN PARA MANEJAR ROLES SIN PREFIJO 'ROLE_' ---
    /**
     * Configura el mapeador de autoridades para remover el prefijo 'ROLE_'.
     * Esto permite usar roles de la base de datos en minúsculas (ej: 'user')
     * con los métodos hasRole/hasAnyRole.
     */
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setPrefix(""); // Establece el prefijo a vacío
        authorityMapper.setConvertToUpperCase(true); // Opcional, pero recomendado: convierte los roles internos a mayúsculas
        return authorityMapper;
    }
    // --------------------------------------------------------

    /**
     * Define la cadena de filtros de seguridad para todas las peticiones HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuración CORS: Habilitar el bean de configuración
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Deshabilitar CSRF
                .csrf(csrf -> csrf.disable())

                // 3. Configuración de autorización de peticiones (Definición de rutas públicas y privadas)
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso público a la API de autenticación (Login/Registro)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Permite acceso a la consulta de clientes (simulación RENIEC/SUNAT)
                        .requestMatchers("/api/facturas/cliente/**").permitAll()

                        // CONFIGURACIÓN DE COMPRA (Necesita autorización de rol)
                        // Permite la compra (POST /api/comprar) a usuarios con los roles 'admin' o 'user'.
                        // Dado que el GrantedAuthoritiesMapper fue configurado (arriba),
                        // podemos usar los roles en minúsculas tal como están en la base de datos.
                        .requestMatchers(HttpMethod.POST, "/api/comprar").hasAnyRole("admin", "user", "cliente")

                        // Permite acceso público a la raíz y a todos los archivos estáticos del Front-end
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/*.css", "/*.js").permitAll()

                        // Todas las demás peticiones requieren autenticación (token JWT)
                        .anyRequest().authenticated()
                )

                // 4. Configuración de gestión de sesiones (STATELESS)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Añadir el AuthenticationProvider personalizado
                .authenticationProvider(authenticationProvider)

                // 6. Añadir el filtro JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    /**
     * Bean de configuración CORS. Define qué orígenes, métodos y cabeceras están permitidos.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (necesario para el desarrollo local)
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "https://www.google.com/search?q=http://127.0.0.1:8080", "http://localhost:3000"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeceras permitidas (incluyendo 'Authorization' para el token JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // Permite el envío de credenciales (cookies, etc., aunque usamos JWT)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas las rutas
        return source;
    }
}