package com.kairos.persistence.repository.master_data.asset_management.hosting_provider;


import com.kairos.persistence.model.master_data.default_asset_setting.HostingProviderMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.HostingProviderResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostingProviderRepository extends CustomGenericRepository<HostingProviderMD> {


    @Query(value = "SELECT new com.kairos.response.dto.common.HostingProviderResponseDTO(hp.id, hp.name, hp.organizationId, hp.suggestedDataStatus, hp.suggestedDate )  FROM HostingProviderMD hp WHERE hp.countryId = ?1 and hp.deleted = false order by createdAt desc")
    List<HostingProviderResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.HostingProviderResponseDTO(hp.id, hp.name, hp.organizationId, hp.suggestedDataStatus, hp.suggestedDate )  FROM HostingProviderMD hp WHERE hp.organizationId = ?1 and hp.deleted = false order by createdAt desc")
    List<HostingProviderResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
