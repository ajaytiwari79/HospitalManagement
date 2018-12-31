package com.kairos.persistence.repository.clause;

import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationTypeAndSubTypeIdDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import com.kairos.response.dto.clause.UnitLevelClauseResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public interface CustomClauseRepository {


    Clause findByCountryIdAndTitleAndDescription(Long countryId, String title, String description);

    Clause findByUnitIdAndTitleAndDescription(Long unitId, String title, String description);

    List<ClauseResponseDTO> getClauseDataWithFilterSelection(Long countryId,FilterSelectionDTO filterSelectionDto);

    Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

    List<Clause> findClauseByReferenceIdAndTitles(Long referenceId,boolean isUnitId, List<String> clauseTitles);

    List<ClauseResponseDTO> findAllClauseByCountryId(Long countryId);

    List<UnitLevelClauseResponseDTO> findAllClauseByUnitId(Long unitId);

    List<ClauseBasicResponseDTO> findAllClauseByAgreementTemplateMetadataAndCountryId(Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO, BigInteger templateTypeId);

    ClauseResponseDTO findClauseWithTemplateTypeById(Long countryId, BigInteger id);

    List<Clause> getClauseByCountryIdAndOrgTypeSubTypeCategoryAndSubCategory(Long countryId, OrganizationTypeAndSubTypeIdDTO organizationMetaDataDTO);

}
