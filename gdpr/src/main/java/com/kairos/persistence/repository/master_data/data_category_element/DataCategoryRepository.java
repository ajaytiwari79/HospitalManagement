package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataCategoryMD;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
//@JaversSpringDataAuditable
public interface DataCategoryRepository extends JpaRepository<DataCategoryMD,Long> {

    @Query(value = "Select DC from DataCategoryMD DC where DC.organizationId = ?1 and lower(DC.name) = lower(?2) and DC.deleted = false")
    DataCategoryMD findByUnitIdAndName(Long refId, String name);

    @Query(value = "Select DC from DataCategoryMD DC where DC.countryId = ?1 and lower(DC.name) = lower(?2) and DC.deleted = false")
    DataCategoryMD findByCountryIdName(Long refId, String name);

    //@Query(value = "select dc.id, dc.name, de.id, de.name, de.deleted from data_categorymd dc LEFT JOIN data_elementmd de ON dc.id = de.data_category_id where dc.country_id = ?1", nativeQuery = true)
    @Query("Select DC from DataCategoryMD DC where DC.countryId = ?1 and DC.deleted = false")
    List<DataCategoryMD> getAllDataCategoriesByCountryId(Long countryId);


    @Query("Select DC from DataCategoryMD DC where DC.organizationId = ?1 and DC.deleted = false")
    List<DataCategoryMD> getAllDataCategoriesByUnitId(Long unitId);

    @Query("Select DC from DataCategoryMD DC where DC.countryId = ?1 and DC.id = ?2 and DC.deleted = false")
    DataCategoryMD getDataCategoryByCountryIdAndId(Long countryId, Long id);

    @Query("Select DC from DataCategoryMD DC where DC.organizationId = ?1 and DC.id = ?2 and DC.deleted = false")
    DataCategoryMD getDataCategoryByUnitIdAndId(Long unitId, Long id);

    @Query("Select DC from DataCategoryMD DC where DC.id IN (?1) and DC.deleted = false")
    List<DataCategoryMD> getAllDataCategoriesByIds(Set<Long> ids);

   /* DataCategory findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataCategory findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    DataCategory findByCountryIdAndName(Long countryId, Long organizationId, String name);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataCategory> findDataCategoryByIds(Long countryId, Set<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataCategory findByUnitIdAndId(Long unitId, BigInteger id);*/




}
