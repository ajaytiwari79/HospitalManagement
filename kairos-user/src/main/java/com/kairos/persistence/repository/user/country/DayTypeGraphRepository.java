package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.Day;
import com.kairos.persistence.model.user.country.DayType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface DayTypeGraphRepository extends GraphRepository<DayType> {

    List<DayType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(dt:DayType {isEnabled:true}) where id(c)={0} return {id:id(dt), name:dt.name, description:dt.description,code:dt.code, colorCode:dt.colorCode } as result ")
    List<Map<String,Object>> findByCountryId(long countryId);
    List<DayType>findByValidDays(Day day);
}
