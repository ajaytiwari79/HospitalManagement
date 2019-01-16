package com.kairos.persistence.repository.data_inventory.processing_activity;

import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ProcessingActivityRepository extends JpaRepository<ProcessingActivityMD, Long> {

    //@Query(value = "Select PA.name from ProcessingActivityMD PA where PA.organizationId = ?1 and PA.asset.id = ?2 and PA.deleted = false and PA.subProcess = false")
    //List<String> findAllProcessingActivityLinkWithAssetById(Long orgId, Long assetId);

    /*@Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingActivity findByUnitIdAndId(Long unitId, BigInteger id);

    ProcessingActivity findByid(BigInteger id);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false,subProcess:true}")
    List<ProcessingActivity> findSubProcessingActivitiesByIds(Long unitId, Set<BigInteger> ids);

    @Query("{_id:{$in:?0},deleted:false,subProcess:true}")
    List<ProcessingActivityResponseDTO> findAllSubProcessingActivitiesByIds(List<BigInteger> ids);

    @Query("{organizationId:?0,assetId:?1,deleted:false,subProcess:false}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkWithAssetById(Long unitId, BigInteger assetId);

    @Query("{organizationId:?0,deleted:false,active:true,responsibilityType:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkedWithResponsibilityType(Long unitId, BigInteger responsibilityTypeId);

    @Query("{organizationId:?0,deleted:false,active:true,accessorParties:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkedWithAccessorParty(Long unitId, BigInteger accessorPartyId);

    @Query("{organizationId:?0,deleted:false,active:true,transferMethods:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkedWithTransferMethod(Long unitId, BigInteger transferMethodId);

    @Query("{organizationId:?0,deleted:false,active:true,dataSources:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkedWithDataSource(Long unitId, BigInteger dataSourceId);

    @Query("{organizationId:?0,deleted:false,active:true,processingPurposes:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkedWithProcessingPurpose(Long unitId, BigInteger processingPurposeId);

    @Query("{organizationId:?0,deleted:false,active:true,processingLegalBasis:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicDTO> findAllProcessingActivityLinkedWithProcessingLegalBasis(Long unitId, BigInteger processingLegalBasisId);*/


}
