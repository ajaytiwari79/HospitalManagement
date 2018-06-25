package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId, BigInteger id);

   List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId);

   List<MasterProcessingActivity> getMasterProcessingActivityWithFilterSelection(Long countryId, FilterSelectionDTO filterSelectionDto);

   Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query);



}
