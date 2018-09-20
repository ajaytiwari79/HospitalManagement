package com.kairos.persistence.repository.master_data.processing_activity_masterdata;

import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityRiskResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterProcessingActivity {


   MasterProcessingActivity findByName(Long countryId,String name);

   MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessingActivity(Long countryId, BigInteger id);

   List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessingActivity(Long countryId);

   List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityWithFilterSelection(Long countryId,FilterSelectionDTO filterSelectionDto);

   Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

   List<MasterProcessingActivity> getMasterProcessingActivityByOrgTypeSubTypeCategoryAndSubCategory(Long  countryId,  OrganizationMetaDataDTO organizationMetaDataDTO);

   List<MasterProcessingActivityRiskResponseDTO> getAllProcessingActivityWithLinkedRisksAndSubProcessingActivitiesByCountryId(Long countryId);


}
