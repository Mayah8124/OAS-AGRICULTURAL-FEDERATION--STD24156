package org.ingredients.agriculturalfederation.controller;

import org.ingredients.agriculturalfederation.dto.response.CollectivityTransactionResponse;
import org.ingredients.agriculturalfederation.service.TransactionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TransactionsController {
    private final TransactionsService transactionsService;

    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @GetMapping("/collectivities/{id}/transactions")
    public ResponseEntity<List<CollectivityTransactionResponse>> getTransactionsBetween(
            @PathVariable String id,
            @RequestParam String from,
            @RequestParam String to
    ) {
        List<CollectivityTransactionResponse> transactions = transactionsService.getCollectivityTransactionsBetween(id, LocalDate.parse(from), LocalDate.parse(to));
        return ResponseEntity.ok(transactions);
    }
}
