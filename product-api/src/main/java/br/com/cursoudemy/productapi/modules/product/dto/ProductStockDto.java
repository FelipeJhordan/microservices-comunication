package br.com.cursoudemy.productapi.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDto {

    private String salesId;

    private List<ProductQuantityDto> products;
}
