package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    void save(Member member);
    Optional<Member> findById(String id);
    List<Member> findAllById(List<String> ids);
    void saveReferees(String memberId, List<String> refereeIds);
    List<String> findRefereeIdsByMemberId(String memberId);
    void updateCollectivityId(String memberId, String collectivityId);
}
