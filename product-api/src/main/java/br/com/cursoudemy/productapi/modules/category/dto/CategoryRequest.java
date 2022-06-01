package br.com.cursoudemy.productapi.modules.category.dto;

import br.com.cursoudemy.productapi.modules.category.model.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CategoryRequest {
    private String description;

    public static Category toEntity(CategoryRequest request) {
        var category = new Category();

        BeanUtils.copyProperties(request, category);

        return category;
    }
}
