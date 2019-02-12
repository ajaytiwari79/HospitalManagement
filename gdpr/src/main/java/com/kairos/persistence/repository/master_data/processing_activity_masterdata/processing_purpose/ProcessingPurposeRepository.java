package com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface ProcessingPurposeRepository extends CustomGenericRepository<ProcessingPurpose> {

    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingPurposeResponseDTO(PP.id, PP.name, PP.organizationId, PP.suggestedDataStatus, PP.suggestedDate )  FROM ProcessingPurpose PP WHERE PP.countryId = ?1 and PP.deleted = false order by createdAt desc")
    List<ProcessingPurposeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingPurposeResponseDTO(PP.id, PP.name, PP.organizationId, PP.suggestedDataStatus, PP.suggestedDate )  FROM ProcessingPurpose PP WHERE PP.organizationId = ?1 and PP.deleted = false order by createdAt desc")
    List<ProcessingPurposeResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
