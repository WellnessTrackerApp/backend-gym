package com.gymtracker.app.domain.workout;

import com.gymtracker.app.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WorkoutRepetitionItem extends WorkoutItem {
    private List<ExerciseSet> sets;

    public record ExerciseSet(int reps, BigDecimal weight) {
        public ExerciseSet {
            if (reps < 0) {
                throw new DomainException("negative-reps");
            }
            if (weight == null) {
                throw new DomainException("null-weight");
            }
            if (weight.compareTo(BigDecimal.ZERO) < 0) {
                throw new DomainException("negative-weight");
            }
        }
    }
}
