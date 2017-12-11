package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 28/12/16.
 */
@Repository
public interface ZipCodeGraphRepository extends Neo4jBaseRepository<ZipCode,Long>{


    @Query("Match (n:ZipCode{isEnable:true}) where n.zipCode={0} return n")
    ZipCode findByZipCode(int zipCode);

    List<ZipCode> findAll();


    @Query("MATCH (m:Municipality{isEnable:true})-[:MUNICIPALITY]-(zc:ZipCode) where id(zc)={0} return m")
    List<Municipality> findMunicipByZipCode(long zipCodeId);

    @Query("Match (zipCode:ZipCode{isEnable:true})-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region)-[:"+BELONGS_TO+"]->(country:Country) where id(country)={0} return DISTINCT { id:id(zipCode),name:zipCode.name ,zipCode:zipCode.zipCode} as result")
    List<Map<String,Object>> getAllZipCodeByCountryId(Long countryId);


    @Query("MATCH (c:Country)<-[:BELONGS_TO]-(r:Region)<-[:REGION]-(p:Province)<-[:PROVINCE]-(m:Municipality)<-[:MUNICIPALITY]-(z:ZipCode{isEnable:true})  where id(c)={0} return DISTINCT { value:id(z),name:z.name ,label:z.zipCode} as result")
    List<Map<String,Object>> getAllZipCodeByCountryIdAnotherFormat(Long countryId);




}
