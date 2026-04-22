package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<List<Collectivity>> createListOfCollectivities(@RequestBody List<CreateCollectivityRequest> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectivityService.createCollectivities(request));
    }

    @PutMapping("/collectivities/{id}/informations")
    public ResponseEntity<Collectivity> updateCollectivityInformations(
            @PathVariable String id,
            @RequestBody CollectivityInformationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(collectivityService.updateInformations(id, request));
    }
}
