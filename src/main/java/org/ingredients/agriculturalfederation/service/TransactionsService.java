package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.response.CollectivityTransactionResponse;
import org.ingredients.agriculturalfederation.dto.response.AccountCreditedResponse;
import org.ingredients.agriculturalfederation.dto.response.MemberDebitedResponse;
import org.ingredients.agriculturalfederation.entity.CollectivityTransaction;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.repository.CollectivityTransactionRepository;
import org.ingredients.agriculturalfederation.validator.TransactionsValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionsService {
    private final CollectivityTransactionRepository collectivityTransactionRepository;
    private final TransactionsValidator transactionsValidator;

    public TransactionsService(CollectivityTransactionRepository collectivityTransactionRepository, TransactionsValidator transactionsValidator) {
        this.collectivityTransactionRepository = collectivityTransactionRepository;
        this.transactionsValidator = transactionsValidator;
    }

    public List<CollectivityTransactionResponse> getCollectivityTransactionsBetween(
            String collectivityId,
            LocalDate from,
            LocalDate to
    ) {
        transactionsValidator.validateParameters(collectivityId, from, to);
        boolean collectivityExists = collectivityTransactionRepository.existsById(collectivityId);
        transactionsValidator.validateCollectivityExists(collectivityId, collectivityExists);
        
        List<CollectivityTransaction> transactions = collectivityTransactionRepository.findByCollectivityIdAndDateRange(collectivityId, from, to);
        
        return transactions.stream().map(transaction -> {
            CollectivityTransactionResponse response = new CollectivityTransactionResponse();
            response.setId(transaction.getId());
            response.setCreationDate(transaction.getCreationDate());
            response.setAmount(transaction.getAmount());
            response.setPaymentMode(transaction.getPaymentMode().toString());
            
            // AccountCredited mapping
            if (transaction.getAccountCredited() != null) {
                AccountCreditedResponse accountResponse = new AccountCreditedResponse();
                // Assuming accountCredited has getId() and getAmount() methods
                // You may need to adapt this based on your actual AccountCredited structure
                accountResponse.setId("account-id"); // placeholder
                accountResponse.setAmount(transaction.getAmount()); // placeholder
                response.setAccountCredited(accountResponse);
            }
            
            // MemberDebited mapping
            if (transaction.getMemberDebited() != null) {
                Member member = transaction.getMemberDebited();
                MemberDebitedResponse memberResponse = new MemberDebitedResponse();
                memberResponse.setId(member.getId());
                memberResponse.setFirstName(member.getFirstName());
                memberResponse.setLastName(member.getLastName());
                memberResponse.setBirthDate(member.getBirthDate());
                memberResponse.setGender(member.getGender().toString());
                memberResponse.setAddress(member.getAddress());
                memberResponse.setProfession(member.getProfession());
                memberResponse.setPhoneNumber(member.getPhoneNumber());
                memberResponse.setEmail(member.getEmail());
                memberResponse.setOccupation(member.getOccupation().toString());
                memberResponse.setReferees(
                        member.getReferees() == null
                                ? null
                                : member.getReferees().stream().map(Member::getId).collect(Collectors.toList())
                );
                response.setMemberDebited(memberResponse);
            }
            
            return response;
        }).collect(Collectors.toList());
    }
}
