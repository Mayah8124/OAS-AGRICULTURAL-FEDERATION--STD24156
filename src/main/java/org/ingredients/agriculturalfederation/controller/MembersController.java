package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.entity.CreateMember;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MembersController {

    private final MemberService memberService;

    public MembersController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity<List<Member>> createListOfMembers(@RequestBody List<CreateMember> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMembers(request));
    }
}
