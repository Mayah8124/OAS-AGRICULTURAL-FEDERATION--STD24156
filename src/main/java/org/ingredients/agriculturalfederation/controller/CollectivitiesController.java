package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.ingredients.agriculturalfederation.dto.response.MembershipFeeResponse;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.ingredients.agriculturalfederation.service.MembershipFeeService;
import org.ingredients.agriculturalfederation.validator.GetCollectivityValidator;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CollectivitiesController {

    private final CollectivityService collectivityService;
    private final MembershipFeeService membershipFeeService;
    private final GetCollectivityValidator getCollectivityValidator;

    public CollectivitiesController(CollectivityService collectivityService, MembershipFeeService membershipFeeService, GetCollectivityValidator getCollectivityValidator) {
        this.collectivityService = collectivityService;
        this.membershipFeeService = membershipFeeService;
        this.getCollectivityValidator = getCollectivityValidator;
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

    @GetMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<List<MembershipFeeResponse>> getMembershipFees(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(membershipFeeService.getMembershipFees(id));
    }

    @PostMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeRequest> request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(membershipFeeService.createMembershipFees(id, request));
    }

    @GetMapping("/collectivities/{id}")
    public ResponseEntity<Collectivity> getCollectivityById(@PathVariable String id) {
        getCollectivityValidator.validateGetCollectivity(id);
        return ResponseEntity.status(HttpStatus.OK).body(collectivityService.getCollectivityById(id));
    }

    @ExceptionHandler(CollectivityNotFoundException.class)
    public ResponseEntity<String> handleCollectivityNotFoundException(CollectivityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidCollectivityException.class)
    public ResponseEntity<String> handleInvalidCollectivityException(InvalidCollectivityException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
