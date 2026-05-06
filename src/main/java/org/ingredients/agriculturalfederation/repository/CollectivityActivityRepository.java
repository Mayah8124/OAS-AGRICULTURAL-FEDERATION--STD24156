package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.CollectivityActivity;

import java.util.List;

public interface CollectivityActivityRepository {
    boolean collectivityExists(String collectivityId);

    List<CollectivityActivity> addActivities(String collectivityId, List<CollectivityActivity> activities);
}
