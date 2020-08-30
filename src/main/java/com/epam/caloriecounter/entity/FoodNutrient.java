package com.epam.caloriecounter.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@Table(name = "food_nutrient")
public class FoodNutrient {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "nutrient_seq_gen")
    @SequenceGenerator(name = "nutrient_seq_gen", sequenceName = "nutrient_nutrient_id_seq", allocationSize = 1)
    private Long nutrientId;

    @Column(name = "amount")
    private String amount;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "nutrient_type_id")
    private NutrientType nutrientType;

}
