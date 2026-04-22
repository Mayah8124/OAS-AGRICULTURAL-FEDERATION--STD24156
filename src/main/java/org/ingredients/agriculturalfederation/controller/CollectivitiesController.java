package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.AssignCollectivityIdentity;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CollectivitiesController {

    private final CollectivityService collectivityService;

    public CollectivitiesController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping("/collectivities")
    public ResponseEntity<List<Collectivity>> createListOfCollectivities(@RequestBody List<CreateCollectivity> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectivityService.createCollectivities(request));
    }

    @PostMapping("/collectivity/{collectivityId}/collectivity-assignement")
    public ResponseEntity<Collectivity> assignCollectivityIdentity(
            @PathVariable String collectivityId,
            @RequestBody AssignCollectivityIdentity request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(collectivityService.assignIdentity(collectivityId, request));
    }
}
