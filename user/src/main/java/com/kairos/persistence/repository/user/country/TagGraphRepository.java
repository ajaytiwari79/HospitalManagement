package com.kairos.persistence.repository.user.country;

import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.country.tag.TagQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 10/11/17.
 */
@Repository
public interface TagGraphRepository extends Neo4jBaseRepository<Tag,Long> {

    List<Tag> findAll();

    // DONE
    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, tag.countryTag as countryTag, tag.masterDataType as masterDataType")
    List<TagQueryResult> getListOfCountryTags(Long countryId , boolean deleted, String searchTextRegex);

    // DONE
    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "return id(tag) as id, tag.name as name, tag.countryTag as countryTag, tag.masterDataType as masterDataType")
    List<TagQueryResult> getListOfCountryTagsByMasterDataType(Long countryId , boolean deleted, String searchTextRegex, String masterDataType);

    // DONE
    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag {countryTag:false})\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, tag.countryTag as countryTag, tag.masterDataType as masterDataType\n" +
            "UNION\n" +
            "MATCH (country:Country)<-[:" + COUNTRY + "]-(o:Organization) where id(o)={0} AND o.showCountryTags=true\n" +
            "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n" +
            "WHERE tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, tag.countryTag as countryTag, tag.masterDataType as masterDataType\n")
    List<TagQueryResult>  getListOfOrganizationTags(Long orgId , boolean deleted, String searchTextRegex);

    // DONE
    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag {countryTag:false})\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "return id(tag) as id, tag.name as name, tag.countryTag as countryTag, tag.masterDataType as masterDataType\n" +
            "UNION\n" +
            "MATCH (country:Country)<-[:" + COUNTRY + "]-(o:Organization) where id(o)={0} AND o.showCountryTags=true\n" +
            "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n" +
            "WHERE tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "return id(tag) as id, tag.name as name, tag.countryTag as countryTag, tag.masterDataType as masterDataType\n")
    List<TagQueryResult>  getListOfOrganizationTagsByMasterDataType(Long orgId , boolean deleted, String searchTextRegex, String masterDataType);

    // DONE
    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(tag)={1} AND id(country) = {0} AND tag.deleted={2} return tag")
    Tag getCountryTag(long countryId, long tagId, boolean isDeleted);

    // DONE
    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag {countryTag:false})\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND tag.deleted={2} return tag")
    Tag getOrganizationTag(long tagId, long orgId,  boolean isDeleted);

    // DONE
    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} \n" +
            "SET tag.name={2}, tag.lastModificationDate={3} return tag")
    Tag updateCountryTag(Long tagId, Long countryId, String name, long lastModificationDate);

    // DONE
    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(tag)={0} AND id(org) = {1} \n" +
            "SET tag.name={2}, tag.lastModificationDate={3} return tag")
    Tag updateOrganizationTag(Long tagId, Long orgId, String name, long lastModificationDate);

    // DONE
    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithDataType(Long tagId, Long countryId, String masterDataType, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN tag")
    Tag getCountryTagByIdAndDataType(Long tagId, Long countryId, String masterDataType, boolean isDeleted);


    // DONE
    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithDataType(Long tagId, Long orgId, String masterDataType, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN tag")
    Tag getOrganizationTagByIdAndDataType(Long tagId, Long orgId, String masterDataType, boolean isDeleted);

    // DONE
    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(country) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithSameNameAndDataType(String name, Long countryId, String masterDataType, boolean isDeleted);

    // DONE
    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag {countryTag:false})\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithSameNameAndDataType(String name, Long orgId, String masterDataType, boolean isDeleted);

    // DONE
    @Query( "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND tag.masterDataType ={1}\n" +
            "return tag\n")
    List<Tag> getCountryTagsById(List<Long> tagIds, String masterDataType, boolean deleted);

    // DONE
    @Query("MATCH (c:Country) WHERE id(c)={0} CREATE (c)-[:"+COUNTRY_HAS_TAG+"]->(tag:Tag{name:{1}, countryTag:true, masterDataType:{2}, deleted:false, creationDate:{3}, lastModificationDate:{3}}) return tag")
    Tag createCountryTag(Long countryId, String tagName, String masterDataType, Long date);

    // DONE
    @Query("MATCH (o:Organization) WHERE id(o)={0} CREATE (o)-[:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag{name:{1}, countryTag:false, masterDataType:{2}, deleted:false, creationDate:{3}, lastModificationDate:{3}}) return tag")
    Tag createOrganizationTag(Long countryId, String tagName, String masterDataType, Long date);

    @Query("MATCH (s:Skill)-[hasTag:"+HAS_TAG+"]-(tag:Tag) WHERE tag.countryTag=true AND id(s)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfSkillByIdAndDeleted(long skillId, String filterText, boolean deleted);

    @Query("MATCH (e:Expertise)-[hasTag:"+HAS_TAG+"]-(tag:Tag) WHERE tag.countryTag=true AND id(e)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfExpertiseByIdAndDeleted(long expertiseId, String filterText, boolean deleted);

 /*   @Query("MATCH (wta:WorkingTimeAgreement)-[hasTag:"+HAS_TAG+"]-(tag:Tag) WHERE tag.countryTag=true AND id(wta)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfWTAByIdAndDeleted(long wtaId, String filterText, boolean deleted);*/

    @Query("MATCH (r:RuleTemplateCategory)-[hasTag:"+HAS_TAG+"]-(tag:Tag) WHERE tag.countryTag=true AND id(r)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfRuleTemplateCategoryByIdAndDeleted(long ruleTmplCategoryId, String filterText, boolean deleted);



    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag {countryTag:true})\n"+
            "WHERE id(country)={0} AND tag.name = {1} AND tag.masterDataType ={2} AND tag.deleted= {3} return tag")
    Tag getCountryTagByNameAndType(long countryId, String name, String masterDataType, boolean deleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag {countryTag:false})\n"+
             "WHERE id(org)={0} AND tag.name = {1} AND tag.masterDataType ={2} AND tag.deleted= {3} return tag")
    Tag getOrganizationTagByName(long orgId, String name, String masterDataType, boolean deleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND tag.masterDataType ={1}\n" +
            "return tag")
    List<Tag> getCountryTagsByIdAndMasterDataType(List<Long> tagIds, String masterDataType, boolean deleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND tag.masterDataType ={1}\n" +
            "return tag")
    List<Tag> getOrganizationTagsByIdAndMasterDataType(List<Long> tagIds, String masterDataType, boolean deleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithSameNameAndDataType(String name, Long orgId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.masterDataType = {1} AND tag.deleted= {2} return tag")
        //collect({id:tag.id, masterDataType:r.masterDataType, name:tag.name}) as data
    List<Tag> getListOfCountryTagsByDataType(Long countryId, MasterDataTypeEnum masterDataType, boolean deleted);

    @Query("MATCH (tag:Tag) WHERE id(tag) IN {0} AND tag.deleted={1}")
    List<Tag> getTagsById(List<Long> tagIds, boolean deleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND tag.masterDataType ={1}\n" +
            "return tag\n" +
            "UNION\n" +
            "Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND tag.masterDataType ={1}\n" +
            "return tag\n")
    List<Tag> getTagsById(List<Long> tagIds, String masterDataType, boolean deleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(org) = {0} tag.deleted={1} return tag")
    Tag getListOFOrganizationTag(Long orgId, boolean isDeleted);


    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND tag.masterDataType={2}\n" +
            "return id(tag) as id, tag.name as name, tag.masterDataType as masterDataType")
    List<TagQueryResult> getListOfCountryTagsByMasterDataType(Long countryId , boolean deleted, String masterDataType);


    @Query("MATCH (s:Skill)-[hasTag:"+HAS_TAG+"]-(tag:Tag) WHERE  id(s)={0} AND tag.deleted = {1} return tag")
    List<Tag> getTagsOfSkillByDeleted(long skillId, boolean deleted);

    @Query( "Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(org)={0} AND id(tag) IN {1} AND tag.deleted= {3} AND tag.masterDataType ={2}\n" +
            "return tag\n")
    List<Tag> getOrganizationTagsById(Long orgId, List<Long> tagIds, String masterDataType, boolean deleted);



}


