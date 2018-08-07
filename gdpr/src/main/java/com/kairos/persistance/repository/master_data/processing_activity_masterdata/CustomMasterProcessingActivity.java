package com.kairos.persistance.repository.master_data.processing_activity_masterdata;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.dto.data_inventory.OrganizationMetaDataDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivity findByName(Long countryId,Long organizationId,String name);

   MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId,Long organizationId, BigInteger id);

   List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId,Long organizationId);

   List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityWithFilterSelection(Long countryId,Long organizationId,FilterSelectionDTO filterSelectionDto);

   Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

   List<MasterProcessingActivity> getMasterProcessingActivityByOrgTypeSubTypeCategoryAndSubCategory(Long  countryId, Long organizationId, OrganizationMetaDataDTO organizationMetaDataDTO);


}
