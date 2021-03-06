package com.epam.caloriecounter.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static javax.persistence.GenerationType.SEQUENCE;

@Indexed
@Setter
@Getter
@Entity
@Table(name = "food_type")
public class FoodType {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "food_type_seq_gen")
    @SequenceGenerator(name = "food_type_seq_gen", sequenceName = "food_type_food_type_id_seq", allocationSize = 1)
    @JoinColumn(name = "food_type_id")
    private Long foodTypeId;

    @Column(name = "food_type_title", unique = true)
    private String foodTypeTitle;
}
