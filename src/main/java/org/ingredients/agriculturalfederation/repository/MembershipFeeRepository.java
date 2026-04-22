package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.MembershipFee;

import java.util.List;

public interface MembershipFeeRepository {
    List<MembershipFee> saveAll(String collectivityId, List<MembershipFee> fees);
}
