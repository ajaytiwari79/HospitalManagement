package com.kairos.persistence.repository.master_data.asset_management.tech_security_measure;


import com.kairos.dto.gdpr.metadata.TechnicalSecurityMeasureDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationalSecurityMeasureMD;
import com.kairos.persistence.model.master_data.default_asset_setting.TechnicalSecurityMeasureMD;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TechnicalSecurityMeasureMDRepository extends JpaRepository<TechnicalSecurityMeasureMD, Integer> {

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.countryId = ?1 and tsm.deleted = ?2 and lower(tsm.name) IN ?3")
    List<TechnicalSecurityMeasureMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set name = ?1 where id= ?2")
    Integer updateTechnicalSecurityMeasureName(String name, Integer id);


    @Transactional
    @Modifying
    @Query(value = "update TechnicalSecurityMeasureMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Integer id, Long countryId);

    @Query(value = "SELECT tsm FROM TechnicalSecurityMeasureMD tsm WHERE tsm.id = ?1 and tsm.countryId = ?2 and tsm.deleted = ?3")
    TechnicalSecurityMeasureMD findByIdAndCountryIdAndDeleted(Integer id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO(tsm.id, tsm.name, tsm.organizationId, tsm.suggestedDataStatus, tsm.suggestedDate) FROM TechnicalSecurityMeasureMD tsm WHERE tsm.countryId = ?1 and tsm.deleted = false order by createdAt desc")
    List<TechnicalSecurityMeasureResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

}
