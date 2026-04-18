INSERT INTO training_plans (name, is_custom) VALUES
('Full Body Beginner', FALSE),
('Upper', FALSE),
('Lower', FALSE),
('Push', FALSE),
('Pull', FALSE),
('Legs', FALSE);

INSERT INTO plan_items (training_plan_id, exercise_id, default_sets) VALUES
-- Full Body Beginner
(1, 3, 3),  -- Squat
(1, 9, 3),  -- Bench Press
(1, 4, 2),  -- Bicep Curl
(1, 10, 2), -- Triceps Dip
(1, 5, 2),  -- Shoulder Press
(1, 12, 3), -- Lat Pulldown
(1, 19, 2), -- Calf Raise
-- Upper Body Plan
(2, 1, 4),  -- Push-Up
(2, 2, 4),  -- Pull-Up
(2, 4, 3),  -- Bicep Curl
(2, 5, 3),  -- Shoulder Press
(2, 9, 4),  -- Bench Press
(2, 10, 3), -- Triceps Dip
(2, 12, 4), -- Lat Pulldown
(2, 13, 3), -- Chest Fly
(2, 17, 4), -- Dumbbell Row
(2, 18, 3), -- Overhead Tricep Extension
-- Lower Body Plan
(3, 3, 4),  -- Squat
(3, 7, 4),  -- Deadlift
(3, 8, 4),  -- Lunges
(3, 11, 4), -- Leg Press
(3, 19, 4), -- Calf Raise
(3, 15, 3), -- Glute Bridge
-- Push Plan
(4, 1, 4),  -- Push-Up
(4, 5, 3),  -- Shoulder Press
(4, 9, 4),  -- Bench Press
(4, 10, 3), -- Triceps Dip
(4, 13, 3), -- Chest Fly
-- Pull Plan
(5, 2, 4),  -- Pull-Up
(5, 4, 3),  -- Bicep Curl
(5, 7, 4),  -- Deadlift
(5, 12, 4), -- Lat Pulldown
(5, 17, 4), -- Dumbbell Row
(5, 18, 3), -- Overhead Tricep Extension
-- Legs Plan
(6, 3, 4),  -- Squat
(6, 8, 4),  -- Lunges
(6, 11, 4), -- Leg Press
(6, 19, 4), -- Calf Raise
(6, 15, 3); -- Glute Bridge
