package com.epam.caloriecounter.dto;

import lombok.Data;

@Data
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

    @Data
    public static class Fat {
        private Float value;
    }

    @Data
    public static class SaturatedFat {
        private Float value;
    }

    @Data
    public static class TransFat {
        private Float value;
    }

    @Data
    public static class Cholesterol {
        private Float value;
    }

    @Data
    public static class Sodium {
        private Float value;
    }

    @Data
    private static class Carbohydrates {
        private Float value;
    }

    @Data
    private static class Fiber {
        private Float value;
    }

    @Data
    private static class Sugars {
        private Float value;
    }

    @Data
    private static class Protein {
        private Float value;
    }

    @Data
    private static class Calcium {
        private Float value;
    }

    @Data
    private static class Iron {
        private Float value;
    }

    @Data
    private static class Postassium {
        private Float value;
    }

    @Data
    private static class Calories {
        private Float value;
    }

}

