package com.kairos.persistence.repository.clause_tag;


import com.kairos.persistence.model.clause_tag.ClauseTag;

import java.util.List;
import java.util.Set;

public interface CustomClauseTagRepository {


    List<ClauseTag> findByCountryIdAndTitles(Long countryId, Set<String> titles);

    List<ClauseTag> findByUnitIdAndTitles(Long unitId, Set<String> titles);


}
