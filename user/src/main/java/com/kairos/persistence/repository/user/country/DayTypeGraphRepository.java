package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.DayType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface DayTypeGraphRepository extends Neo4jBaseRepository<DayType,Long> {

    List<DayType> findAll();
    //@Query("MATCH (c:Country)-[:BELONGS_TO]-(dt:DayType {isEnabled:true}) where id(c)={0} return {id:id(dt), name:dt.name,validDays:dt.validDays, description:dt.description,code:dt.code, colorCode:dt.colorCode, allowTimeSettings:dt.allowTimeSettings } as result ")
    @Query("MATCH (c:Country)-[:"+ BELONGS_TO +"]-(dt:DayType {isEnabled:true}) where id(c)={0} return dt")
    List<DayType> findByCountryId(long countryId);

    @Query("MATCH (n:DayType{isEnabled:true}) WITH n.validDays AS coll,n UNWIND coll AS x with distinct x,n WHERE x  in {0} and n.holidayType=false return n")
    List<DayType>findByValidDaysContains( List<String> days);

    @Query("Match (n:DayType) where id(n) in {0} return n")
    List<DayType> getDayTypes(List<Long> dayTypeIds);

    @Query("Match (dayType:DayType{isEnabled:true}) where id(dayType) in {0} return dayType")
    List<DayType> getDayTypes(Set<Long> dayTypeIds);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(dayType:DayType {isEnabled:true}) WHERE id(country)={0} AND id(dayType)<>{3} AND (dayType.name =~{1} OR dayType.code={2}) " +
            " WITH count(dayType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean dayTypeExistInCountryByNameOrCode(Long countryId, String name, int code, Long currentDayTypeId);

    @Query("MATCH (organization) where id(organization)={0} with organization MATCH (organization)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region) with region MATCH (region)-[:"+BELONGS_TO+"]->(country:Country)-[:"+ BELONGS_TO +"]-(dt:DayType {isEnabled:true}) return dt")
    List<DayType> getDayTypeByOrganizationById(Long organizationId);
}
