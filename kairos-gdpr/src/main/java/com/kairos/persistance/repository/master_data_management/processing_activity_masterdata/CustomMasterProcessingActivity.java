package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivityResponseDto getMasterProcessingActivityWithSubProcessingActivity(Long countryId,BigInteger id);

   List<MasterProcessingActivityResponseDto> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId);

   List<MasterProcessingActivity> getMasterProcessingActivityWithFilterSelection(Long countryId, FilterSelectionDto filterSelectionDto);

   Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query);



}
