package br.com.cursoudemy.productapi.modules.supplier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_id_generator")
    @SequenceGenerator(name = "supplier_id_generator", initialValue = 3)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;
}
