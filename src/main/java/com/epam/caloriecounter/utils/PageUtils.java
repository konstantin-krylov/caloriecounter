package com.epam.caloriecounter.utils;

import com.epam.caloriecounter.dto.ShortFoodDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtils {

    public static Page<ShortFoodDto> getPageFromList(List<ShortFoodDto> executionList, Pageable pageable, long maxSize) {
        int toIndex = (pageable.getOffset() + pageable.getPageSize() > executionList.size()) ?
                executionList.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        int fromIndex = toIndex > pageable.getOffset() ? (int) pageable.getOffset() : toIndex;
        return new PageImpl<>(
                executionList.subList(fromIndex, toIndex),
                pageable, maxSize
        );
    }

}
