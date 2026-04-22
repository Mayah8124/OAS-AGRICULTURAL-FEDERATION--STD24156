package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.ingredients.agriculturalfederation.service.MembershipFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MembershipFeesController {

    private final MembershipFeeService membershipFeeService;

    public MembershipFeesController(MembershipFeeService membershipFeeService) {
        this.membershipFeeService = membershipFeeService;
    }

    @PostMapping("/collectivities/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> createMembershipFees(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFeeRequest> request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(membershipFeeService.createMembershipFees(id, request));
    }
}
