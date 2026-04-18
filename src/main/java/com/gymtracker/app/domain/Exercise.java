package com.gymtracker.app.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Exercise {
    private Long exerciseId;
    private String name;
    private boolean isCustom;
    private UUID ownerId;
    private ExerciseCategory category;
}
