package com.kairos.persistence.repository.master_data.asset_management.storage_format;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormatMD;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface StorageFormatMDRepository extends JpaRepository<StorageFormatMD, Long> {

    @Query(value = "SELECT sf FROM StorageFormatMD sf WHERE sf.countryId = ?1 and sf.deleted = ?2 and lower(sf.name) IN ?3")
    List<StorageFormatMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);


    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT sf FROM StorageFormatMD sf WHERE sf.id = ?1 and sf.countryId = ?2 and sf.deleted = ?3")
    StorageFormatMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormatMD sf WHERE sf.countryId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT ht FROM StorageFormatMD ht WHERE ht.countryId = ?1 and ht.deleted = ?2 and lower(ht.name) = lower(?3)")
    StorageFormatMD findByCountryIdAndDeletedAndName(Long countryId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterStorageFormatName(String name, Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set suggestedDataStatus = ?3 where id= ?2 and countryId = ?1")
    Integer updateMasterStorageFormatStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT ht FROM StorageFormatMD ht WHERE ht.id IN (?1) and ht.deleted = false")
    List<StorageFormatMD> findAllByIds( Set<Long> ids);


    @Query(value = "SELECT sf FROM StorageFormatMD sf WHERE sf.organizationId = ?1 and sf.deleted = ?2 and lower(sf.name) IN ?3")
    List<StorageFormatMD> findByOrganizationIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> names);


    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndOrganizationId(Long id, Long orgId);

    @Query(value = "SELECT sf FROM StorageFormatMD sf WHERE sf.id = ?1 and sf.organizationId = ?2 and sf.deleted = ?3")
    StorageFormatMD findByIdAndOrganizationIdAndDeleted(Long id, Long orgId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormatMD sf WHERE sf.organizationId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

    @Query(value = "SELECT ht FROM StorageFormatMD ht WHERE ht.organizationId = ?1 and ht.deleted = ?2 and lower(ht.name) = lower(?3)")
    StorageFormatMD findByOrganizationIdAndDeletedAndName(Long orgId, boolean deleted, String name);


    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateStorageFormatName(String name, Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update StorageFormatMD set suggestedDataStatus = ?3 where id IN (?2) and organizationId = ?1")
    Integer updateStorageFormatStatus(Long orgId, Set<Long> ids, SuggestedDataStatus status);
}
