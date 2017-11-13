package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.model.user.skill.Skill;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

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
            "ON MATCH SET tag.name={0}, r.masterDataType = {2},r.lastModificationDate={4} return tag")
    Tag addCountryTag(String name, Long countryId, MasterDataTypeEnum masterDataType, long creationDate, long lastModificationDate);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={1} AND id(country) = {0} AND r.masterDataType ={2} AND tag.deleted={3} return tag")
    Tag getCountryTag(long countryId, long tagId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country) = {0} AND tag.deleted={1} return tag")
    Tag getListOfCountryTag(long countryId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} \n" +
            "SET tag.name={2}, r.lastModificationDate={3} return tag")
    Tag updateCountryTag(Long tagId, Long countryId, String name, long lastModificationDate);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted=false with tag,org\n" +
            "Merge (org)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag)\n" +
            "ON CREATE SET tag.name={0}, r.masterDataType = {2}, r.creationDate={3},r.lastModificationDate={4}\n" +
            "ON MATCH SET tag.name={0}, r.masterDataType = {2},r.lastModificationDate={4} return tag")
    Tag addOrganizationTag(String name, Long orgId, MasterDataTypeEnum masterDataType, long creationDate, long lastModificationDate);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={1} AND id(org) = {0} AND r.masterDataType ={2} AND tag.deleted={3} return tag")
    Tag getOrganizationTag(Long tagId, Long orgId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(org) = {0} tag.deleted={1} return tag")
    Tag getListOFOrganizationTag(Long orgId, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(org) = {1} \n" +
            "SET SET tag.name={2}, r.lastModificationDate={3} return tag")
    Tag updateOrganizationTag(Long tagId, Long orgId, String name, long lastModificationDate);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(country) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithDataType(Long tagId, Long countryId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(country) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isCountryTagExistsWithSameNameAndDataType(String name, Long countryId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE tag.name={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithSameNameAndDataType(String name, Long orgId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (org:Organization)-[r:"+ORGANIZATION_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(tag)={0} AND id(org) = {1} AND r.masterDataType ={2} AND tag.deleted={3} \n" +
            "RETURN CASE WHEN count(tag)>0 THEN true ELSE false END")
    boolean isOrganizationTagExistsWithDataType(Long tagId, Long orgId, MasterDataTypeEnum masterDataType, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_TAG+"]->(tag:Tag)\n" +
            "WHERE id(country)={0}, r.masterDataType = {1}, tag.deleted= {2} return tag")
    List<Tag> getListOfCountryTagsByDataType(Long countryId, MasterDataTypeEnum masterDataType, boolean deleted);

}
