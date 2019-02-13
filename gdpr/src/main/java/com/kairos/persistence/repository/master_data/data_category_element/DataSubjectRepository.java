package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataSubjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface DataSubjectRepository extends JpaRepository<DataSubjectMapping, Long>{

    @Query(value = "Select DS from DataSubjectMapping DS where DS.countryId = ?1 and lower(DS.name) = lower(?2) and DS.deleted = false")
    DataSubjectMapping findByCountryIdAndName(Long countryId, String name);

    @Transactional
    @Modifying
    @Query(value = "update DataSubjectMapping set deleted = true where id = ?1 and deleted = false")
    Integer safeDeleteById(Long id);

    @Query(value = "Select DS from DataSubjectMapping DS where DS.countryId = ?1 and DS.id = ?2 and DS.deleted =  false")
    DataSubjectMapping getDataSubjectByCountryIdAndId(Long countryId, Long id);

    @Query(value = "Select DS from DataSubjectMapping DS where DS.countryId = ?1 and DS.deleted = false Order By DS.createdAt desc")
    List<DataSubjectMapping> getAllDataSubjectByCountryId(Long countryId);

    @Query(value = "Select DS from DataSubjectMapping DS where DS.organizationId = ?1 and lower(DS.name) = lower(?2) and DS.deleted = false")
    DataSubjectMapping findByNameAndUnitId(Long unitId, String name);

    @Query(value = "Select DS from DataSubjectMapping DS where DS.organizationId = ?1 and DS.id = ?2 and DS.deleted =  false")
    DataSubjectMapping getDataSubjectByUnitIdAndId(Long unit, Long id);

    @Query(value = "Select DS from DataSubjectMapping DS where DS.organizationId = ?1 and DS.deleted = false Order By DS.createdAt desc")
    List<DataSubjectMapping> getAllDataSubjectByUnitId(Long unitId);

    @Query(value = "select ds.name from data_subject_mapping ds Inner Join data_subject_mapping_data_categories dc  ON ds.id = dc.data_subject_mapping_id where ds.organization_id = ?1 and dc.data_categories_id = ?2", nativeQuery = true)
    List<String> findDataSubjectsLinkWithDataCategoryByUnitIdAndDataCategoryId(Long unitId, Long id);

    @Query(value = "select ds.name from data_subject_mapping ds Inner Join data_subject_mapping_data_categories dc  ON ds.id = dc.data_subject_mapping_id where ds.country_id = ?1 and dc.data_categories_id = ?2", nativeQuery = true)
    List<String> findDataSubjectsLinkWithDataCategoryByCountryIdAndDataCategoryId(Long countryId, Long id);
}
