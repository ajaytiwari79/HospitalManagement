package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
////@JaversSpringDataAuditable
public interface DataCategoryRepository extends JpaRepository<DataCategory,Long> {

    @Query(value = "Select DC from DataCategory DC where DC.organizationId = ?1 and lower(DC.name) = lower(?2) and DC.deleted = false")
    DataCategory findByUnitIdAndName(Long refId, String name);

    @Query(value = "Select DC from DataCategory DC where DC.countryId = ?1 and lower(DC.name) = lower(?2) and DC.deleted = false")
    DataCategory findByCountryIdName(Long refId, String name);

    //@Query(value = "select dc.id, dc.name, de.id, de.name, de.deleted from data_category dc LEFT JOIN data_element de ON dc.id = de.data_category_id where dc.country_id = ?1", nativeQuery = true)
    @Query("Select DC from DataCategory DC where DC.countryId = ?1 and DC.deleted = false")
    List<DataCategory> getAllDataCategoriesByCountryId(Long countryId);

    @Query(value = "select * from data_category as DC left join data_subject_categories DSC ON DC.id = DSC.data_category_id where DC.country_id = ?1 and DC.deleted=false and DSC.data_category_id IS NULL", nativeQuery = true)
    List<DataCategory> getAllDataCategoriesByCountryIdNotLinkedWithDataSubject(Long countryId);


    @Query("Select DC from DataCategory DC where DC.organizationId = ?1 and DC.deleted = false")
    List<DataCategory> getAllDataCategoriesByUnitId(Long organizationId);

    @Query("Select DC from DataCategory DC where DC.countryId = ?1 and DC.id = ?2 and DC.deleted = false")
    DataCategory getDataCategoryByCountryIdAndId(Long countryId, Long id);

    @Query("Select DC from DataCategory DC where DC.organizationId = ?1 and DC.id = ?2 and DC.deleted = false")
    DataCategory getDataCategoryByUnitIdAndId(Long organizationId, Long id);

    @Query("Select DC from DataCategory DC where DC.id IN (?1) and DC.deleted = false")
    List<DataCategory> getAllDataCategoriesByIds(Set<Long> ids);

    @Transactional
    @Modifying
    @Query("Update DataCategory set deleted = true where id = ?1 and deleted =  false and organizationId = ?2")
    Integer safelyDeleteDataCategory(Long id, Long refId);

    @Transactional
    @Modifying
    @Query("Update DataCategory set deleted = true where id = ?1 and deleted =  false and countryId = ?2")
    Integer safelyDeleteMasterDataCategory(Long id, Long refId);




}
