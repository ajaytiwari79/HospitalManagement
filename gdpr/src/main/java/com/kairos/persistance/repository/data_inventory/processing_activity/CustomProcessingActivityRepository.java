package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponsDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomProcessingActivityRepository {

    ProcessingActivity findByName( Long organizationId, String name);

    List<ProcessingActivityResponseDTO>  getAllProcessingActivityAndMetaData( Long unitId);

    ProcessingActivityResponseDTO  getAllSubProcessingActivitiesOfProcessingActivity( Long unidId, BigInteger processingActivityId);

    List<ProcessingActivityBasicResponsDTO>  getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(Long unitId, Set<BigInteger> processingActivityIds);

    List<ProcessingActivityBasicResponsDTO> getAllProcessingActivityBasicDetailWithSubprocessingActivities(Long unitId);



}
