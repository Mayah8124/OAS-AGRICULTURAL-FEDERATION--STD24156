package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.ActivityAttendanceCount;
import org.ingredients.agriculturalfederation.entity.MemberFullStats;
import org.ingredients.agriculturalfederation.entity.OverallAttendanceStats;

import java.time.LocalDate;
import java.util.List;

public interface CollectivityActivityAttendanceRepository {
    List<ActivityAttendanceCount> getMemberAttendanceStats(String collectivityId, LocalDate from, LocalDate to);
    List<OverallAttendanceStats> getOverallAttendanceStats(LocalDate from, LocalDate to);
    List<MemberFullStats> getMemberFullStats(String collectivityId, LocalDate from, LocalDate to);
}

