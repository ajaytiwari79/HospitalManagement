package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ProcessingActivityMongoRepository extends MongoBaseRepository<ProcessingActivity,BigInteger>,CustomProcessingActivityRepository {


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingActivity findByIdAndNonDeleted(Long unitId,BigInteger id);

    ProcessingActivity findByid(BigInteger id);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false}")
    List<ProcessingActivity> findProcessingActivityListByUnitIdAndIds(Long unitId, Set<BigInteger> processingActivityIds);

    @Query("{organizationId:?0,_id:{$in:?1},deleted:false,subProcess:false}")
    List<ProcessingActivity> findSubProcessingActivitiesByIds(Long unitId, List<BigInteger> ids);

    @Query("{_id:{$in:?1},deleted:false,subProcess:false}")
    List<ProcessingActivityResponseDTO> findAllSubProcessingActivitiesByIds(List<BigInteger> ids);

    @Query("{organizationId:?0,assetId:?1,deleted:false,subProcess:false}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkWithAssetById(Long unitId, BigInteger assetId);

    @Query("{organizationId:?0,deleted:false,active:true,responsibilityType:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkedWithResponsibilityType(Long unitId,BigInteger responsibilityTypeId);

    @Query("{organizationId:?0,deleted:false,active:true,accessorParties:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkedWithAccessorParty(Long unitId,BigInteger accessorPartyId);

    @Query("{organizationId:?0,deleted:false,active:true,transferMethods:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkedWithTransferMethod(Long unitId,BigInteger transferMethodId);

    @Query("{organizationId:?0,deleted:false,active:true,dataSources:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkedWithDataSource(Long unitId,BigInteger dataSourceId);

    @Query("{organizationId:?0,deleted:false,active:true,processingPurposes:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkedWithProcessingPurpose(Long unitId,BigInteger processingPurposeId);

    @Query("{organizationId:?0,deleted:false,active:true,processingLegalBasis:?1},{name:1,_id:0}")
    List<ProcessingActivityBasicResponseDTO> findAllProcessingActivityLinkedWithProcessingLegalBasis(Long unitId,BigInteger processingLegalBasisId);


}
