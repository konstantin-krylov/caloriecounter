package com.epam.caloriecounter.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

@Indexed
@Setter
@Getter
@Entity
@Table(name = "food")
public class Food {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "food_seq_gen")
    @SequenceGenerator(name = "food_seq_gen", sequenceName = "food_food_id_seq", allocationSize = 1)
    @Column(name = "food_id")
    private Long foodId;

    @Field(termVector = TermVector.YES)
    @Column(name = "food_title")
    private String foodTitle;

    @Column(name = "fdc_id")
    private Long fdcId;

    @Column(name = "food_description")
    private String foodDescription;

    @Field
    @Column(name = "food_ingredients", length = 2000)
    private String foodIngredients;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "food_type_id")
    private FoodType foodType;

    @OneToMany(targetEntity = FoodNutrient.class, fetch = FetchType.EAGER,
            cascade = CascadeType.ALL, mappedBy = "food")
    private Set<FoodNutrient> foodNutrients;

}
