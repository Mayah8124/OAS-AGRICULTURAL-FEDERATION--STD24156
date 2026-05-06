package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.entity.*;
import org.ingredients.agriculturalfederation.dto.response.MembershipFeeResponse;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.ingredients.agriculturalfederation.service.CollectivityStatisticsService;
import org.ingredients.agriculturalfederation.service.CollectivityMemberStatisticsService;
import org.ingredients.agriculturalfederation.service.FinancialAccountService;
import org.ingredients.agriculturalfederation.service.MembershipFeeService;
import org.ingredients.agriculturalfederation.validator.GetCollectivityValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CollectivitiesController {

    private final CollectivityService collectivityService;
    private final CollectivityStatisticsService collectivityStatisticsService;
    private final CollectivityMemberStatisticsService collectivityMemberStatisticsService;
    private final MembershipFeeService membershipFeeService;
    private final GetCollectivityValidator getCollectivityValidator;
    private final FinancialAccountService financialAccountService;

    public CollectivitiesController(
            CollectivityService collectivityService,
            CollectivityStatisticsService collectivityStatisticsService,
            CollectivityMemberStatisticsService collectivityMemberStatisticsService,
            MembershipFeeService membershipFeeService,
            GetCollectivityValidator getCollectivityValidator,
            FinancialAccountService financialAccountService
    ) {
        this.collectivityService = collectivityService;
        this.collectivityStatisticsService = collectivityStatisticsService;
        this.collectivityMemberStatisticsService = collectivityMemberStatisticsService;
        this.membershipFeeService = membershipFeeService;
        this.getCollectivityValidator = getCollectivityValidator;
        this.financialAccountService = financialAccountService;
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

    @GetMapping("/collectivities/statistics")
    public ResponseEntity<?> getCollectivitiesStatistics(
            @RequestParam("from") String from,
            @RequestParam("to") String to
    ) {
        if (from == null || from.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'from' is required");
        }
        if (to == null || to.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'to' is required");
        }

        LocalDate parsedFrom;
        LocalDate parsedTo;
        try {
            parsedFrom = LocalDate.parse(from);
            parsedTo = LocalDate.parse(to);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Query parameters 'from' and 'to' must be dates in format YYYY-MM-DD");
        }

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(collectivityStatisticsService.getCollectivitiesStatistics(parsedFrom, parsedTo));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            String msg = e.getMessage() == null || e.getMessage().trim().isEmpty() ? "Invalid request" : e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @GetMapping("/collectivities/{id}/statistics")
    public ResponseEntity<?> getCollectivityStatistics(
            @PathVariable String id,
            @RequestParam("from") String from,
            @RequestParam("to") String to
    ) {
        if (from == null || from.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'from' is required");
        }
        if (to == null || to.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Query parameter 'to' is required");
        }

        LocalDate parsedFrom;
        LocalDate parsedTo;
        try {
            parsedFrom = LocalDate.parse(from);
            parsedTo = LocalDate.parse(to);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Query parameters 'from' and 'to' must be dates in format YYYY-MM-DD");
        }

        try {
            List<CollectivityLocalStatistics> statistics = collectivityMemberStatisticsService.getCollectivityStatistics(id, parsedFrom, parsedTo);
            return ResponseEntity.status(HttpStatus.OK).body(statistics);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            String msg = e.getMessage() == null || e.getMessage().trim().isEmpty() ? "Invalid request" : e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

}
