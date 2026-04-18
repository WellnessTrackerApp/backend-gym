CREATE TABLE training_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_custom BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id UUID,
    constraint fk_training_plans_users FOREIGN KEY (owner_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE plan_items (
    training_plan_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    default_sets INT,
    constraint fk_plan_items_training_plan FOREIGN KEY (training_plan_id) REFERENCES training_plans (id) ON DELETE CASCADE,
    constraint fk_plan_items_exercises FOREIGN KEY (exercise_id) REFERENCES exercises (exercise_id)
);
