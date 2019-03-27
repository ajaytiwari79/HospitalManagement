package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DataSubjectRepository extends JpaRepository<DataSubject, Long>{

    @Query(value = "Select DS from DataSubject DS where DS.countryId = ?1 and lower(DS.name) = lower(?2) and DS.deleted = false")
    DataSubject findByCountryIdAndName(Long countryId, String name);

    @Transactional
    @Modifying
    @Query(value = "update DataSubject set deleted = true where id = ?1 and deleted = false")
    Integer safeDeleteById(Long id);

    @Query(value = "Select DS from DataSubject DS where DS.countryId = ?1 and DS.id = ?2 and DS.deleted =  false")
    DataSubject getDataSubjectByCountryIdAndId(Long countryId, Long id);

    @Query(value = "Select DS from DataSubject DS where DS.countryId = ?1 and DS.deleted = false Order By DS.createdAt desc")
    List<DataSubject> getAllDataSubjectByCountryId(Long countryId);

    @Query(value = "Select DS from DataSubject DS where DS.organizationId = ?1 and lower(DS.name) = lower(?2) and DS.deleted = false")
    DataSubject findByNameAndUnitId(Long organizationId, String name);

    @Query(value = "Select DS from DataSubject DS where DS.organizationId = ?1 and DS.id = ?2 and DS.deleted =  false")
    DataSubject getDataSubjectByUnitIdAndId(Long unit, Long id);

    @Query(value = "Select DS from DataSubject DS where DS.organizationId = ?1 and DS.deleted = false Order By DS.createdAt desc")
    List<DataSubject> getAllDataSubjectByUnitId(Long organizationId);

    @Query(value = "select ds.name from data_subject ds Inner Join data_subject_categories dc  ON ds.id = dc.data_subject_id where ds.organization_id = ?1 and dc.data_category_id = ?2", nativeQuery = true)
    List<String> findDataSubjectsLinkWithDataCategoryByUnitIdAndDataCategoryId(Long organizationId, Long id);

    @Query(value = "select ds.name from data_subject ds Inner Join data_subject_categories dc  ON ds.id = dc.data_subject_id where ds.country_id = ?1 and dc.data_category_id = ?2", nativeQuery = true)
    List<String> findDataSubjectsLinkWithDataCategoryByCountryIdAndDataCategoryId(Long countryId, Long id);
}
