package com.kairos.persistance.repository.master_data.data_category_element;

import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomDataCategoryRepository {



    DataCategory findByName(Long countryId, Long organizationId, String name);

    DataCategoryResponseDto getDataCategoryWithDataElementById(Long countryId,Long organizationId,BigInteger id);

    List<DataCategoryResponseDto> getAllDataCategoryWithDataElement(Long countryId,Long organizationId);

}
