package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.MembershipFee;

import java.util.List;

public interface MembershipFeeRepository {
    List<MembershipFee> findByCollectivityId(String collectivityId);
    void save(MembershipFee membershipFee);
}
