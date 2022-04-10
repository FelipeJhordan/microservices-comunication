package br.com.cursoudemy.productapi.modules.supplier.service;


import br.com.cursoudemy.productapi.config.exception.ValidationException;
import br.com.cursoudemy.productapi.config.message.SuccessResponse;
import br.com.cursoudemy.productapi.modules.product.service.ProductService;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierRequest;
import br.com.cursoudemy.productapi.modules.supplier.dto.SupplierResponse;
import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import br.com.cursoudemy.productapi.modules.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;


@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductService productService;

    public SupplierResponse save(SupplierRequest request) {
        validateSupplierNameInformed(request);
        var supplier = supplierRepository.save(SupplierRequest.toEntity(request));

        return SupplierResponse.of(supplier);
    }

    public Supplier findById(Integer id) {
        return supplierRepository
                .findById(id)
                .orElseThrow( () -> new ValidationException("There´s no supplier for the given ID."));
    }

    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplier -> SupplierResponse.of(supplier))
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByName(String name) {
        if(isEmpty(name)) {
            throw new ValidationException("The supplier name must be informed.");
        }
        return supplierRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        validateInformedId(id);
        var supplier =  supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There´s no supplier for the given ID."));

        return SupplierResponse.of(supplier);
    }

    private void validateSupplierNameInformed(SupplierRequest request) {
        if(isEmpty(request.getName())) {
            throw new ValidationException("The supplier´s name was not informed");
        }
    }

    public SuccessResponse delete(Integer id) {
        validateInformedId(id);

        if(productService.existsBySupplierId(id)) {
            throw  new ValidationException("You cannot delete this supplier because it´s already defined by a product.");
        }

        supplierRepository.deleteById(id);

        return SuccessResponse.create("The supplier was deleted.");
    }

    public SupplierResponse updateById(SupplierRequest request, Integer id) {
        validateSupplierNameInformed(request);
        validateInformedId(id);
        var supplier = SupplierRequest.toEntity(request);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    private void validateInformedId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException(("The supplier ID must be informed."));
        }
    }

}
