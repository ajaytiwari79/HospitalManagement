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



    @Query("MATCH(union:Organization{deleted:false})-[:"+HAS_LOCATION+"]-(location:Location{deleted:false}) WHERE id(union)={0} RETURN location")
    List<Location> findLocationsByUnion(Long unionId);

    @Query("MATCH(union:Organization)-[:"+HAS_LOCATION+"]-(location:Location{deleted:false}) WHERE LOWER(location.name)=LOWER({0}) and id(union)={1} RETURN COUNT(location)>0")
    boolean existsByName(String name,Long unionId);

    @Query("MATCH(union:Organization)-[:"+HAS_LOCATION+"]->(location:Location{deleted:false}) WHERE (id(location)={0} or LOWER(location.name)=LOWER({1})) and id(union)={2} WITH union,location " +
            "OPTIONAL MATCH(location)-[:"+LOCATION_HAS_ADDRESS+"]->(address:ContactAddress) WITH union,location,address" +
            " OPTIONAL MATCH(address)-[:"+ZIP_CODE+"]->(zipCode:ZipCode) WITH union,location,address,zipCode " +
            "OPTIONAL MATCH(address)-[:"+MUNICIPALITY+"]->(municipality:Municipality) OPTIONAL MATCH(zipCode)-[:"+MUNICIPALITY+"]-(linkedMunicipality:Municipality)  RETURN location,id(address)AS addressId," +
            "id(zipCode)AS zipCodeId,id(municipality) AS municipalityId, id(union) AS unionId,address,collect(linkedMunicipality) AS municipalities ")
    List<LocationQueryResult> findByIdOrNameAndDeletedFalse(Long locationId, String locationName,Long unionId);

    @Query("MATCH(location:Location{deleted:false}) WHERE id(location)={0} RETURN location")
    Location findByIdAndDeletedFalse(Long locationId);

    @Query("MATCH(location:Location{deleted:false}) WHERE id(location) IN {0} OPTIONAL MATCH(location)-[:"+LOCATION_HAS_ADDRESS+"]->(address:ContactAddress) OPTIONAL MATCH(address)-" +
            "[:"+ZIP_CODE+"]->(zipCode:ZipCode) OPTIONAL MATCH(address)-[:"+MUNICIPALITY+"]->(municipality:Municipality) OPTIONAL MATCH(zipCode)-["+MUNICIPALITY+"]->(linkedMunicipality:Municipality)" +
            " OPTIONAL MATCH(municipality)-[:"+PROVINCE+"]->(province:Province) OPTIONAL MATCH(province)-[:"+REGION+"]-(region:Region) RETURN id(location) AS locationId,address,zipCode,municipality," +
            "collect(linkedMunicipality) AS municipalities,province,region")
    List<LocationDataQueryResult> getLocationData(List<Long> locationIds);


}
