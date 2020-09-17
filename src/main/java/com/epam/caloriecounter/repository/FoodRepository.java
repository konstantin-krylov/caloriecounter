package com.epam.caloriecounter.repository;

import com.epam.caloriecounter.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface FoodRepository extends JpaRepository<Food, Long> {
    Food findByFdcId(Long fdcId);

    //    @Query(value="SELECT Food.fdcId FROM Food WHERE Food.fdcId is not null")
//    List<Long> getAllIds();
    @Query("select p.fdcId from #{#entityName} p")
    List<Long> getAllIds();
}
