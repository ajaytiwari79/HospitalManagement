package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivityResponseDto getMasterProcessingActivityWithSubProcessingActivity(Long countryId,BigInteger id);

   List<MasterProcessingActivityResponseDto> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId);


}
