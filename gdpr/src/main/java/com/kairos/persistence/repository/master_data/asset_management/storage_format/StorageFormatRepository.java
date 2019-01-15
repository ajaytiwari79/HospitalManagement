package com.kairos.persistence.repository.master_data.asset_management.storage_format;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormatMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface StorageFormatRepository extends CustomGenericRepository<StorageFormatMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormatMD sf WHERE sf.countryId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.StorageFormatResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM StorageFormatMD sf WHERE sf.organizationId = ?1 and sf.deleted = false order by createdAt desc")
    List<StorageFormatResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
