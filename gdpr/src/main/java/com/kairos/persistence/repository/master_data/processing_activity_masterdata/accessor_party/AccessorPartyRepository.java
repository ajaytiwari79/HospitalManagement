package com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.AccessorPartyResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface AccessorPartyRepository extends CustomGenericRepository<AccessorPartyMD> {

    @Query(value = "SELECT new com.kairos.response.dto.common.AccessorPartyResponseDTO(AP.id, AP.name, AP.organizationId, AP.suggestedDataStatus, AP.suggestedDate )  FROM AccessorPartyMD AP WHERE AP.countryId = ?1 and AP.deleted = false order by createdAt desc")
    List<AccessorPartyResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);


    @Query(value = "SELECT new com.kairos.response.dto.common.AccessorPartyResponseDTO(AP.id, AP.name, AP.organizationId, AP.suggestedDataStatus, AP.suggestedDate )  FROM AccessorPartyMD AP WHERE AP.organizationId = ?1 and AP.deleted = false order by createdAt desc")
    List<AccessorPartyResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);
}
