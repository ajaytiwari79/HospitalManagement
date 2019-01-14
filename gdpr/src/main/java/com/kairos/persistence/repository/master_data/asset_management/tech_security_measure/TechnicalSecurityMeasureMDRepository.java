package com.kairos.persistence.repository.master_data.asset_management.tech_security_measure;


import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasureMD;
import com.kairos.persistence.model.master_data.default_asset_setting.TechnicalSecurityMeasureMD;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface TechnicalSecurityMeasureMDRepository extends JpaRepository<TechnicalSecurityMeasureMD, Long> {

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.countryId = ?1 and tsm.deleted = ?2 and lower(tsm.name) IN ?3")
    List<TechnicalSecurityMeasureMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set name = ?1 where id= ?2")
    Integer updateTechnicalSecurityMeasureName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.id = ?1 and tsm.countryId = ?2 and tsm.deleted = ?3")
    TechnicalSecurityMeasureMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO(tsm.id, tsm.name, tsm.organizationId, tsm.suggestedDataStatus, tsm.suggestedDate) FROM TechnicalSecurityMeasureMD tsm WHERE tsm.countryId = ?1 and tsm.deleted = false order by createdAt desc")
    List<TechnicalSecurityMeasureResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.countryId = ?1 and tsm.deleted = ?2 and lower(tsm.name) = lower(?3)")
    TechnicalSecurityMeasureMD findByCountryIdAndDeletedAndName(Long countryId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterTechnicalStorageMeasureName(String name, Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set suggestedDataStatus = ?3 where id IN (?2) and countryId = ?1")
    Integer updateMasterTechnicalStorageMeasureStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.id IN (?1) and tsm.deleted = false")
    List<TechnicalSecurityMeasureMD> findAllByIds( Set<Long> ids);


    @Query(value = "SELECT sf FROM TechnicalSecurityMeasureMD sf WHERE sf.organizationId = ?1 and sf.deleted = ?2 and lower(sf.name) IN ?3")
    List<TechnicalSecurityMeasureMD> findByOrganizationIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> names);


    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndOrganizationId(Long id, Long orgId);

    @Query(value = "SELECT sf FROM TechnicalSecurityMeasureMD sf WHERE sf.id = ?1 and sf.organizationId = ?2 and sf.deleted = ?3")
    TechnicalSecurityMeasureMD findByIdAndOrganizationIdAndDeleted(Long id, Long orgId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM TechnicalSecurityMeasureMD sf WHERE sf.organizationId = ?1 and sf.deleted = false order by createdAt desc")
    List<TechnicalSecurityMeasureResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.organizationId = ?1 and tsm.deleted = ?2 and lower(tsm.name) = lower(?3)")
    TechnicalSecurityMeasureMD findByOrganizationIdAndDeletedAndName(Long orgId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateTechnicalSecurityMeasureName(String name, Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set suggestedDataStatus = ?3 where id IN (?2) and organizationId = ?1")
    Integer updateTechnicalSecurityMeasureStatus(Long orgId, Set<Long> ids, SuggestedDataStatus status);

}
