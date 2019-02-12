package com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface DataSourceRepository extends CustomGenericRepository<DataSource> {


    @Query(value = "SELECT new com.kairos.response.dto.common.DataSourceResponseDTO(DS.id, DS.name, DS.organizationId, DS.suggestedDataStatus, DS.suggestedDate )  FROM DataSource DS WHERE DS.countryId = ?1 and DS.deleted = false order by createdAt desc")
    List<DataSourceResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Query(value = "SELECT new com.kairos.response.dto.common.DataSourceResponseDTO(DS.id, DS.name, DS.organizationId, DS.suggestedDataStatus, DS.suggestedDate )  FROM DataSource DS WHERE DS.organizationId = ?1 and DS.deleted = false order by createdAt desc")
    List<DataSourceResponseDTO> findAllByOrganizationIdAndSortByCreatedDate(Long orgId);

}
