package com.kairos.persistence.repository.clause;

import com.kairos.persistence.model.clause.Clause;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface ClauseRepository extends JpaRepository<Clause, Long> {

    @Query(value = "Select c from Clause c where c.countryId = ?1 and lower(c.title) = lower(?2) and lower(c.description) = lower(?3) and c.deleted = false")
    Clause findByCountryIdAndTitleAndDescription(Long referenceId, String title, String description);

    @Query(value = "Select c from Clause c where c.organizationId = ?1 and lower(c.title) = lower(?2) and lower(c.description) = lower(?3) and c.deleted = false")
    Clause findByUnitIdAndTitleAndDescription(Long referenceId, String title, String description);

    @Modifying
    @Transactional
    @Query(value = "update Clause set deleted = true where id = ?1 and deleted = false")
    Integer safeDeleteById(Long id);

    @Query(value = "Select c from Clause c where c.countryId = ?1 and c.deleted = false")
    List<Clause> findAllClauseByCountryId(Long countryId);

    @Query(value = "Select c from Clause c where c.organizationId = ?1 and c.deleted = false")
    List<Clause> findAllClauseByUnitId(Long unitId);

    @Query(value = "Select c from Clause c where c.id = ?1 and c.countryId = ?2 and c.deleted = false")
    Clause findByIdAndCountryId(Long id, Long countryId);

    @Query(value = "Select c from Clause c where c.tempClauseId != null")
    List<Clause> getAllClausesHavingTempId();

    @Query(value = "Select DISTINCT c from MasterClause c JOIN c.organizationTypes OT JOIN c.organizationSubTypes OST JOIN c.organizationServices SC JOIN c.organizationSubServices SSC where c.countryId = ?1 and c.deleted = false and OT.id IN (?2) and OST.id IN (?3) and SC.id IN (?4) and SSC.id IN (?5)")
    List<Clause> findAllClauseByAgreementTemplateMetadataAndCountryId(Long countryId, List<Long> organizationTypeIds, List<Long> organizationSubTypeIds, List<Long> serviceCategoryIds, List<Long> subServiceCategoryIds, Long templateTypeId);

    @Query(value = "Select DISTINCT c from MasterClause c JOIN c.organizationTypes OT JOIN c.organizationSubTypes OST JOIN c.organizationServices SC JOIN c.organizationSubServices SSC where c.countryId = ?1 and c.deleted = false and OT.id IN (?2) and OST.id IN (?3) and SC.id IN (?4) and SSC.id IN (?5)")
    List<Clause> getClauseByCountryIdAndOrgTypeSubTypeCategoryAndSubCategory(Long countryId, List<Long> organizationTypeIds, List<Long> organizationSubTypeIds, List<Long> organizationServiceCategoryIds, List<Long> organizationSubServiceCategoryTypeIds);

}
