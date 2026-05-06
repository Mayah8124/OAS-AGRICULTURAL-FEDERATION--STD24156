package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.ActivityAttendanceCount;

import java.time.LocalDate;
import java.util.List;

public interface CollectivityActivityAttendanceRepository {
    List<ActivityAttendanceCount> getMemberAttendanceStats(String collectivityId, LocalDate from, LocalDate to);
}

