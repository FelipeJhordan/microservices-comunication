package br.com.cursoudemy.productapi.modules.category.repository;

import br.com.cursoudemy.productapi.modules.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value="SELECT * FROM category where category.description ilike %:description%", nativeQuery = true)
    List<Category> findByDescriptionContainingIgnoreCase(@Param("description") String description);

}
