package com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSourceMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface DataSourceRepository extends CustomGenericRepository<DataSourceMD> {


    @Query(value = "SELECT new com.kairos.response.dto.common.DataSourceResponseDTO(DS.id, DS.name, DS.organizationId, DS.suggestedDataStatus, DS.suggestedDate )  FROM DataSourceMD DS WHERE DS.countryId = ?1 and DS.deleted = false order by createdAt desc")
    List<DataSourceResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.DataSourceResponseDTO(DS.id, DS.name, DS.organizationId, DS.suggestedDataStatus, DS.suggestedDate )  FROM DataSourceMD DS WHERE DS.organizationId = ?1 and DS.deleted = false order by createdAt desc")
    List<DataSourceResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
