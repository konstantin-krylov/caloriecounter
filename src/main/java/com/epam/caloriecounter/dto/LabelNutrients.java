package com.epam.caloriecounter.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LabelNutrients {
    private Fat fat;
    private SaturatedFat saturatedFat;
    private TransFat transFat;
    private Cholesterol cholesterol;
    private Sodium sodium;
    private Carbohydrates carbohydrates;
    private Fiber fiber;
    private Sugars sugars;
    private Protein protein;
    private Calcium calcium;
    private Iron iron;
    private Postassium postassium;
    private Calories calories;


    @Setter
    @Getter
    public static class Fat {
        private Float value;
    }


    @Setter
    @Getter
    public static class SaturatedFat {
        private Float value;
    }


    @Setter
    @Getter
    public static class TransFat {
        private Float value;
    }


    @Setter
    @Getter
    public static class Cholesterol {
        private Float value;
    }


    @Setter
    @Getter
    public static class Sodium {
        private Float value;
    }


    @Setter
    @Getter
    private static class Carbohydrates {
        private Float value;
    }


    @Setter
    @Getter
    private static class Fiber {
        private Float value;
    }


    @Setter
    @Getter
    private static class Sugars {
        private Float value;
    }


    @Setter
    @Getter
    private static class Protein {
        private Float value;
    }


    @Setter
    @Getter
    private static class Calcium {
        private Float value;
    }


    @Setter
    @Getter
    private static class Iron {
        private Float value;
    }


    @Setter
    @Getter
    private static class Postassium {
        private Float value;
    }


    @Setter
    @Getter
    private static class Calories {
        private Float value;
    }

}

