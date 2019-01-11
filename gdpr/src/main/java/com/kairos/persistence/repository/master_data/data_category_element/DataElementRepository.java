package com.kairos.persistence.repository.master_data.data_category_element;

import com.kairos.persistence.model.master_data.data_category_element.DataElementMD;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface DataElementRepository extends JpaRepository<DataElementMD, Long> {

    @Query(value = "SELECT DE FROM DataElementMD DE WHERE DE.organizationId = ?1 and lower(DE.name) IN ?2 and DE.deleted = false")
    List<DataElementMD> findByUnitIdAndNames(Long refId , Set<String> userNames);

    @Query(value = "SELECT DE FROM DataElementMD DE WHERE DE.countryId = ?1 and lower(DE.name) IN ?2 and DE.deleted = false")
    List<DataElementMD> findByCountryIdAndNames(Long refId , Set<String> userNames);

    @Query(value = "Select DE from DataElementMD DE where DE.organizationId = ?1 and DE.id IN (?2) and DE.deleted = false")
    List<DataElementMD> findByUnitIdAndIds(Long refId, Set<Long> ids);

    @Query(value = "Select DE from DataElementMD DE where DE.countryId = ?1 and DE.id IN (?2) and DE.deleted = false")
    List<DataElementMD> findByCountryIdAndIds(Long refId, Set<Long> ids);


    /*DataElement findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataElement findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{deleted:false,_id:?0}")
    DataElementBasicResponseDTO getByIdAndNonDeleted(BigInteger id);


    @Query("{deleted:false,countryId:?0}")
    List<DataElementBasicResponseDTO> getAllDataElementByCountryId(Long countryId);


    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<DataElement> findByUnitIdAndIds(Long unitId, Set<BigInteger> ids);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataElement> findByCountryIdAndIds(Long countryId, Set<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0}")
    List<DataElementBasicResponseDTO> getAllDataElementByUnitId(Long unitId);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElementBasicResponseDTO getDataElementByUnitIdAndId(Long organizationId, BigInteger id);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElement findByUnitIdAndId(Long unitId, BigInteger id);
*/

}
