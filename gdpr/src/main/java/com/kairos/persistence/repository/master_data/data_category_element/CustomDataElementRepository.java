package com.kairos.persistence.repository.master_data.data_category_element;

import com.kairos.persistence.model.master_data.data_category_element.DataElement;

import java.util.List;
import java.util.Set;

public interface CustomDataElementRepository {


    List<DataElement> findByCountryIdAndNames(Long countryId, Set<String> names);

    List<DataElement> findByUnitIdAndNames(Long unitId, Set<String> names);

}
