package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.request.CreateMemberRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateMemberPaymentRequest;
import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.service.MemberPaymentService;
import org.ingredients.agriculturalfederation.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MembersController {

    private final MemberService memberService;
    private final MemberPaymentService memberPaymentService;

    public MembersController(MemberService memberService, MemberPaymentService memberPaymentService) {
        this.memberService = memberService;
        this.memberPaymentService = memberPaymentService;
    }

    @PostMapping("/members")
    public ResponseEntity<List<Member>> createListOfMembers(@RequestBody List<CreateMemberRequest> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMembers(request));
    }

    @PostMapping("/members/{id}/payments")
    public ResponseEntity<List<MemberPaymentResponse>> createMemberPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPaymentRequest> request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberPaymentService.createPayments(id, request));
    }
}
