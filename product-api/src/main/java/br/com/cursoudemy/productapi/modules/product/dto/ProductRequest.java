package br.com.cursoudemy.productapi.modules.product.dto;

import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.product.model.Product;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Data
public class ProductRequest {

    private String name;
    private Integer quantityAvailable;
    private Integer supplierId;
    private Integer categoryId;
    public static Product toEntity(ProductRequest request, Category c, Supplier s) {
        return Product.builder()
                .name(request.getName())
                .quantityAvailable(request.getQuantityAvailable())
                .category(c)
                .supplier(s).build();
    }
}
