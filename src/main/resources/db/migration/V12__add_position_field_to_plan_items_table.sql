ALTER TABLE plan_items 
ADD COLUMN position INTEGER;

ALTER TABLE plan_items
ADD COLUMN id BIGSERIAL UNIQUE;

UPDATE plan_items
SET position = subquery.new_pos
FROM (SELECT id, row_number() over (partition by training_plan_id order by id) - 1 as new_pos FROM plan_items) AS subquery
WHERE plan_items.id = subquery.id;

ALTER TABLE plan_items
ALTER COLUMN position SET NOT NULL