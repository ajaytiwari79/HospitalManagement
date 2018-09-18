package com.kairos.persistence.repository.clause;

import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.persistence.model.clause.Clause;
import com.kairos.enums.FilterType;
import com.kairos.response.dto.clause.ClauseResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public interface CustomClauseRepository {


    Clause findByTitle(Long countryId,Long organizationId,String title);

    List<ClauseResponseDTO> getClauseDataWithFilterSelection(Long countryId,Long organizationId,FilterSelectionDTO filterSelectionDto);

    Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

    List<Clause> findClausesByTitle(Long countryId,Long orgId,List<String> clauseTitles);

    List<ClauseResponseDTO> findAllClauseWithTemplateType(Long countryId,Long organizationId);

    ClauseResponseDTO findClauseWithTemplateTypeById(Long countryId, Long organizationId, BigInteger id);

}
