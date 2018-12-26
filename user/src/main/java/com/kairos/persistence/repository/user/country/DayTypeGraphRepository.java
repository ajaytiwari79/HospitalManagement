package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.DayType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface DayTypeGraphRepository extends Neo4jBaseRepository<DayType,Long> {

    List<DayType> findAll();
    //@Query("MATCH (c:Country)-[:BELONGS_TO]-(dt:DayType {isEnabled:true}) where id(c)={0} return {id:id(dt), name:dt.name,validDays:dt.validDays, description:dt.description,code:dt.code, colorCode:dt.colorCode, allowTimeSettings:dt.allowTimeSettings } as result ")
    @Query("MATCH (c:Country)-[:BELONGS_TO]-(dt:DayType {isEnabled:true}) where id(c)={0} return dt")
    List<DayType> findByCountryId(long countryId);

    @Query("MATCH (n:DayType{isEnabled:true}) WITH n.validDays AS coll,n UNWIND coll AS x with distinct x,n WHERE x  in {0} and n.holidayType=false return n")
    List<DayType>findByValidDaysContains( List<String> days);

    @Query("Match (n:DayType) where id(n) in {0} return n")
    List<DayType> getDayTypes(List<Long> dayTypeIds);

    @Query("Match (dayType:DayType{isEnabled:true}) where id(dayType) in {0} return dayType")
    List<DayType> getDayTypes(Set<Long> dayTypeIds);
}
