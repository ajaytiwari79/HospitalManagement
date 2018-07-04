package com.kairos.persistance.repository.master_data.processing_activity_masterdata;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivity findByName(Long countryId,Long organizationId,String name);

   MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId,Long organizationId, BigInteger id);

   List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId,Long organizationId);

   List<MasterProcessingActivity> getMasterProcessingActivityWithFilterSelection(Long countryId,Long organizationId,FilterSelectionDTO filterSelectionDto);

   Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query);



}
