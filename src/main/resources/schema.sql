DROP SEQUENCE IF EXISTS food_food_id_seq CASCADE;
CREATE SEQUENCE food_food_id_seq;

DROP SEQUENCE IF EXISTS food_nutrient_nutrient_id_seq CASCADE;
CREATE SEQUENCE food_nutrient_nutrient_id_seq;

DROP SEQUENCE IF EXISTS nutrient_type_nutrient_type_id_seq CASCADE;
CREATE SEQUENCE nutrient_type_nutrient_type_id_seq;

DROP SEQUENCE IF EXISTS food_type_food_type_id_seq CASCADE;
CREATE SEQUENCE food_type_food_type_id_seq;

CREATE TABLE food (
    food_id  BIGINT DEFAULT NEXTVAL('food_food_id_seq') PRIMARY KEY,
    food_title varchar(50)   NOT NULL,
    food_description varchar(500)   NULL,
    food_ingredients varchar(2000)   NULL,
    food_type_id BIGINT   NOT NULL
);

CREATE TABLE food_nutrient (
    nutrient_id  BIGINT DEFAULT NEXTVAL('food_nutrient_nutrient_id_seq') PRIMARY KEY,
    amount float  DEFAULT 0 NOT NULL,
    food_id BIGINT   NOT NULL,
    nutrient_type_id BIGINT   NOT NULL
);

CREATE TABLE nutrient_type (
    nutrient_type_id  BIGINT DEFAULT NEXTVAL('nutrient_type_nutrient_type_id_seq') PRIMARY KEY,
    nutrient_name varchar(50) UNIQUE NOT NULL,
    unit_name varchar(10)   NOT NULL
);

CREATE TABLE food_type (
    food_type_id  BIGINT DEFAULT NEXTVAL('food_type_food_type_id_seq') PRIMARY KEY,
    food_type varchar(50) UNIQUE NOT NULL
);

ALTER TABLE food ADD CONSTRAINT fk_food_food_type_id FOREIGN KEY(food_type_id)
REFERENCES food_type (food_type_id);

ALTER TABLE food_nutrient ADD CONSTRAINT fk_food_nutrient_food_id FOREIGN KEY(food_id)
REFERENCES food (food_id);

ALTER TABLE food_nutrient ADD CONSTRAINT fk_food_nutrient_nutrient_type_id FOREIGN KEY(nutrient_type_id)
REFERENCES nutrient_type (nutrient_type_id);

