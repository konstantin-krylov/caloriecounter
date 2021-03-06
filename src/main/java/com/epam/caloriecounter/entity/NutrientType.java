package com.epam.caloriecounter.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

@Setter
@Getter
@Entity
@Table(name = "nutrient_type")
public class NutrientType {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "nutrient_type_seq_gen")
    @SequenceGenerator(name = "nutrient_type_seq_gen", sequenceName = "nutrient_type_nutrient_type_id_seq", allocationSize = 1)
    @JoinColumn(name = "nutrient_type_id")
    private Long nutrientTypeId;

    @Column(name = "nutrient_name", unique = true)
    private String nutrientName;

    @Column(name = "nutrient_number", unique = true)
    private String nutrientNumber;

    @Column(name = "unit_name")
    private String unitName;

}
