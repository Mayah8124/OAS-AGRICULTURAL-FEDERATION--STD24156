package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityActivityRequest;
import org.ingredients.agriculturalfederation.entity.CollectivityActivity;
import org.ingredients.agriculturalfederation.service.CollectivityActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivitiesController {

    private final CollectivityActivityService collectivityActivityService;

    public ActivitiesController(CollectivityActivityService collectivityActivityService) {
        this.collectivityActivityService = collectivityActivityService;
    }

    @PostMapping("/collectivities/{id}/activities")
    public ResponseEntity<List<CollectivityActivity>> addCollectivityActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivityRequest> request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(collectivityActivityService.addActivities(id, request));
    }
}
