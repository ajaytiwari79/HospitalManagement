package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.country.tag.TagQueryResult;
import com.kairos.persistence.model.staff.StaffKpiFilterQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 10/11/17.
 */
@Repository
public interface TagGraphRepository extends Neo4jBaseRepository<Tag, Long> {

    List<Tag> findAll();

    @Query("Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(country)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return " +
            "{english: CASE WHEN tag.`translatedNames.english` IS NULL THEN '' ELSE tag.`translatedNames.english` END,danish: CASE WHEN tag.`translatedNames.danish` IS NULL THEN '' ELSE tag.`translatedNames.danish` END,hindi: CASE WHEN tag.`translatedNames.hindi` IS NULL THEN '' ELSE tag.`translatedNames.hindi` END,britishenglish: CASE WHEN tag.`translatedNames.britishenglish` IS NULL THEN '' ELSE tag.`translatedNames.britishenglish` END} as translatedNames,\n" +
            "{english: CASE WHEN tag.`translatedDescriptions.english` IS NULL THEN '' ELSE tag.`translatedDescriptions.english` END,danish: CASE WHEN tag.`translatedDescriptions.danish` IS NULL THEN '' ELSE tag.`translatedDescriptions.danish` END,hindi: CASE WHEN tag.`translatedDescriptions.hindi` IS NULL THEN '' ELSE tag.`translatedDescriptions.hindi` END,britishenglish: CASE WHEN tag.`translatedDescriptions.britishenglish` IS NULL THEN '' ELSE tag.`translatedDescriptions.britishenglish` END}as translatedDescriptions,\n" +
            "id(tag) as id, tag.name as name, tag.color as color , tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType,tag.orgTypeId as orgTypeId,tag.orgSubTypeIds as orgSubTypeIds")
    List<TagQueryResult> getListOfCountryTags(Long countryId, boolean deleted, String searchTextRegex);

    @Query("Match (country:Country)-[r:COUNTRY_HAS_TAG]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(country)={0} AND tag.deleted = {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "return \n" +
            "{english: CASE WHEN tag.`translatedNames.english` IS NULL THEN '' ELSE tag.`translatedNames.english` END,danish: CASE WHEN tag.`translatedNames.danish` IS NULL THEN '' ELSE tag.`translatedNames.danish` END,hindi: CASE WHEN tag.`translatedNames.hindi` IS NULL THEN '' ELSE tag.`translatedNames.hindi` END,britishenglish: CASE WHEN tag.`translatedNames.britishenglish` IS NULL THEN '' ELSE tag.`translatedNames.britishenglish` END} as translatedNames,\n" +
            "{english: CASE WHEN tag.`translatedDescriptions.english` IS NULL THEN '' ELSE tag.`translatedDescriptions.english` END,danish: CASE WHEN tag.`translatedDescriptions.danish` IS NULL THEN '' ELSE tag.`translatedDescriptions.danish` END,hindi: CASE WHEN tag.`translatedDescriptions.hindi` IS NULL THEN '' ELSE tag.`translatedDescriptions.hindi` END,britishenglish: CASE WHEN tag.`translatedDescriptions.britishenglish` IS NULL THEN '' ELSE tag.`translatedDescriptions.britishenglish` END}as translatedDescriptions,\n" +
            "id(tag) as id, tag.name as name, tag.color as color,tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType,tag.orgTypeId as orgTypeId,tag.orgSubTypeIds as orgSubTypeIds")
    List<TagQueryResult> getListOfCountryTagsByMasterDataType(Long countryId, boolean deleted, String searchTextRegex, String masterDataType);

    @Query("Match (org)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag {countryTag:false})\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, tag.color as color,tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType\n" +
            "UNION\n" +
            "MATCH (country:Country)<-[:" + BELONGS_TO + "]-(o) where id(o)={0} AND o.showCountryTags=true\n" +
            "Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag {countryTag:true})\n" +
            "WHERE tag.deleted= {1} AND lower(tag.name) contains lower({2})\n" +
            "return id(tag) as id, tag.name as name, tag.color as color,tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType\n")
    List<TagQueryResult> getListOfOrganizationTags(Long orgId, boolean deleted, String searchTextRegex);

    @Query("Match (org)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "OPTIONAL MATCH (tag)-[:"+HAS_PENALTY_SCORE+"]->(penaltyScore:PenaltyScore)\n" +
            "return id(tag) as id, tag.name as name, tag.color as color,tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType, penaltyScore\n" +
            "UNION\n" +
            "MATCH (country:Country)<-[:" + BELONGS_TO + "]-(o) where id(o)={0} AND o.showCountryTags=true\n" +
            "Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag{countryTag:true})\n" +
            "WHERE tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "OPTIONAL MATCH (tag)-[:"+HAS_PENALTY_SCORE+"]->(penaltyScore:PenaltyScore)\n" +
            "return id(tag) as id, tag.name as name, tag.color as color,tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType, penaltyScore\n")
    List<TagQueryResult> getListOfOrganizationTagsByMasterDataType(Long orgId, boolean deleted, String searchTextRegex, String masterDataType);

    @Query("Match (org)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag {countryTag:false})\n" +
            "WHERE id(org)={0} AND tag.deleted= {1} AND lower(tag.name) contains lower({2}) AND tag.masterDataType ={3}\n" +
            "OPTIONAL MATCH (tag)-[:"+HAS_PENALTY_SCORE+"]->(penaltyScore:PenaltyScore)\n" +
            "return id(tag) as id, tag.name as name, tag.color as color,tag.shortName as shortName,tag.ultraShortName as ultraShortName, tag.countryTag as countryTag, tag.masterDataType as masterDataType, penaltyScore\n")
    List<TagQueryResult> getListOfStaffOrganizationTags(Long orgId, boolean deleted, String searchTextRegex, String masterDataType);

    @Query("Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(tag)={1} AND id(country) = {0} AND tag.deleted={2} return tag")
    Tag getCountryTag(long countryId, long tagId, boolean isDeleted);

    @Query("Match (org:OrganizationBaseEntity)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag {countryTag:false})\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND tag.deleted={2} return tag")
    Tag getOrganizationTag(long tagId, long orgId, boolean isDeleted);


    @Query("Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN tag")
    Tag getCountryTagByIdAndDataType(Long tagId, Long countryId, String masterDataType, boolean isDeleted);

    
    @Query("Match (org:OrganizationBaseEntity)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "OPTIONAL MATCH (tag)-[rel:" + HAS_PENALTY_SCORE + "]->(penaltyScore:PenaltyScore) \n" +
            "RETURN tag,rel,penaltyScore")
    Tag getOrganizationTagByIdAndDataType(Long tagId, Long orgId, String masterDataType, boolean isDeleted);

    
    @Query("Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag)\n" +
            "WHERE tag.name=~{0} AND id(country) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithSameNameAndDataType(String name, Long countryId, String masterDataType, boolean isDeleted);

    
    @Query("Match (org:OrganizationBaseEntity)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag {countryTag:false})\n" +
            "WHERE tag.name=~{0} AND id(org) = {1} AND tag.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithSameNameAndDataType(String name, Long orgId, String masterDataType, boolean isDeleted);

    
    @Query("Match (country:Country)-[r:" + COUNTRY_HAS_TAG + "]->(tag:Tag {countryTag:true})\n" +
            "WHERE id(tag) IN {0} AND tag.deleted= {2} AND tag.masterDataType ={1}\n" +
            "return tag\n")
    List<Tag> getCountryTagsById(List<Long> tagIds, String masterDataType, boolean deleted);

    @Query("MATCH (s:Skill)-[hasTag:" + HAS_TAG + "]-(tag:Tag) WHERE tag.countryTag=true AND id(s)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfSkillByIdAndDeleted(long skillId, String filterText, boolean deleted);

    @Query("MATCH (e:Expertise)-[hasTag:" + HAS_TAG + "]-(tag:Tag) WHERE tag.countryTag=true AND id(e)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfExpertiseByIdAndDeleted(long expertiseId, String filterText, boolean deleted);

    @Query("MATCH (r:RuleTemplateCategory)-[hasTag:" + HAS_TAG + "]-(tag:Tag) WHERE tag.countryTag=true AND id(r)={0}  AND lower(tag.name) contains lower({1}) AND tag.deleted = {2} return tag")
    List<Tag> getCountryTagsOfRuleTemplateCategoryByIdAndDeleted(long ruleTmplCategoryId, String filterText, boolean deleted);

    @Query("MATCH (s:Skill)-[hasTag:" + HAS_TAG + "]-(tag:Tag) WHERE  id(s)={0} AND tag.deleted = {1} return tag")
    List<Tag> getTagsOfSkillByDeleted(long skillId, boolean deleted);

    @Query("Match (org:OrganizationBaseEntity)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag{countryTag:false})\n" +
            "WHERE id(org)={0} AND id(tag) IN {1} AND tag.deleted= {3} AND tag.masterDataType ={2}\n" +
            "return tag\n")
    List<Tag> getOrganizationTagsById(Long orgId, List<Long> tagIds, String masterDataType, boolean deleted);

    @Query("Match (org:OrganizationBaseEntity)-[r:" + ORGANIZATION_HAS_TAG + "]->(tag:Tag{countryTag:false,deleted:false})\n" +
            "WHERE id(org)={0} AND tag.masterDataType ={1}\n" +
            "return tag\n")
    List<Tag> getOrganizationTags(Long orgId, String masterDataType);

    @Query("Match (staff)-[:BELONGS_TO_TAGS]-(tag:Tag) WHERE id(staff) IN {0} return id(staff) as id,COLLECT( distinct {id:id(tag),name:tag.name,endDate:tag.endDate,startDate:tag.startDate}) as tags")
    List<StaffKpiFilterQueryResult> getStaffsTagsByStaffIds(List<Long> staffIds);
}


