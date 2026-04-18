package com.gymtracker.app.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WorkoutSessionSnapshot(Long workoutId, LocalDate workoutDate, List<SetDetail> sets) {

    @Builder
    public record SetDetail(int reps, BigDecimal weight) {}
}
