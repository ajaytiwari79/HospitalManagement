package com.kairos.persistence.repository.master_data.asset_management.hosting_type;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingTypeMD;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface HostingTypeMDRepository extends JpaRepository<HostingTypeMD, Long> {

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.countryId = ?1 and ht.deleted = ?2 and lower(ht.name) IN ?3")
    List<HostingTypeMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.countryId = ?1 and ht.deleted = ?2 and lower(ht.name) = lower(?3)")
    HostingTypeMD findByCountryIdAndDeletedAndName(Long countryId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterHostingTypeName(String name, Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set suggestedDataStatus = ?3 where id= ?2 and countryId = ?1")
    Integer updateMasterHostingTypeStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.id IN (?1) and ht.deleted = false")
    List<HostingTypeMD> findAllByIds( Set<Long> ids);


    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.id = ?1 and ht.countryId = ?2 and ht.deleted = ?3")
    HostingTypeMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingTypeResponseDTO(ht.id, ht.name, ht.organizationId, ht.suggestedDataStatus, ht.suggestedDate )  FROM HostingTypeMD ht WHERE ht.countryId = ?1 and ht.deleted = false order by createdAt desc")
    List<HostingTypeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);


    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.organizationId = ?1 and ht.deleted = ?2 and lower(ht.name) IN ?3")
    List<HostingTypeMD> findByOrganizationIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> name);

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.organizationId = ?1 and ht.deleted = ?2 and lower(ht.name) = lower(?3)")
    HostingTypeMD findByOrganizationIdAndDeletedAndName(Long orgId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateHostingTypeName(String name, Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set suggestedDataStatus = ?3 where id IN (?2) and organizationId = ?1")
    Integer updateHostingTypeStatus(Long orgId, Set<Long> ids, SuggestedDataStatus status);

    @Transactional
    @Modifying
    @Query(value = "update HostingTypeMD set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndOrganizationId(Long id, Long orgId);

    @Query(value = "SELECT ht FROM HostingTypeMD ht WHERE ht.id = ?1 and ht.organizationId = ?2 and ht.deleted = ?3")
    HostingTypeMD findByIdAndOrganizationIdAndDeleted(Long id, Long orgId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingTypeResponseDTO(ht.id, ht.name, ht.organizationId, ht.suggestedDataStatus, ht.suggestedDate )  FROM HostingTypeMD ht WHERE ht.organizationId = ?1 and ht.deleted = false order by createdAt desc")
    List<HostingTypeResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);







}
