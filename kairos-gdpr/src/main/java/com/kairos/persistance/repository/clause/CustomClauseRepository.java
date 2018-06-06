package com.kairos.persistance.repository.clause;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.enums.FilterType;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface CustomClauseRepository {

    List<Clause> getClauseDataWithFilterSelection(Long countryId, FilterSelectionDto filterSelectionDto);

    Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query);

}
