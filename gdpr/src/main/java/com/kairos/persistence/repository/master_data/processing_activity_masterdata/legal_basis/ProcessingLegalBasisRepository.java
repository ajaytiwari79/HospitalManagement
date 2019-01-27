package com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasisMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingLegalBasisRepository extends CustomGenericRepository<ProcessingLegalBasisMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO(LB.id, LB.name, LB.organizationId, LB.suggestedDataStatus, LB.suggestedDate )  FROM ProcessingLegalBasisMD LB WHERE LB.countryId = ?1 and LB.deleted = false order by createdAt desc")
    List<ProcessingLegalBasisResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO(LB.id, LB.name, LB.organizationId, LB.suggestedDataStatus, LB.suggestedDate )  FROM ProcessingLegalBasisMD LB WHERE LB.organizationId = ?1 and LB.deleted = false order by createdAt desc")
    List<ProcessingLegalBasisResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);
}
