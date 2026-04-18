package com.gymtracker.app.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanItemEntity {
    @ManyToOne
    @JoinColumn(name = "exercise_id", referencedColumnName = "exerciseId")
    private ExerciseEntity exercise;
    private int defaultSets;
}
