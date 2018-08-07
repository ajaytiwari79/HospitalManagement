package com.kairos.persistance.repository.master_data.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomDataSubjectMappingRepository {


    DataSubjectMapping findByName(Long countryId, Long organizationId, String name);


    DataSubjectMappingResponseDto getDataSubjectAndMappingWithDataCategory(Long countryId,Long organizationId, BigInteger id);

    List<DataSubjectMappingResponseDto> getAllDataSubjectAndMappingWithDataCategory(Long countryId,Long organizationId);


    List<DataSubjectMapping> findByNamesAndUnitId(Long unitId, Set<String> names);
}
