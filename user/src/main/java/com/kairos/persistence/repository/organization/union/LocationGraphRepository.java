package com.kairos.persistence.repository.organization.union;

import java.util.List;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.union.Location;
import com.kairos.persistence.model.organization.union.LocationDataQueryResult;
import com.kairos.persistence.model.organization.union.LocationQueryResult;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface LocationGraphRepository extends Neo4jBaseRepository<Location, Long> {



    @Query("match(union:Organization{deleted:false})-[:"+HAS_LOCATION+"]-(location:Location{deleted:false}) where id(union)={0} return location")
    List<Location> findLocationsByUnion(Long unionId);

    @Query("Match(location:Location{deleted:false}) where location.name={0} return count(location)>0")
    boolean existsByName(String name);

    @Query("Match(union:Organization)-[:"+HAS_LOCATION+"]->(location:Location{deleted:false})-[:"+LOCATION_HAS_ADDRESS+"]->(address:ContactAddress)-[:"+ZIP_CODE+"]->(zipCode:ZipCode) where id(location)={0} or " +
            "location.name={1} Match(address)-[:"+MUNICIPALITY+"]->(municipality:Municipality)  return location,id(address)as addressId," +
            "id(zipCode)as zipCodeId,id(municipality) as municipalityId, id(union) as unionId,address")
    List<LocationQueryResult> findByIdOrNameAndDeletedFalse(Long locationId, String locationName);

    @Query("Match(location:Location{deleted:false}) where id(location)={0} return location")
    Location findByIdAndDeletedFalse(Long locationId);

    @Query("Match(location:Location{deleted:false}) where id(location) in {0} optional match(location)-[:"+LOCATION_HAS_ADDRESS+"]->(address:ContactAddress) optional match(address)-" +
            "[:"+ZIP_CODE+"]->(zipCode:ZipCode) optional match(address)-[:"+MUNICIPALITY+"]->(municipality:Municipality) optional match(zipCode)-["+MUNICIPALITY+"]->(linkedMunicipality:Municipality)" +
            " optional match(municipality)-[:"+PROVINCE+"]->(province:Province) optional match(province)-[:"+REGION+"]-(region:Region) return id(location) as locationId,address,zipCode,municipality," +
            "collect(linkedMunicipality) as municipalities,province,region")
    List<LocationDataQueryResult> getLocationData(List<Long> locationIds);


}
