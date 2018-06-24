package com.kairos.persistance.repository.clause;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.enums.FilterType;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface CustomClauseRepository {

    List<Clause> getClauseDataWithFilterSelection(Long countryId,Long organizationId,FilterSelectionDTO filterSelectionDto);

    Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query);

}
