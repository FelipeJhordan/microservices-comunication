package br.com.cursoudemy.productapi.modules.sales.dto;

import br.com.cursoudemy.productapi.modules.sales.enums.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesConfirmationDto {
    private String salesId;
    private SalesStatus status;
}
