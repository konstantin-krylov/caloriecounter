package com.epam.caloriecounter.repository;

import com.epam.caloriecounter.entity.FoodNutrient;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface FoodNutrientRepository extends JpaRepository<FoodNutrient, Long> {
}
