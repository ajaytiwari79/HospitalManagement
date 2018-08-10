package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomProcessingActivityRepository {

    ProcessingActivity findByName(Long countryid, Long organizationId, String name);

    List<ProcessingActivityResponseDTO>  getAllProcessingActivityWithMetaData(Long countryId, Long organizationId);

    ProcessingActivityResponseDTO  getProcessingActivityWithMetaDataById(Long countryId, Long organizationId, BigInteger id);


}
