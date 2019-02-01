package com.kairos.persistence.repository.master_data.asset_management.storage_format;


import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageFormatRepository extends CustomGenericRepository<StorageFormat> {

    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormat sf WHERE sf.countryId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormat sf WHERE sf.organizationId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
