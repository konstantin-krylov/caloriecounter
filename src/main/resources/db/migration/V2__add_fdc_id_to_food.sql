ALTER TABLE food DROP column IF EXISTS fdc_id;


ALTER TABLE food ADD column fdc_id BIGINT UNIQUE;
ALTER TABLE food ALTER COLUMN food_title TYPE varchar(100)