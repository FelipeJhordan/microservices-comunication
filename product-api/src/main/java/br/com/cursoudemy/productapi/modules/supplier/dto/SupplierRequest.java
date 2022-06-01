package br.com.cursoudemy.productapi.modules.supplier.dto;

import br.com.cursoudemy.productapi.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SupplierRequest {
    private String name;


    public static Supplier toEntity(SupplierRequest request) {
        var supplier  = new Supplier();
        BeanUtils.copyProperties(request, supplier);

        return supplier;
    }
}
