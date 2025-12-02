package com.facturacion.sistema.repository;

import com.facturacion.sistema.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un cliente por su documento de identidad (DNI o RUC).
     * Esto simula la consulta que se haría a una API externa (RENIEC/SUNAT).
     * @param documentoIdentidad DNI o RUC del cliente.
     * @return Optional<Cliente>
     */
    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);
}