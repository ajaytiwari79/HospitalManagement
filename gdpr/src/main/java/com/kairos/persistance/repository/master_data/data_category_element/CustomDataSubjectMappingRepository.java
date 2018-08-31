package com.kairos.persistance.repository.master_data.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomDataSubjectMappingRepository {


    DataSubjectMapping findByName(Long countryId, String name);


    DataSubjectMappingResponseDTO getDataSubjectWithDataCategoryAndDataElementByCountryId(Long countryId, BigInteger dataSubjectId);

    List<DataSubjectMappingResponseDTO> getAllDataSubjectWithDataCategoryAndDataElementByCountryId(Long countryId);

    List<DataSubjectMapping> findByNameListAndUnitId(Long unitId, Set<String> names);

    DataSubjectMapping findByNameAndUnitId(Long unitId, String name);

    List<DataSubjectMappingResponseDTO> getAllDataSubjectWithDataCategoryAndDataElementByUnitId(Long unitId);

    DataSubjectMappingResponseDTO getDataSubjectWithDataCategoryAndDataElementByUnitId(Long unitId, BigInteger dataSubjectId);


}
