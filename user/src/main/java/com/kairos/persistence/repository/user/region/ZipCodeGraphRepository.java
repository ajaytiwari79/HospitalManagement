package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.address.ZipCodeSectorQueryResult;
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


    @Query("MATCH (n:ZipCode{isEnable:true}) WHERE n.zipCode={0} RETURN n")
    ZipCode findByZipCode(int zipCode);

    List<ZipCode> findAll();


    @Query("MATCH (m:Municipality{isEnable:true})-[:MUNICIPALITY]-(zc:ZipCode) WHERE id(zc)={0} RETURN m")
    List<Municipality> findMunicipByZipCode(long zipCodeId);

    @Query("MATCH (zipCode:ZipCode{isEnable:true})-[:"+MUNICIPALITY+"]->(municipality:Municipality)-[:"+PROVINCE+"]->(province:Province)-[:"+REGION+"]->(region:Region)-[:"+BELONGS_TO+"]->(country:Country) WHERE id(country)={0} RETURN DISTINCT { id:id(zipCode),name:zipCode.name ,zipCode:zipCode.zipCode} as result")
    List<Map<String,Object>> getAllZipCodeByCountryId(Long countryId);


    @Query("MATCH (c:Country)<-[:BELONGS_TO]-(r:Region)<-[:REGION]-(p:Province)<-[:PROVINCE]-(m:Municipality)<-[:MUNICIPALITY]-(z:ZipCode{isEnable:true})  WHERE id(c)={0} RETURN DISTINCT { value:id(z),name:z.name ,label:z.zipCode} as result")
    List<Map<String,Object>> getAllZipCodeByCountryIdAnotherFormat(Long countryId);



    @Query("MATCH(zipCode:ZipCode{deleted:false}) WHERE id(zipCode)={0} RETURN zipCode")
    ZipCode findByIdDeletedFalse(Long id);


    @Query("MATCH(address:ContactAddress)-[zipCodeRel:"+ZIP_CODE+"]-(zipCode:ZipCode) WHERE id(address)={0} and id(zipCode)={1} delete zipCodeRel")
    void deleteAddressZipcodeRelation(Long addressId, Long zipcodeId);

    @Query("MATCH(address:ContactAddress)-[municipalityRel:"+MUNICIPALITY+"]-(municipality:Municipality) WHERE id(address)={0} and id(municipality)={1} delete municipalityRel")
    void deleteAddressMunicipalityRelation(Long addressId, Long municiaplityId);

    @Query("MATCH(zipCode:ZipCode{deleted:false}) WITH collect(zipCode) as zipCodes MATCH(sector:Sector)-[:"+IN_COUNTRY+"]-(country:Country) WHERE id(country)={0} RETURN collect(sector)as sectors," +
            " zipCodes" )
    ZipCodeSectorQueryResult getZipCodesAndSectors(Long countryId);











}
