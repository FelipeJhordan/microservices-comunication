package br.com.cursoudemy.productapi.modules.category.service;


import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.config.message.SuccessResponse;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryRequest;
import br.com.cursoudemy.productapi.modules.category.dto.CategoryResponse;
import br.com.cursoudemy.productapi.modules.category.model.Category;
import br.com.cursoudemy.productapi.modules.category.repository.CategoryRepository;
import br.com.cursoudemy.productapi.modules.product.service.ProductService;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;

    public CategoryResponse findByIdResponse(Integer id) {
        validateInformedId(id);

        var category =  categoryRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There´s no category for the given ID."));

        return CategoryResponse.of(category);
    }

    public CategoryResponse save(CategoryRequest request) {
        validateCategoryNameInformed(request);
        var category = categoryRepository.save(CategoryRequest.toEntity(request));

        return CategoryResponse.of(category);
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponse.of(category))
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> findByDescription(String description) {
        if(isEmpty(description)) {
            throw new ValidationException("The category description must be informed.");
        }
        return categoryRepository.findByDescriptionContainingIgnoreCase(description)
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }


    public Category findById(Integer id) {
        return categoryRepository
                .findById(id)
                .orElseThrow( () -> new ValidationException("There´s no category for the given ID."));
    }

    private void validateCategoryNameInformed(CategoryRequest request) {
        if(isEmpty(request.getDescription())) {
            throw new ValidationException("The Category description was not informed.");
        }
    }


    public SuccessResponse delete(Integer id) {
        validateInformedId(id);

        if(productService.existsByCategoryId(id)) {
            throw  new ValidationException("You cannot delete this category because it´s already defined by a product.");
        }

        categoryRepository.deleteById(id);

        return SuccessResponse.create("The category was deleted.");
    }

    public CategoryResponse updateById(CategoryRequest request, Integer id) {
        validateCategoryNameInformed(request);

        validateInformedId(id);
        var category = CategoryRequest.toEntity(request);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    private void validateInformedId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException(("The category ID must be informed."));
        }
    }
}
