package br.com.cursoudemy.productapi.modules.product.service;

import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.config.message.SuccessResponse;
import br.com.cursoudemy.productapi.modules.category.service.CategoryService;
import br.com.cursoudemy.productapi.modules.product.dto.*;
import br.com.cursoudemy.productapi.modules.product.model.Product;
import br.com.cursoudemy.productapi.modules.product.repository.ProductRepository;
import br.com.cursoudemy.productapi.modules.sales.client.SalesClient;
import br.com.cursoudemy.productapi.modules.sales.dto.SalesConfirmationDto;
import br.com.cursoudemy.productapi.modules.sales.enums.SalesStatus;
import br.com.cursoudemy.productapi.modules.supplier.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import br.com.cursoudemy.productapi.modules.sales.rabbitmq.SalesConfirmationSender;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    @Lazy
    private SupplierService supplierService;

    @Autowired
    @Lazy
    private CategoryService categoryService;

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    @Autowired
    private SalesClient salesClient;

    public ProductResponse save(ProductRequest request) {
        validateProductDataInformed(request);
        validateCategoryAndSupplierInformed(request);

        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());

        var product =  productRepository.save(ProductRequest.toEntity(request, category, supplier));

        return ProductResponse.of(product);
    }

    public ProductResponse updateById(ProductRequest request, Integer id) {
        validateProductDataInformed(request);
        validateInformedId(id);
        validateCategoryAndSupplierInformed(request);

        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = ProductRequest.toEntity(request, category, supplier);

        product.setId(id);
        productRepository.save(product);

        return ProductResponse.of(product);
    }


    public Product findById(Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow( () -> new ValidationException("There´s no product for the given ID."));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(product -> ProductResponse.of(product))
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByName(String name) {
        if(isEmpty(name)) {
            throw new ValidationException("The product name must be informed.");
        }
        return productRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if(isEmpty(supplierId)) {
            throw new ValidationException("The product´s supplier id must be informed.");
        }
        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if(isEmpty(categoryId)) {
            throw new ValidationException("The product´s category id must be informed.");
        }
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        if(isEmpty(id)) {
            throw  new ValidationException("The product id must be informed.");
        }
        var product =  productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There´s no product for the given ID."));

        return ProductResponse.of(product);
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);


        productRepository.deleteById(id);

        return SuccessResponse.create("The product was deleted.");
    }

    public void updateProductStock(ProductStockDto product) {
        try {
            validateStockUpdateData(product);
            updateStock(product);
        } catch(Exception e) {
            log.error("Error while trying to update stock for message with error {}", e.getMessage(), e);
            salesConfirmationSender.sendSalesConfirmationMessage(new SalesConfirmationDto(product.getSalesId() , SalesStatus.REJECT));
        }
    }

    @Transactional
    private void updateStock(ProductStockDto product) {
        var productsForUpdate = new ArrayList<Product>();
        product.getProducts().forEach(salesProduct -> {
            var existingProduct = findById(salesProduct.getProductId());
            if(salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
                throw new ValidationException(String.format("The product %s is out of stock", existingProduct.getId()));
            }

            existingProduct.decreaseStockProduct(salesProduct.getQuantity());
            productsForUpdate.add(existingProduct);
    });

        if(!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDto(product.getSalesId(), SalesStatus.APPROVED);

            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
}


    public SuccessResponse checkProductStock(ProductCheckStockRequest request) {
        if(isEmpty(request) || isEmpty(request.getProducts())) {
            throw  new ValidationException("The request data and products must be informed");
        }

        request
                .getProducts()
                .forEach(this::validateStock);
        return SuccessResponse.create("The stock is ok");
    }

    private void validateStock(ProductQuantityDto productQuantityDto) {
        if(isEmpty(productQuantityDto.getProductId()) || isEmpty(productQuantityDto.getQuantity())) {
            throw new ValidationException("Product ID and quantity must be informed");
        }

        var product = findById(productQuantityDto.getProductId());

        if(product.getQuantityAvailable() < productQuantityDto.getQuantity()) {
              throw new ValidationException(String.format("The product %s is out of stock", product.getId()));
        }
    }

    private void validateStockUpdateData(ProductStockDto product) {
        if(isEmpty(product) || isEmpty(product.getSalesId())) {
            throw  new ValidationException("The product data or sales ID cannot be null.");
        }

        if(isEmpty(product.getProducts())) {
            throw new ValidationException("The sale´s products must be informed");
        }

        product.getProducts()
                .forEach(salesProduct -> {
                    if(isEmpty(salesProduct.getProductId()) || isEmpty(salesProduct.getProductId())) {
                        throw new ValidationException("The productID and quantity must be informed");
                    }
                });
    }


    private void validateInformedId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException(("The Product ID must be informed."));
        }
    }


    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    private void validateCategoryAndSupplierInformed(ProductRequest request) {
        if ( isEmpty(request.getCategoryId())) {
            throw new ValidationException("The category ID was not informed.");
        }

        if(isEmpty(request.getSupplierId())) {
            throw new ValidationException("The supplier ID was not informed.");
        }
    }

    private void validateProductDataInformed(ProductRequest request) {
        if(isEmpty(request.getName())) {
            throw new ValidationException("The product name was not informed");
        }

        if(isEmpty(request.getQuantityAvailable())) {
            throw new ValidationException("The product quantity was not informed");
        }

        if(request.getQuantityAvailable() <= 0) {
            throw new ValidationException("The quantity should not be equal to zero");
        }
    }

    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);

        try {
            var sales = salesClient.findSalesByProductId(id).orElseThrow(()-> new ValidationException("The sales was not found by this product."));

            return ProductSalesResponse.of(product, sales.getSalesIds());
        } catch(Exception e) {
            throw new ValidationException("There was an error trying to get the product´s sales.");
        }
    }

}
