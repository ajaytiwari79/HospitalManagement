package com.kairos.persistence.repository.master_data.asset_management.hosting_provider;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.model.master_data.default_asset_setting.HostingProviderMD;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface HostingProviderMDRepository extends JpaRepository<HostingProviderMD, Long> {

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.countryId = ?1 and hp.deleted = ?2 and lower(hp.name) IN ?3")
    List<HostingProviderMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set name = ?1 where id= ?2 and countryId = ?3")
    Integer updateMasterHostingProviderName(String name, Long id , Long countryId);


    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.id = ?1 and hp.countryId = ?2 and hp.deleted = ?3")
    HostingProviderMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingProviderResponseDTO(hp.id, hp.name, hp.organizationId, hp.suggestedDataStatus, hp.suggestedDate )  FROM HostingProviderMD hp WHERE hp.countryId = ?1 and hp.deleted = false order by createdAt desc")
    List<HostingProviderResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.countryId = ?1 and hp.deleted = ?2 and lower(hp.name) = lower(?3)")
    HostingProviderMD findByCountryIdAndDeletedAndName(Long countryId, boolean deleted, String name);



    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.organizationId = ?1 and hp.deleted = ?2 and lower(hp.name) IN ?3")
    List<HostingProviderMD> findByUnitIdAndDeletedAndNameIn(Long orgId, boolean deleted, List<String> name);

    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateHostingProviderName(String name, Long id, Long orgId);

    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateDataDisposalStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);


    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set deleted = true where id = ?1 and organizationId = ?2")
    Integer deleteByIdAndOrganizationId(Long id, Long orgId);

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.id = ?1 and hp.organizationId = ?2 and hp.deleted = ?3")
    HostingProviderMD findByIdAndOrganizationIdAndDeleted(Long id, Long orgId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingProviderResponseDTO(hp.id, hp.name, hp.organizationId, hp.suggestedDataStatus, hp.suggestedDate )  FROM HostingProviderMD hp WHERE hp.organizationId = ?1 and hp.deleted = false order by createdAt desc")
    List<HostingProviderResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.organizationId = ?1 and hp.deleted = ?2 and lower(hp.name) = lower(?3)")
    HostingProviderMD findByOrganizationIdAndDeletedAndName(Long orgId, boolean deleted, String name);

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.id IN (?1) and hp.deleted = false")
    List<HostingProviderMD> findAllByIds( Set<Long> ids);

}
