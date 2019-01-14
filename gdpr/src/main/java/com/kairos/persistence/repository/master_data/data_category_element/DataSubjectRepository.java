package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMappingMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface DataSubjectRepository extends JpaRepository<DataSubjectMappingMD, Long>{

    @Query(value = "Select DS from DataSubjectMappingMD DS where DS.countryId = ?1 and lower(DS.name) = lower(?2) and DS.deleted = false")
    DataSubjectMappingMD findByCountryIdAndName(Long countryId, String name);

    @Transactional
    @Modifying
    @Query(value = "update DataSubjectMappingMD set deleted = true where id = ?1 and deleted = false")
    Integer safeDeleteById(Long id);

    @Query(value = "Select DS from DataSubjectMappingMD DS where DS.countryId = ?1 and DS.id = ?2 and DS.deleted =  false")
    DataSubjectMappingMD getDataSubjectByCountryIdAndId(Long countryId, Long id);

    @Query(value = "Select DS from DataSubjectMappingMD DS where DS.countryId = ?1 and DS.deleted = false Order By DS.createdAt desc")
    List<DataSubjectMappingMD> getAllDataSubjectWithDataCategoryAndDataElementByCountryId(Long countryId);
}
