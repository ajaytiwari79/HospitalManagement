package com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityTypeMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsibilityTypeRepository extends CustomGenericRepository<ResponsibilityTypeMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.ResponsibilityTypeResponseDTO(RT.id, RT.name, RT.organizationId, RT.suggestedDataStatus, RT.suggestedDate )  FROM ResponsibilityTypeMD RT WHERE RT.countryId = ?1 and RT.deleted = false order by createdAt desc")
    List<ResponsibilityTypeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.ResponsibilityTypeResponseDTO(RT.id, RT.name, RT.organizationId, RT.suggestedDataStatus, RT.suggestedDate )  FROM ResponsibilityTypeMD RT WHERE RT.organizationId = ?1 and RT.deleted = false order by createdAt desc")
    List<ResponsibilityTypeResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
