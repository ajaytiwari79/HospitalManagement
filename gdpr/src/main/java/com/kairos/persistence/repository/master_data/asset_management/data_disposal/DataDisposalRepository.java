package com.kairos.persistence.repository.master_data.asset_management.data_disposal;

import com.kairos.persistence.model.master_data.default_asset_setting.DataDisposalMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.DataDisposalResponseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataDisposalRepository extends CustomGenericRepository<DataDisposalMD> {
    
    @Query(value = "SELECT new com.kairos.response.dto.common.DataDisposalResponseDTO(dd.id, dd.name, dd.organizationId, dd.suggestedDataStatus, dd.suggestedDate )  FROM #{#entityName} dd WHERE dd.countryId = ?1 and dd.deleted = false order by createdAt desc")
    List<DataDisposalResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.DataDisposalResponseDTO(d.id, d.name, d.organizationId, d.suggestedDataStatus, d.suggestedDate )  FROM DataDisposalMD d WHERE d.organizationId = ?1 and d.deleted = false order by createdAt desc")
    List<DataDisposalResponseDTO> findAllByUnitIdAndSortByCreatedDate(Long orgId);
}
