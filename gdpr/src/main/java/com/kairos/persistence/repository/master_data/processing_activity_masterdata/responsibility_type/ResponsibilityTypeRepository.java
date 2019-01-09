package com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityTypeMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityTypeMD;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface ResponsibilityTypeRepository extends JpaRepository<ResponsibilityTypeMD, Long> {

    @Query(value = "SELECT RT FROM ResponsibilityTypeMD RT WHERE RT.countryId = ?1 and RT.deleted = ?2 and lower(RT.name) IN ?3")
    List<ResponsibilityTypeMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update ResponsibilityTypeMD set name = ?1 where id= ?2")
    Integer updateResponsibilityTypeName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update ResponsibilityTypeMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT RT FROM ResponsibilityTypeMD RT WHERE RT.id = ?1 and RT.countryId = ?2 and RT.deleted = ?3")
    ResponsibilityTypeMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.ResponsibilityTypeResponseDTO(RT.id, RT.name, RT.organizationId, RT.suggestedDataStatus, RT.suggestedDate )  FROM ResponsibilityTypeMD RT WHERE RT.countryId = ?1 and RT.deleted = false order by createdAt desc")
    List<ResponsibilityTypeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update ResponsibilityTypeMD set deleted = true where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateResponsibilityTypeStatus(Long countryId, Set<Long> ids);

    @Query(value = "SELECT RT FROM ResponsibilityTypeMD RT WHERE RT.id IN (?1) and RT.deleted = false")
    List<ResponsibilityTypeMD> findAllByIds( Set<Long> ids);

    @Query(value = "Select RT from ResponsibilityTypeMD RT where RT.countryId = ?2 and lower(RT.name) = lower(?1) and RT.deleted = false")
    ResponsibilityTypeMD findByNameAndCountryId(String name, Long countryId);

}
