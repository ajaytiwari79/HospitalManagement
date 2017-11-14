package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.model.user.country.tag.TagQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 10/11/17.
 */
@Repository
public interface TagGraphRepository extends GraphRepository<Tag> {

    List<Tag> findAll();

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(country) = {1} AND r.masterDataType ={2} AND tag.deleted=false with tag,country\n" +
            "Merge (country)-[r:"+COUNTRY_HAS_TAG+"]->(tag)\n" +
            "ON CREATE SET tag.name={0}, r.masterDataType = {2}, r.creationDate={3},r.lastModificationDate={4}\n" +
            "ON MATCH SET tag.name={0}, r.masterDataType = {2},r.lastModificationDate={4}\n" +
            "return {id:id(tag), name:tag.name, masterDataType:r.masterDataType}")
    Tag addCountryTag(String name, Long countryId, String masterDataType, long creationDate, long lastModificationDate);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType, true as countryTag")
    List<TagQueryResult> getListOfCountryTags(Long countryId , boolean deleted, String searchTextRegex);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND r.masterDataType ={3}\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType, true as countryTag")
    List<TagQueryResult> getListOfCountryTagsByMasterDataType(Long countryId , boolean deleted, String searchTextRegex, String masterDataType);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND e.masterDataType={2}\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType")
    List<TagQueryResult> getListOfCountryTagsByMasterDataType(Long countryId , boolean deleted, String masterDataType);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType, false as countryTag\n" +
            "UNION\n" +
            "MATCH (country:Country)<-[:" + COUNTRY + "]-(o:Organization) where id(o)={0} AND o.showCountryTags=true\n" +
            "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType, true as countryTag\n")
    List<TagQueryResult>  getListOfOrganizationTags(Long orgId , boolean deleted, String searchTextRegex);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND r.masterDataType ={3}\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType, false as countryTag\n" +
            "UNION\n" +
            "MATCH (country:Country)<-[:" + COUNTRY + "]-(o:Organization) where id(o)={0} AND o.showCountryTags=true\n" +
            "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND r.masterDataType ={3}\n" +
            "return id(tag) as id, tag.name as name, r.masterDataType as masterDataType, true as countryTag\n")
    List<TagQueryResult>  getListOfOrganizationTagsByMasterDataType(Long orgId , boolean deleted, String searchTextRegex, String masterDataType);


    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={1} AND id(country) = {0} AND tag.deleted={2} return tag")
    Tag getCountryTag(long countryId, long tagId, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={1} AND id(org) = {0} AND tag.deleted={2} return tag}")
    Tag getOrganizationTag(long countryId, long tagId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} \n" +
            "SET tag.name={2}, r.lastModificationDate={3} return {id:id(tag), name:tag.name, masterDataType:r.masterDataType}")
    HashMap<String,Object> updateCountryTag(Long tagId, Long countryId, String name, long lastModificationDate);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(org) = {1} \n" +
            "SET tag.name={2}, r.lastModificationDate={3} return {id:id(tag), name:tag.name, masterDataType:r.masterDataType}")
    HashMap<String,Object> updateOrganizationTag(Long tagId, Long orgId, String name, long lastModificationDate);


    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted=false with tag,org\n" +
            "Merge (org)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag)\n" +
            "ON CREATE SET tag.name={0}, r.masterDataType = {2}, r.creationDate={3},r.lastModificationDate={4}\n" +
            "ON MATCH SET tag.name={0}, r.masterDataType = {2},r.lastModificationDate={4} return tag")
    Tag addOrganizationTag(String name, Long orgId, MasterDataTypeEnum masterDataType, long creationDate, long lastModificationDate);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(org) = {0} tag.deleted={1} return tag")
    Tag getListOFOrganizationTag(Long orgId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithDataType(Long tagId, Long countryId, String masterDataType, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithDataType(Long tagId, Long orgId, String masterDataType, boolean isDeleted);


    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(country) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithSameNameAndDataType(String name, Long countryId, String masterDataType, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithSameNameAndDataType(String name, Long orgId, String masterDataType, boolean isDeleted);


    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithSameNameAndDataType(String name, Long orgId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND r.masterDataType = {1} AND tag.deleted= {2} return tag")
    //collect({id:tag.id, masterDataType:r.masterDataType, name:tag.name}) as data
    List<Tag> getListOfCountryTagsByDataType(Long countryId, MasterDataTypeEnum masterDataType, boolean deleted);

    @Query("MATCH (tag:Tag) WHERE id(tag) IN {0} AND tag.deleted={1}")
    List<Tag> getTagsById(List<Long> tagIds, boolean deleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND r.masterDataType ={1}\n" +
            "return id(tag) as id\n" +
            "UNION\n" +
            "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND r.masterDataType ={1}\n" +
            "return id(tag) as id\n")
    List<Tag> getTagsById(List<Long> tagIds, String masterDataType, boolean deleted);

}


