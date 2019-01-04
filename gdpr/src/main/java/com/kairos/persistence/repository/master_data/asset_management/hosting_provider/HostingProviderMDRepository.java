package com.kairos.persistence.repository.master_data.asset_management.hosting_provider;


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

@Repository
public interface HostingProviderMDRepository extends JpaRepository<HostingProviderMD, Integer> {

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.countryId = ?1 and hp.deleted = ?2 and lower(hp.name) IN ?3")
    List<HostingProviderMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set name = ?1 where id= ?2")
    Integer updateHostingProviderName(String name, Integer id);


    @Transactional
    @Modifying
    @Query(value = "update HostingProviderMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Integer id, Long countryId);

    @Query(value = "SELECT hp FROM HostingProviderMD hp WHERE hp.id = ?1 and hp.countryId = ?2 and hp.deleted = ?3")
    HostingProviderMD findByIdAndCountryIdAndDeleted(Integer id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingProviderResponseDTO(hp.id, hp.name, hp.organizationId, hp.suggestedDataStatus, hp.suggestedDate )  FROM HostingProviderMD hp WHERE hp.countryId = ?1 and hp.deleted = false order by createdAt desc")
    List<HostingProviderResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

}
