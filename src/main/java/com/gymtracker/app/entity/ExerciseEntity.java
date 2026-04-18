package com.gymtracker.app.entity;

import com.gymtracker.app.domain.ExerciseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId;
    private String name;
    private boolean isCustom;

    @Enumerated(EnumType.STRING)
    private ExerciseCategory category;

    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "owner_id", referencedColumnName = "userId")
    private UserEntity owner;

    private boolean isDeleted;
}
