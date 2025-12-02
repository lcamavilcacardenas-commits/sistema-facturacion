package com.facturacion.sistema.repository;

import com.facturacion.sistema.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Métodos de búsqueda adicionales si fueran necesarios.
}