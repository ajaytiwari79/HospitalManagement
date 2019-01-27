package com.kairos.persistence.repository.clause_tag;

import com.kairos.persistence.model.clause_tag.ClauseTagMD;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
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



    /*@Query("{countryId:?0,_id:?1,deleted:false}")
    ClauseTag findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<ClauseTag> findAllByCountryId(Long countryId);

    @Query("{deleted:false,organizationId:?0}")
    List<ClauseTag> findAllClauseTagByUnitId(Long unitId);

    @Query("{_id:{$in:?0},deleted:false}")
    List<ClauseTag> findAllClauseTagByIds(List<BigInteger> ids);


    @Query("{deleted:false,defaultTag:true}")
    ClauseTag findDefaultTag();

    ClauseTag findByNameAndCountryId(Long countryId, String name);
    ClauseTag findByid(BigInteger id);*/

}
