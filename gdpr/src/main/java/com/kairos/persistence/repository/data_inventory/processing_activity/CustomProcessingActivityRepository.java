package com.kairos.persistence.repository.data_inventory.processing_activity;

import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityRiskResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomProcessingActivityRepository {

    ProcessingActivity findByName( Long organizationId, String name);

    List<ProcessingActivityResponseDTO> getAllProcessingActivityAndMetaDataAndSubProcessingActivities(Long unitId);

    ProcessingActivityResponseDTO  getProcessingActivityAndMetaDataById( Long unitId,BigInteger processingActivityId);

    List<ProcessingActivityBasicResponseDTO>  getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(Long unitId, Set<BigInteger> processingActivityIds);

    List<ProcessingActivityBasicResponseDTO> getAllProcessingActivityBasicDetailWithSubProcessingActivities(Long unitId);

    List<DataSubjectMappingResponseDTO> getAllMappedDataSubjectWithDataCategoryAndDataElement(Long unitId,List<BigInteger> dataSubjectIds);

    List<AssetBasicResponseDTO> getAllAssetLinkedWithProcessingActivityById(Long unitId,BigInteger processingActivityId);

    List<ProcessingActivityRiskResponseDTO> getAllProcessingActivityAndSubProcessWithRisks(Long unitId);

}
