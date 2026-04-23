package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.FinancialAccount;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.ingredients.agriculturalfederation.dto.response.MembershipFeeResponse;
import org.ingredients.agriculturalfederation.service.CollectivityService;
import org.ingredients.agriculturalfederation.service.FinancialAccountService;
import org.ingredients.agriculturalfederation.service.MembershipFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CollectivitiesController {

    private final CollectivityService collectivityService;
    private final MembershipFeeService membershipFeeService;
    private final FinancialAccountService financialAccountService;

    public CollectivitiesController(CollectivityService collectivityService, MembershipFeeService membershipFeeService, FinancialAccountService financialAccountService) {
        this.collectivityService = collectivityService;
        this.membershipFeeService = membershipFeeService;
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

    @GetMapping("/collectivities/{id}/financialAccounts")
    public ResponseEntity<List<FinancialAccount>> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam String at
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(financialAccountService.getCollectivityFinancialAccountsAt(id, LocalDate.parse(at)));
    }

    @PostMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeRequest> request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(membershipFeeService.createMembershipFees(id, request));
    }
}
