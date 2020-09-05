package com.epam.caloriecounter.repository;

import com.epam.caloriecounter.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface FoodRepository extends JpaRepository<Food, Long> {
}
