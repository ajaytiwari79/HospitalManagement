package com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.AccessorPartyMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasisMD;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingLegalBasisMD;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface ProcessingLegalBasisRepository extends JpaRepository<ProcessingLegalBasisMD, Long> {

    @Query(value = "SELECT LB FROM ProcessingLegalBasisMD LB WHERE LB.countryId = ?1 and LB.deleted = ?2 and lower(LB.name) IN ?3")
    List<ProcessingLegalBasisMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update ProcessingLegalBasisMD set name = ?1 where id= ?2")
    Integer updateLegalBasisName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update ProcessingLegalBasisMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT LB FROM ProcessingLegalBasisMD LB WHERE LB.id = ?1 and LB.countryId = ?2 and LB.deleted = ?3")
    ProcessingLegalBasisMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO(LB.id, LB.name, LB.organizationId, LB.suggestedDataStatus, LB.suggestedDate )  FROM ProcessingLegalBasisMD LB WHERE LB.countryId = ?1 and LB.deleted = false order by createdAt desc")
    List<ProcessingLegalBasisResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update ProcessingLegalBasisMD set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateProcessingLegalBasisStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT LB FROM ProcessingLegalBasisMD LB WHERE LB.id IN (?1) and LB.deleted = false")
    List<ProcessingLegalBasisMD> findAllByIds( Set<Long> ids);

    @Query(value = "Select LB from ProcessingLegalBasisMD LB where LB.countryId = ?2 and lower(LB.name) = lower(?1) and LB.deleted = false")
    ProcessingLegalBasisMD findByNameAndCountryId(String name, Long countryId);

}
