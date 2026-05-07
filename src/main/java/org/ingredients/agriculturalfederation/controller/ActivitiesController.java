package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CreateActivityMemberAttendance;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityActivityRequest;
import org.ingredients.agriculturalfederation.dto.response.ActivityMemberAttendance;
import org.ingredients.agriculturalfederation.entity.CollectivityActivity;
import org.ingredients.agriculturalfederation.service.ActivityAttendanceService;
import org.ingredients.agriculturalfederation.service.CollectivityActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivitiesController {

    private final CollectivityActivityService collectivityActivityService;

    private final ActivityAttendanceService activityAttendanceService;

    public ActivitiesController(
            CollectivityActivityService collectivityActivityService,
            ActivityAttendanceService activityAttendanceService
    ) {
        this.collectivityActivityService = collectivityActivityService;
        this.activityAttendanceService = activityAttendanceService;
    }

    @PostMapping("/collectivities/{id}/activities")
    public ResponseEntity<List<CollectivityActivity>> addCollectivityActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivityRequest> request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectivityActivityService.addActivities(id, request));
    }

    @GetMapping("/collectivities/{id}/activities")
    public ResponseEntity<List<CollectivityActivity>> getCollectivityActivities(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(collectivityActivityService.getActivities(id));
    }

    @GetMapping("/collectivities/{id}/activities/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendance>> getActivityAttendance(
            @PathVariable String id,
            @PathVariable String activityId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(activityAttendanceService.getActivityAttendance(id, activityId));
    }

    @PostMapping("/collectivities/{id}/activities/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendance>> createActivityAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendance> requests
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityAttendanceService.createAttendance(id, activityId, requests));
    }
}
