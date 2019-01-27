package com.kairos.persistence.repository.master_data.asset_management.org_security_measure;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasureMD;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface OrganizationalSecurityMeasureMDRepository extends JpaRepository<OrganizationalSecurityMeasureMD, Long> {

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.countryId = ?1 and osm.deleted = ?2 and lower(osm.name) IN ?3")
    List<OrganizationalSecurityMeasureMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> names);

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.countryId = ?1 and osm.deleted = ?2 and lower(osm.name) = lower(?3)")
    OrganizationalSecurityMeasureMD findByCountryIdAndDeletedAndName(Long countryId, boolean deleted,String name);

    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterOrganizationalSecurityMeasureName(String name, Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set suggestedDataStatus = ?3 where id IN (?2) and countryId = ?1")
    Integer updateMasterOrganizationalSecurityMeasureStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.id IN (?1) and osm.deleted = false")
    List<OrganizationalSecurityMeasureMD> findAllByIds( Set<Long> ids);


    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.id = ?1 and osm.countryId = ?2 and osm.deleted = ?3")
    OrganizationalSecurityMeasureMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO(osm.id, osm.name, osm.organizationId, osm.suggestedDataStatus, osm.suggestedDate) FROM OrganizationalSecurityMeasureMD osm WHERE osm.countryId = ?1 and osm.deleted = false order by createdAt desc")
    List<OrganizationalSecurityMeasureResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);




    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.organizationId = ?1 and osm.deleted = ?2 and lower(osm.name) IN ?3")
    List<OrganizationalSecurityMeasureMD> findByOrganizationIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> names);

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.organizationId = ?1 and osm.deleted = ?2 and lower(osm.name) = lower(?3)")
    OrganizationalSecurityMeasureMD findByOrganizationIdAndDeletedAndName(Long orgId, boolean deleted, String name);

    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateOrganizationalSecurityMeasureName(String name, Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set suggestedDataStatus = ?3 where id= ?2 and organizationId = ?1")
    Integer updateOrganizationalSecurityMeasureStatus(Long orgId, Set<Long> ids, SuggestedDataStatus status);

    @Transactional
    @Modifying
    @Query(value = "update OrganizationalSecurityMeasureMD set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndOrganizationId(Long id, Long orgId);

    @Query(value = "SELECT osm FROM OrganizationalSecurityMeasureMD osm WHERE osm.id = ?1 and osm.organizationId = ?2 and osm.deleted = ?3")
    OrganizationalSecurityMeasureMD findByIdAndOrganizationIdAndDeleted(Long id, Long orgId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO(osm.id, osm.name, osm.organizationId, osm.suggestedDataStatus, osm.suggestedDate) FROM OrganizationalSecurityMeasureMD osm WHERE osm.organizationId = ?1 and osm.deleted = false order by createdAt desc")
    List<OrganizationalSecurityMeasureResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
