package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.Region;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 12/12/16.
 */
@Repository
public interface RegionGraphRepository extends Neo4jBaseRepository<Region,Long> {

    @Query("Match (region:Region{isEnable:true}) return region")
    List<Region> findAll();

    @Query("MATCH (r:Region{isEnable:true})-[:BELONGS_TO]->(c:Country) where id(c)={0} return {name:r.name, code:r.code, geoFence:r.geoFence, latitude:r.latitude, longitude:r.longitude ,id: id(r)} as result")
    List<Map<String,Object>> findAllRegionsByCountryId(Long countryId);

    @Query("MATCH (n:Region)  WITH n as r  " +
            "OPTIONAL MATCH (r)-[:MUNICIPALITY_LIST]->(m:Municipality) return { " +
            "id:id(r), " +
            "name:r.name, " +
            "  municipalityList:collect({ " +
            "  id:id(m), " +
            "  name:m.name " +
            "  }) " +
            "} as result ")
    List<Map<String,Object>> getAllRegionWithMunicipalities();

    Region findByCode(String code);

    @Query("MATCH (r:Region {isEnable:true})-[:MUNICIPALITY_LIST]-(m:Municipality) where id(m) = {0} return r")
    List<Region> findRegionByMunicipalityId(long municipalityId);

    @Query("MATCH (zipcode:ZipCode)-[:"+MUNICIPALITY+"]->(municipality:Municipality) where id(zipcode)={0}\n" +
            "Match (municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region)-[:BELONGS_TO]->(country:Country) return {id:id(municipality),name:municipality.name,province:{name:province.name,id:id(province),region:{id:id(region),name:region.name,country:{id:id(country),name:country.name}}}} as result")
    List<Map<String,Object>> getGeographicTreeData(long zipCodeId);

    @Query("Match (municipality:Municipality) where id(municipality)={0}\n" +
            "Match (municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region)-[:"+BELONGS_TO+"]->(country:Country) return \n" +
            "{provinceName:province.name,provinceId:id(province),regionId:id(region),regionName:region.name,countryId:id(country),countryName:country.name} as data")
    Map<String,Object> getGeographicData(long municipalityId);


}
