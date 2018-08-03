package com.kairos.persistance.repository.master_data.data_category_element;

import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomDataCategoryRepository {

    DataCategory findByName(Long countryId, Long organizationId, String name);

    DataCategoryResponseDto getDataCategoryWithDataElementById(Long countryId,Long organizationId,BigInteger id);

    List<DataCategoryResponseDto> getAllDataCategoryWithDataElement(Long countryId,Long organizationId);


    List<DataCategory> findByNamesAndUnitId(Long unitId, Set<String> names);

}
