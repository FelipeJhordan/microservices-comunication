package br.com.cursoudemy.productapi.modules.category.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_generator")
    @SequenceGenerator(name = "category_id_generator", initialValue = 4)
    private Integer id;

    @Column(name = "description", nullable = false)
    private String description;
}
