package com.kairos.persistance.repository.master_data.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomDataSubjectMappingRepository {


    DataSubjectMapping findByName(Long countryId, Long organizationId, String name);


    DataSubjectMappingResponseDTO getDataSubjectAndMappingWithDataCategory(Long countryId, Long organizationId, BigInteger dataSubjectId);

    List<DataSubjectMappingResponseDTO> getAllDataSubjectAndMappingWithDataCategory(Long countryId, Long organizationId);

    List<DataSubjectMapping> findByNamesAndUnitId(Long unitId, Set<String> names);

    List<DataSubjectMappingResponseDTO> getAllDataSubjectAndMappingWithDataCategoryByUnitId(Long unitId);

    DataSubjectMappingResponseDTO getDataSubjectAndMappingWithDataCategoryByUinitId(Long unitId, BigInteger dataSubjectId);


}
