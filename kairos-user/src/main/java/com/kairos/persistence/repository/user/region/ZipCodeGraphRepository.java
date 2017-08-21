package com.kairos.persistence.repository.user.region;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.model.user.region.ZipCode;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 28/12/16.
 */
@Repository
public interface ZipCodeGraphRepository extends GraphRepository<ZipCode>{

    ZipCode findByZipCode(int zipCode);

    List<ZipCode> findAll();


    @Query("MATCH (m:Municipality{isEnable:true})-[:MUNICIPALITY]-(zc:ZipCode) where id(zc)={0} return m")
    List<Municipality> findMunicipByZipCode(long zipCodeId);

    @Query("Match (zipCode:ZipCode{isEnable:true})-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region)-[:"+BELONGS_TO+"]->(country:Country) where id(country)={0} return DISTINCT { id:id(zipCode),name:zipCode.name ,zipCode:zipCode.zipCode} as result")
    List<Map<String,Object>> getAllZipCodeByCountryId(Long countryId);


    @Query("MATCH (c:Country)<-[*4]-(z:ZipCode{isEnable:true}) where id(c)={0} return DISTINCT { value:id(z),name:z.name ,label:z.zipCode} as result")
    List<Map<String,Object>> getAllZipCodeByCountryIdAnotherFormat(Long countryId);




}
