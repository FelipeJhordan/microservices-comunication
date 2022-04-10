package br.com.cursoudemy.productapi.modules.supplier.repository;

import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    @Query(value="SELECT * FROM supplier where supplier.name ilike %:name%", nativeQuery = true)
    List<Supplier> findByNameIgnoreCaseContaining(String name);
}
