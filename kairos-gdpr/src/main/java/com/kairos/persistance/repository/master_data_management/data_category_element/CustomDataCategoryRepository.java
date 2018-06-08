package com.kairos.persistance.repository.master_data_management.data_category_element;

import com.kairos.response.dto.master_data.DataCategoryResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomDataCategoryRepository {



    DataCategoryResponseDto getDataCategoryWithDataElementById(Long countryId, BigInteger id);

    List<DataCategoryResponseDto> getAllDataCategoryWithDataElement(Long countryId);

}
