package com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorParty;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface AccessorPartyRepository extends CustomGenericRepository<AccessorParty> {

    @Query(value = "SELECT new com.kairos.response.dto.common.AccessorPartyResponseDTO(AP.id, AP.name, AP.organizationId, AP.suggestedDataStatus, AP.suggestedDate )  FROM AccessorParty AP WHERE AP.countryId = ?1 and AP.deleted = false order by createdAt desc")
    List<AccessorPartyResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);


    @Query(value = "SELECT new com.kairos.response.dto.common.AccessorPartyResponseDTO(AP.id, AP.name, AP.organizationId, AP.suggestedDataStatus, AP.suggestedDate )  FROM AccessorParty AP WHERE AP.organizationId = ?1 and AP.deleted = false order by createdAt desc")
    List<AccessorPartyResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);
}
