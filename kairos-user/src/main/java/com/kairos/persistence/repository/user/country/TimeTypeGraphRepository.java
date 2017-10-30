package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.dto.TimeTypeDTO;
import com.kairos.persistence.model.user.country.TimeType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 18/10/17.
 */
@Repository
public interface TimeTypeGraphRepository extends GraphRepository <TimeType> {

    @Query("MATCH (timeType:TimeType{enabled:true})-[:"+BELONGS_TO+"]->(c:Country) where id(c)={0} return id(timeType) as Id,timeType.name as name ,timeType.type as type,timeType.negativeDayBalancePresent as negativeDayBalancePresent,timeType.onCallTime as onCallTime,timeType.includeInTimeBank as includeInTimeBank")
    List<TimeTypeDTO> findAllByCountryId(Long countryId);


    @Query("MATCH (timeType:TimeType{enabled:true}) where timeType.name =~ {0} AND timeType.type =~ {1} return timeType")
    TimeType findByNameAndTypeIgnoreCase(String name ,String type);
}
