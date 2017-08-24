package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.user.region.Municipality;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.MUNICIPALITY;


/**
 * Created by oodles on 22/12/16.
 */
@Repository
public interface MunicipalityGraphRepository extends GraphRepository<Municipality>{

    List<Municipality> findAll();

    @Query("MATCH (m:Municipality {isEnable:true})-[:PROVINCE]-(p:Province) where id(p) = {0}  return {name:m.name, code:m.code, geoFence:m.geoFence, latitude:m.latitude, longitude:m.longitude,id: id(m)} as result ")
    List<Map<String,Object>>  getAllMunicipalitiesOfProvince(Long provinceId);

    @Query("MATCH (m:Municipality {isEnable:true})-[:MUNICIPALITY]-(zc:ZipCode  {isEnable:true}) where id(m) = {0} return {name:zc.name, zipCode:zc.zipCode, geoFence:zc.geoFence,id: id(zc)} as result ")
    List<Map<String,Object>> getAllZipCodes(Long municipalityId);

    /**
     * if zipcode has connected to multiple municipality, this will return one municipality randomly with no
     * promise of order maintain,results can be different  for same zip code
     * @param zipCodeId
     * @return
     */
    @Query("Match (zipCode:ZipCode)-[:"+MUNICIPALITY+"]->(municipality:Municipality) where id(zipCode)={0} return municipality limit 1")
    Municipality getMunicipalityByZipCodeId(long zipCodeId);

    @Query("Match (zipCode:ZipCode)-[:"+MUNICIPALITY+"]->(municipality:Municipality) where id(zipCode)={0} return municipality")
    List<Municipality> getMunicipalitiesByZipCode(long zipCode);

    Municipality findByCode(String code);

    @Query("Match (zipCode:ZipCode)-[:"+MUNICIPALITY+"]->(municipality:Municipality) where zipCode.zipCode={0} return municipality limit 1")
    Municipality getMunicipalityByZipCodeId(int zipCodeId);

    @Query("Match (zipCode:ZipCode{zipCode:{0}})-[:"+MUNICIPALITY+"]->(municipality:Municipality) return municipality")
    List<Municipality> getMuncipalityByZipcode(int zipcode);


}
