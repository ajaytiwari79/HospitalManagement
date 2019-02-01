package com.kairos.persistence.repository.clause_tag;

import com.kairos.persistence.model.clause_tag.ClauseTagMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
//@JaversSpringDataAuditable
public interface ClauseTagRepository extends JpaRepository<ClauseTagMD,Long> {

    @Query(value = "Select CT from ClauseTagMD CT where CT.organizationId = ?1 and CT.deleted = false and lower(CT.name) IN (?2)")
    List<ClauseTagMD> findByUnitIdAndTitles(Long referenceId, Set<String> clauseTagsName);

    @Query(value = "Select CT from ClauseTagMD CT where CT.countryId = ?1 and CT.deleted = false and lower(CT.name) IN (?2)")
    List<ClauseTagMD> findByCountryIdAndTitles(Long referenceId, Set<String> clauseTagsName);

    @Query(value = "Select CT from ClauseTagMD CT where CT.deleted = false and CT.id IN (?1)")
    List<ClauseTagMD> findAllClauseTagByIds(List<Long> ids);

    @Query(value = "Select CT from ClauseTagMD CT where CT.deleted=false and CT.defaultTag =  true")
    ClauseTagMD findDefaultTag();

    @Query(value = "Select CT from ClauseTagMD CT where CT.countryId = ?1 and CT.deleted = false")
    List<ClauseTagMD> findAllByCountryId(Long countryId);


    @Query(value = "Select CT from ClauseTagMD CT where CT.organizationId = ?1 and CT.deleted = false")
    List<ClauseTagMD> findAllClauseTagByUnitId(Long countryId);

}
