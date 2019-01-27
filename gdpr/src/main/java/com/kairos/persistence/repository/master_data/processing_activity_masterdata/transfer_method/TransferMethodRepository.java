package com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethodMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferMethodRepository extends CustomGenericRepository<TransferMethodMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.TransferMethodResponseDTO(TM.id, TM.name, TM.organizationId, TM.suggestedDataStatus, TM.suggestedDate )  FROM TransferMethodMD TM WHERE TM.countryId = ?1 and TM.deleted = false order by createdAt desc")
    List<TransferMethodResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.TransferMethodResponseDTO(TM.id, TM.name, TM.organizationId, TM.suggestedDataStatus, TM.suggestedDate )  FROM TransferMethodMD TM WHERE TM.organizationId = ?1 and TM.deleted = false order by createdAt desc")
    List<TransferMethodResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
