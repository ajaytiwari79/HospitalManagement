package com.kairos.persistence.repository.master_data.asset_management.hosting_type;


import com.kairos.persistence.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostingTypeRepository extends CustomGenericRepository<HostingType> {

    @Query(value = "SELECT new com.kairos.response.dto.common.HostingTypeResponseDTO(ht.id, ht.name, ht.organizationId, ht.suggestedDataStatus, ht.suggestedDate )  FROM HostingType ht WHERE ht.countryId = ?1 and ht.deleted = false order by createdAt desc")
    List<HostingTypeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.HostingTypeResponseDTO(ht.id, ht.name, ht.organizationId, ht.suggestedDataStatus, ht.suggestedDate )  FROM HostingType ht WHERE ht.organizationId = ?1 and ht.deleted = false order by createdAt desc")
    List<HostingTypeResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
