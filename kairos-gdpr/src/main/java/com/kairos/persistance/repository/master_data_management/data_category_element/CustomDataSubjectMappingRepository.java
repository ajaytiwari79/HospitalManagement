package com.kairos.persistance.repository.master_data_management.data_category_element;


import com.kairos.response.dto.master_data.DataSubjectMappingResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomDataSubjectMappingRepository {

    DataSubjectMappingResponseDto getDataSubjectAndMappingWithDataCategory(Long countryId, BigInteger id);

    List<DataSubjectMappingResponseDto> getAllDataSubjectAndMappingWithDataCategory(Long countryId);


}
