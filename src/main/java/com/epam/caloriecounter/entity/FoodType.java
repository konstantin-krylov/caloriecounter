package com.epam.caloriecounter.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity
@Table(name = "food_type")
public class FoodType {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "food_type_seq_gen")
    @SequenceGenerator(name = "food_type_seq_gen", sequenceName = "food_type_food_type_id_seq", allocationSize = 1)
    private Long foodTypeId;

    @Column(name = "food_type")
    private String foodType;
}
