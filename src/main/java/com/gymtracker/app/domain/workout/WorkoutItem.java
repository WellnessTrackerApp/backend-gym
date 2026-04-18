package com.gymtracker.app.domain.workout;

import com.gymtracker.app.domain.Exercise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class WorkoutItem {
    protected Exercise exercise;
}
