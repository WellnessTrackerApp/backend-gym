CREATE TABLE workouts (
    id BIGSERIAL PRIMARY KEY,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    training_id BIGINT NOT NULL,
    user_id UUID NOT NULL,

    constraint fk_workouts_users
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    constraint fk_workouts_training_plans
        FOREIGN KEY (training_id)
        REFERENCES training_plans(id)
        ON DELETE SET NULL
);

CREATE TABLE workout_items (
    id BIGSERIAL PRIMARY KEY,
    item_type VARCHAR(32) NOT NULL,
    workout_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    constraint fk_workout_items_workouts FOREIGN KEY (workout_id) REFERENCES workouts(id) ON DELETE CASCADE,
    constraint fk_workout_items_exercises FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id)
);

CREATE TABLE repetition_exercise_sets (
    workout_item_id BIGINT NOT NULL,
    reps INTEGER NOT NULL,
    weight NUMERIC(6, 2) NOT NULL,
    set_order INTEGER NOT NULL,
    constraint fk_repetition_exercise_sets FOREIGN KEY (workout_item_id) REFERENCES workout_items(id) ON DELETE CASCADE
);
