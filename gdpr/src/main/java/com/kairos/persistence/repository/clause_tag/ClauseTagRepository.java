package com.kairos.persistence.repository.clause_tag;

import com.kairos.persistence.model.clause_tag.ClauseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
//@JaversSpringDataAuditable
public interface ClauseTagRepository extends JpaRepository<ClauseTag,Long> {

    @Query(value = "Select CT from ClauseTag CT where CT.organizationId = ?1 and CT.deleted = false and lower(CT.name) IN (?2)")
    List<ClauseTag> findByUnitIdAndTitles(Long referenceId, Set<String> clauseTagsName);

    @Query(value = "Select CT from ClauseTag CT where CT.countryId = ?1 and CT.deleted = false and lower(CT.name) IN (?2)")
    List<ClauseTag> findByCountryIdAndTitles(Long referenceId, Set<String> clauseTagsName);

    @Query(value = "Select CT from ClauseTag CT where CT.deleted = false and CT.id IN (?1)")
    List<ClauseTag> findAllClauseTagByIds(List<Long> ids);

    @Query(value = "Select CT from ClauseTag CT where CT.deleted=false and CT.defaultTag =  true")
    ClauseTag findDefaultTag();

    @Query(value = "Select CT from ClauseTag CT where CT.countryId = ?1 and CT.deleted = false")
    List<ClauseTag> findAllByCountryId(Long countryId);


    @Query(value = "Select CT from ClauseTag CT where CT.organizationId = ?1 and CT.deleted = false")
    List<ClauseTag> findAllClauseTagByUnitId(Long countryId);

}
