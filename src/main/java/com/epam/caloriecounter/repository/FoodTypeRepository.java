package com.epam.caloriecounter.repository;

import com.epam.caloriecounter.entity.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface FoodTypeRepository extends JpaRepository<FoodType, Long> {
}
