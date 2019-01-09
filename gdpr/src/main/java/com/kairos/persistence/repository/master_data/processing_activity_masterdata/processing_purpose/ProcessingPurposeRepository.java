package com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurposeMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurposeMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurposeMD;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface ProcessingPurposeRepository extends JpaRepository<ProcessingPurposeMD, Long> {

    @Query(value = "SELECT PP FROM ProcessingPurposeMD PP WHERE PP.countryId = ?1 and PP.deleted = ?2 and lower(PP.name) IN ?3")
    List<ProcessingPurposeMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update ProcessingPurposeMD set name = ?1 where id= ?2")
    Integer updateProcessingPurposeName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update ProcessingPurposeMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT PP FROM ProcessingPurposeMD PP WHERE PP.id = ?1 and PP.countryId = ?2 and PP.deleted = ?3")
    ProcessingPurposeMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingPurposeResponseDTO(PP.id, PP.name, PP.organizationId, PP.suggestedDataStatus, PP.suggestedDate )  FROM ProcessingPurposeMD PP WHERE PP.countryId = ?1 and PP.deleted = false order by createdAt desc")
    List<ProcessingPurposeResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update ProcessingPurposeMD set deleted = true where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateProcessingPurposeStatus(Long countryId, Set<Long> ids);

    @Query(value = "SELECT PP FROM ProcessingPurposeMD PP WHERE PP.id IN (?1) and PP.deleted = false")
    List<ProcessingPurposeMD> findAllByIds( Set<Long> ids);

    @Query(value = "Select PP from ProcessingPurposeMD PP where PP.countryId = ?2 and lower(PP.name) = lower(?1) and PP.deleted = false")
    ProcessingPurposeMD findByNameAndCountryId(String name, Long countryId);

}
