package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.timetype.TimeTypeDTO;
import com.kairos.persistence.model.user.country.TimeType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 18/10/17.
 */

//It is used by CostTimeAgreementService prerna working on it, but it should be removed
@Repository
public interface TimeTypeGraphRepository extends Neo4jBaseRepository <TimeType,Long>{
    @Query("MATCH (timeType:TimeType{deleted:false})-[:"+BELONGS_TO+"]->(c:Country) where id(c)={0} return id(timeType) as id,timeType.name as name ,timeType.type as type,timeType.negativeDayBalancePresent as negativeDayBalancePresent,timeType.onCallTime as onCallTime,timeType.includeInTimeBank as includeInTimeBank")
    List<TimeTypeDTO> findAllByCountryId(Long countryId);


    @Query("MATCH (timeType:TimeType{deleted:false}) where timeType.name =~ {0} AND timeType.type =~ {1} return timeType")
    TimeType findByNameAndTypeIgnoreCase(String name ,String type);

    @Query("MATCH (timeType:TimeType{deleted:false}) where timeType.name =~ {0} AND timeType.type =~ {1} AND id(timeType)<> {2} return count(timeType)")
    int findByNameAndTypeAndIdIgnoreCase(String name ,String type,long Id);

    @Query("MATCH (timeType:TimeType{deleted:false}) WHERE id(timeType) IN {0} return timeType")
    List<TimeType> findTimeTypeByIds(List<Long> timeTypeIds);
}
