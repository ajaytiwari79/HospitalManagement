package com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method;


import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.TransferMethodMD;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import com.kairos.response.dto.common.TransferMethodResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface TransferMethodRepository extends JpaRepository<TransferMethodMD, Long> {

    @Query(value = "SELECT TM FROM TransferMethodMD TM WHERE TM.countryId = ?1 and TM.deleted = ?2 and lower(TM.name) IN ?3")
    List<TransferMethodMD> findByCountryIdAndDeletedAndNameIn(Long countryId, boolean deleted, List<String> userNames);

    @Transactional
    @Modifying
    @Query(value = "update TransferMethodMD set name = ?1 where id= ?2")
    Integer updateTransferMethodName(String name, Long id);


    @Transactional
    @Modifying
    @Query(value = "update TransferMethodMD set deleted = true where id = ?1 and countryId = ?2")
    Integer deleteByIdAndCountryId(Long id, Long countryId);

    @Query(value = "SELECT TM FROM TransferMethodMD TM WHERE TM.id = ?1 and TM.countryId = ?2 and TM.deleted = ?3")
    TransferMethodMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);


    @Query(value = "SELECT new com.kairos.response.dto.common.TransferMethodResponseDTO(TM.id, TM.name, TM.organizationId, TM.suggestedDataStatus, TM.suggestedDate )  FROM TransferMethodMD TM WHERE TM.countryId = ?1 and TM.deleted = false order by createdAt desc")
    List<TransferMethodResponseDTO> findAllByCountryIdAndSortByCreatedDate(Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update TransferMethodMD set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateTransferMethodStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "SELECT TM FROM TransferMethodMD TM WHERE TM.id IN (?1) and TM.deleted = false")
    List<TransferMethodMD> findAllByIds( Set<Long> ids);

    @Query(value = "Select TM from TransferMethodMD TM where TM.countryId = ?2 and lower(TM.name) = lower(?1) and TM.deleted = false")
    TransferMethodMD findByNameAndCountryId(String name, Long countryId);

}
