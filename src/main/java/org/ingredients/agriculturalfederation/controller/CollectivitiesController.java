package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CollectivityActivity;
import org.ingredients.agriculturalfederation.entity.FinancialAccount;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.ingredients.agriculturalfederation.dto.response.ActivityMemberAttendance;
import org.ingredients.agriculturalfederation.dto.response.MembershipFeeResponse;
import org.ingredients.agriculturalfederation.service.ActivityAttendanceService;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.ingredients.agriculturalfederation.service.CollectivityActivityService;
import org.ingredients.agriculturalfederation.service.FinancialAccountService;
import org.ingredients.agriculturalfederation.service.MembershipFeeService;
import org.ingredients.agriculturalfederation.validator.GetCollectivityValidator;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CollectivitiesController {

    private final CollectivityService collectivityService;
    private final MembershipFeeService membershipFeeService;
    private final GetCollectivityValidator getCollectivityValidator;
    private final FinancialAccountService financialAccountService;
    private final CollectivityActivityService collectivityActivityService;
    private final ActivityAttendanceService activityAttendanceService;

    public CollectivitiesController(CollectivityService collectivityService, MembershipFeeService membershipFeeService, GetCollectivityValidator getCollectivityValidator, FinancialAccountService financialAccountService, CollectivityActivityService collectivityActivityService, ActivityAttendanceService activityAttendanceService) {
        this.collectivityService = collectivityService;
        this.membershipFeeService = membershipFeeService;
        this.getCollectivityValidator = getCollectivityValidator;
        this.financialAccountService = financialAccountService;
        this.collectivityActivityService = collectivityActivityService;
        this.activityAttendanceService = activityAttendanceService;
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

    @GetMapping("/collectivities/{id}/financialAccounts")
    public ResponseEntity<?> getCollectivityFinancialAccounts(
            @PathVariable String id,
            @RequestParam("at") String at
    ) {
        if (at == null || at.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'at' is required");
        }

        LocalDate parsedAt;
        try {
            parsedAt = LocalDate.parse(at);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'at' must be a date in format YYYY-MM-DD");
        }

        try {
            List<FinancialAccount> out = financialAccountService.getCollectivityFinancialAccountsAt(id, parsedAt);
            return ResponseEntity.status(HttpStatus.OK).body(out);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage() == null ? "Invalid request" : e.getMessage();
            if (msg.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
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
