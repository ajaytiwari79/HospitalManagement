package com.kairos.persistence.repository.clause;

import com.kairos.persistence.model.clause.Clause;
import com.kairos.persistence.model.clause.ClauseMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Repository
//@JaversSpringDataAuditable
public interface ClauseRepository extends JpaRepository<ClauseMD, Long> {

    @Query(value = "Select c from ClauseMD c where c.countryId = ?1 and lower(c.title) = lower(?2) and lower(c.description) = lower(?3) and c.deleted = false")
    ClauseMD findByCountryIdAndTitleAndDescription(Long referenceId, String title, String description);

    @Query(value = "Select c from ClauseMD c where c.organizationId = ?1 and lower(c.title) = lower(?2) and lower(c.description) = lower(?3) and c.deleted = false")
    ClauseMD findByUnitIdAndTitleAndDescription(Long referenceId, String title, String description);

    @Query(value = "update ClauseMD set deleted = true where id = ?1 and deleted = false")
    Integer safeDeleteById(Long id);

    @Query(value = "Select c from ClauseMD c where c.countryId = ?1 and c.deleted = false")
    List<ClauseMD> findAllClauseByCountryId(Long countryId);

    @Query(value = "Select c from ClauseMD c where c.organizationId = ?1 and c.deleted = false")
    List<ClauseMD> findAllClauseByUnitId(Long unitId);

    @Query(value = "Select c from ClauseMD c where c.id = ?1 and c.countryId = ?2 and c.deleted = false")
    ClauseMD findByIdAndCountryId(Long id, Long countryId);

    @Query(value = "Select c from ClauseMD c where c.tempClauseId != null")
    List<ClauseMD> getAllClausesHavingTempId();

    @Query(value = "Select c from ClauseMD c JOIN c.organizationTypes OT JOIN c.organizationSubTypes OST JOIN c.organizationServices SC JOIN c.organizationSubServices SSC where c.countryId = ?1 and c.deleted = false and OT.id IN (?1) and OST.id IN (?2) and SC.id IN (?3) and SSC.id IN (?4)")
    List<ClauseMD> findAllClauseByAgreementTemplateMetadataAndCountryId(Long countryId,List<Long> organizationTypeIds,List<Long> organizationSubTypeIds,List<Long> serviceCategoryIds,List<Long> subServiceCategoryIds, Long templateTypeId);

}
