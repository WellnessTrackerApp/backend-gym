package com.gymtracker.app.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "training_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ElementCollection
    @CollectionTable(name = "plan_items", joinColumns = @JoinColumn(name = "training_plan_id"))
    @SQLRestriction("exercise_id IN (SELECT e.exercise_id FROM exercises e WHERE e.is_deleted = false)")
    @OrderColumn(name = "position")
    private List<PlanItemEntity> planItems;

    private boolean isCustom;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "userId")
    private UserEntity owner;

    private boolean isDeleted;
}
