package com.epam.caloriecounter;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;

@ApiModel
public enum FoodType {

    BRANDED("Branded"),
    FOUNDATION("Foundation"),
    SR_LEGACY("Survey (FNDDS)"),
    SURVEY("SR Legacy");

    private final String type;

    FoodType(String type) {
        this.type = type;
    }

    @JsonCreator
    public static FoodType findByType(String byLabel) {
        for (FoodType t : FoodType.values()) {
            if (t.type.equalsIgnoreCase(byLabel))
                return t;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return type;
    }

    public String getType() {
        return type;
    }
}
