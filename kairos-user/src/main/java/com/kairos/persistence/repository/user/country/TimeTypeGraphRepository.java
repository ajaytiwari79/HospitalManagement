package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.dto.TimeTypeDTO;
import com.kairos.persistence.model.user.country.TimeType;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by vipul on 18/10/17.
 */
@Repository
public interface TimeTypeGraphRepository extends GraphRepository <TimeType> {

    List<TimeTypeDTO> findAllByCountryIdAndEnabled(Long countryId,boolean enabled);
}
