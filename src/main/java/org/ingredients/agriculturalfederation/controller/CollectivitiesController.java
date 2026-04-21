package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CollectivitiesController {

    private final CollectivityService collectivityService;

    public CollectivitiesController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping("/collectivities")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Collectivity> createListOfCollectivities(@RequestBody List<CreateCollectivity> request) {
        return collectivityService.createCollectivities(request);
    }
}
