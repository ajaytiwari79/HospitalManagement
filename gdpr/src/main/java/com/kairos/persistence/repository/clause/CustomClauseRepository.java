package com.kairos.persistence.repository.clause;

import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.response.dto.clause.ClauseBasicResponseDTO;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public interface CustomClauseRepository {


    Clause findByTitle(Long countryId,String title);

    List<ClauseResponseDTO> getClauseDataWithFilterSelection(Long countryId,FilterSelectionDTO filterSelectionDto);

    Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

    List<Clause> findClausesByTitle(Long countryId,List<String> clauseTitles);

    List<ClauseResponseDTO> findAllClauseWithTemplateType(Long countryId);

    List<ClauseBasicResponseDTO> getClausesByAgreementTemplateMetadata(Long countryId, OrganizationMetaDataDTO organizationMetaDataDTO);

    ClauseResponseDTO findClauseWithTemplateTypeById(Long countryId, BigInteger id);

}
