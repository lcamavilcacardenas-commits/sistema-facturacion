package com.facturacion.sistema.repository;

import com.facturacion.sistema.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// -----------------------------------------------------------------------
// PRUEBA GIT: Repositorio modificado por Lucia Perez el 02-12-2025
// -----------------------------------------------------------------------




@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // Métodos personalizados aquí
}