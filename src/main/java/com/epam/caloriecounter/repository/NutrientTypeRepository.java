package com.epam.caloriecounter.repository;

import com.epam.caloriecounter.entity.NutrientType;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface NutrientTypeRepository extends JpaRepository<NutrientType, Long> {
    NutrientType findByNutrientName(String nutrientName);
}
