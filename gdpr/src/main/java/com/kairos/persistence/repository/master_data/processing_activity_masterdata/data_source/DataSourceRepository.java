package com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.DataSourceMD;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSourceMD, Long> {

    @Query(value = "SELECT DS FROM DataSourceMD DS WHERE DS.countryId = ?1 and DS.deleted = ?2 and lower(DS.name) IN ?3")
    List<DataSourceMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update DataSourceMD set name = ?1 where id= ?2")
    Integer updateDataSourceName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update DataSourceMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT DS FROM DataSourceMD DS WHERE DS.id = ?1 and DS.countryId = ?2 and DS.deleted = ?3")
    DataSourceMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.DataSourceResponseDTO(DS.id, DS.name, DS.organizationId, DS.suggestedDataStatus, DS.suggestedDate )  FROM DataSourceMD DS WHERE DS.countryId = ?1 and DS.deleted = false order by createdAt desc")
    List<DataSourceResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update DataSourceMD set deleted = true where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateDataSourceStatus(Long countryId, Set<Long> ids);

    @Query(value = "SELECT DS FROM DataSourceMD DS WHERE DS.id IN (?1) and DS.deleted = false")
    List<DataSourceMD> findAllByIds( Set<Long> ids);

    @Query(value = "Select DS from DataSourceMD DS where DS.countryId = ?2 and lower(DS.name) = lower(?1) and DS.deleted = false")
    DataSourceMD findByNameAndCountryId(String name, Long countryId);

}
