package com.kairos.persistence.repository.master_data.data_category_element;

import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomDataCategoryRepository {

    DataCategory findByName(Long countryId, String name);

    DataCategoryResponseDTO getDataCategoryWithDataElementById(Long countryId, BigInteger id);

    List<DataCategoryResponseDTO> getAllDataCategoryWithDataElement(Long countryId);

    List<DataCategory> findByNamesAndUnitId(Long unitId, Set<String> names);

    List<DataCategoryResponseDTO> getAllDataCategoryWithDataElementByUnitId(Long unitId);

    DataCategoryResponseDTO getDataCategoryWithDataElementByUnitIdAndId(Long unitId, BigInteger dataCategoryId);

    DataCategory findByUnitIdAndName(Long unitId, String name);



}
