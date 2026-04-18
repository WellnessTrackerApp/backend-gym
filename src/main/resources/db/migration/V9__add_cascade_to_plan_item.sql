ALTER TABLE plan_items
DROP CONSTRAINT fk_plan_items_exercises;

ALTER TABLE plan_items
ADD CONSTRAINT fk_plan_items_exercises
FOREIGN KEY (exercise_id)
REFERENCES exercises (exercise_id)
ON DELETE CASCADE;