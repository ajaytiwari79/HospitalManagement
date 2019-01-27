package com.kairos.persistence.repository.master_data.asset_management.tech_security_measure;


import com.kairos.persistence.model.master_data.default_asset_setting.TechnicalSecurityMeasureMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicalSecurityMeasureRepository extends CustomGenericRepository<TechnicalSecurityMeasureMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO(tsm.id, tsm.name, tsm.organizationId, tsm.suggestedDataStatus, tsm.suggestedDate) FROM TechnicalSecurityMeasureMD tsm WHERE tsm.countryId = ?1 and tsm.deleted = false order by createdAt desc")
    List<TechnicalSecurityMeasureResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO(sf.id, sf.name, sf.organizationId, sf.suggestedDataStatus, sf.suggestedDate )  FROM TechnicalSecurityMeasureMD sf WHERE sf.organizationId = ?1 and sf.deleted = false order by createdAt desc")
    List<TechnicalSecurityMeasureResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
