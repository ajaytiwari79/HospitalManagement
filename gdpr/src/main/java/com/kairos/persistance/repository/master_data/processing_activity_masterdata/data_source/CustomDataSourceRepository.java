package com.kairos.persistance.repository.master_data.processing_activity_masterdata.data_source;


import com.kairos.response.dto.common.DataSourceResponseDTO;

import java.util.List;

public interface CustomDataSourceRepository {

    List<DataSourceResponseDTO> getAllNotInheritedDataSourceFromParentOrgAndUnitDataSource(Long countryId, Long parentOrganizationId, Long organizationId);

}
