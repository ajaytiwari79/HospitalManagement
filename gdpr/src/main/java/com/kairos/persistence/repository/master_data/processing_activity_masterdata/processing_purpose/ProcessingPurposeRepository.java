package com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurposeMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurposeMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurposeMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface ProcessingPurposeRepository extends CustomGenericRepository<ProcessingPurposeMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingPurposeResponseDTO(PP.id, PP.name, PP.organizationId, PP.suggestedDataStatus, PP.suggestedDate )  FROM ProcessingPurposeMD PP WHERE PP.countryId = ?1 and PP.deleted = false order by createdAt desc")
    List<ProcessingPurposeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingPurposeResponseDTO(PP.id, PP.name, PP.organizationId, PP.suggestedDataStatus, PP.suggestedDate )  FROM ProcessingPurposeMD PP WHERE PP.organizationId = ?1 and PP.deleted = false order by createdAt desc")
    List<ProcessingPurposeResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
