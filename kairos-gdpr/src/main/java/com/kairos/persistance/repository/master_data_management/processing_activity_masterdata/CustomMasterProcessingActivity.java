package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;

import java.math.BigInteger;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivityResponseDto getMasterProcessingActivityWithData(Long countryId,BigInteger id);


}
