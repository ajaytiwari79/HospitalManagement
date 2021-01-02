package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.address.MunicipalityQueryResult;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.MunicipalityQueryResults;
import com.kairos.persistence.model.user.region.ZipCodeQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 22/12/16.
 */
@Repository
public interface MunicipalityGraphRepository extends Neo4jBaseRepository<Municipality, Long> {


    List<Municipality> findAll();

    @Query("MATCH (m:Municipality {isEnable:true})-[:PROVINCE]-(p:Province) WHERE id(p) = {0}  RETURN m.translations as translations,\n" +
            "m.name as name, m.code as code, m.geoFence as geoFence, m.latitude as latitude, m.longitude as longitude,id(m) as id")
    List<MunicipalityQueryResults> getAllMunicipalitiesOfProvince(Long provinceId);

    @Query("MATCH (m:Municipality {isEnable:true})-[:MUNICIPALITY]-(zc:ZipCode  {isEnable:true}) WHERE id(m) = {0} RETURN zc.translations as translations,\n" +
            "zc.name as name, zc.code as code, zc.geoFence as geoFence, zc.latitude as latitude, zc.longitude as longitude,id(zc) as id")
    List<ZipCodeQueryResult> getAllZipCodes(Long municipalityId);

    /**
     * if zipcode has connected to multiple municipality, this will RETURN one municipality randomly WITH no
     * promise of order maintain,results can be different  for same zip code
     *
     * @param zipCodeId
     * @RETURN
     */
    @Query("MATCH (zipCode:ZipCode)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WHERE id(zipCode)={0} RETURN municipality limit 1")
    Municipality getMunicipalityByZipCodeId(long zipCodeId);

    @Query("MATCH (zipCode:ZipCode)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WHERE id(zipCode)={0} RETURN municipality")
    List<Municipality> getMunicipalitiesByZipCode(long zipCode);

    Municipality findByCode(String code);

    @Query("MATCH (zipCode:ZipCode)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WHERE zipCode.zipCode={0} RETURN municipality limit 1")
    Municipality getMunicipalityByZipCodeId(int zipCodeId);

    @Query("MATCH (zipCode:ZipCode{zipCode:{0}})-[:" + MUNICIPALITY + "]->(municipality:Municipality) RETURN municipality")
    List<Municipality> getMuncipalityByZipcode(int zipcode);

    @Query("MATCH(municipality:Municipality{isEnable:true})-[:PROVINCE]->(p:Province)-[:REGION]->(r:Region)-[:BELONGS_TO]->(country:Country) WHERE id(country)={0} \n" +
            "RETURN municipality")
    List<Municipality> getMunicipalityByCountryId(Long countryId);

    @Query("MATCH(municipality:Municipality)-[:"+PROVINCE+"]-(province:Province)-[:"+REGION+"]-(region:Region) WHERE id(municipality) in {0}  " +
            "RETURN municipality,region,province")
    List<MunicipalityQueryResult> findMunicipalityRegionAndProvince(Set<Long> municipalityIds);


    @Query("MATCH(zipCode:ZipCode)-[:"+MUNICIPALITY+"]-(municipality:Municipality{deleted:false}) WHERE id(municipality)={0} and id(zipCode)={1} RETURN municipality")
    Municipality findByZipCodeIdandIdDeletedFalse(Long municipalityId,Long zipCodeId);

    @Query("MATCH(address:ContactAddress)-[municipalityRel:"+MUNICIPALITY+"]-(municipality:Municipality) WHERE id(address)={0} and id(municipality)={1} DELETE municipalityRel")
    void deleteAddressMunicipalityRelation(Long addressId, Long municipalityId);
}
