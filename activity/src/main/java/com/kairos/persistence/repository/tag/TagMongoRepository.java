package com.kairos.persistence.repository.tag;

import com.kairos.activity.tags.TagDTO;
import com.kairos.persistence.model.tag.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prerna on 21/11/17.
 */
@Repository
public interface TagMongoRepository extends MongoRepository<Tag,BigInteger>,CustomTagMongoRepository {


    Tag findTagByNameIgnoreCaseAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(String name, Long countryId, String masterDataTypeEnum, boolean deleted);

    Tag findTagByIdAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(BigInteger id, Long countryId, String masterDataTypeEnum, boolean deleted);

    @Query(value = "{ 'name' :{$regex:?1,$options:'i'},'countryId' : ?0, 'deleted' : ?2, 'countryTag' : true} ")
    List<Tag> findAllTagByCountryIdAndNameAndDeletedAndCountryTagTrue(Long countryId, String filterText, boolean deleted);

    @Query(value = "{ 'name' :{$regex:?1,$options:'i'},'countryId' : ?0, 'masterDataType' : ?2, 'deleted' : ?3, 'countryTag' : true} ")
    List<Tag> findAllTagByCountryIdAndNameAndMasterDataTypeAndDeletedAndCountryTagTrue(Long countryId, String filterText, String masterDataTypeEnum, boolean deleted);

    Tag findTagByIdAndCountryIdAndDeletedAndCountryTagTrue(BigInteger id, Long countryId, boolean deleted);




    Tag findTagByNameIgnoreCaseAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(String name, Long organizatonId, String masterDataTypeEnum, boolean deleted);

    Tag findTagByIdAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(BigInteger id, Long organizatonId, String masterDataTypeEnum, boolean deleted);

    @Query(value = "{ 'name' :{$regex:?1,$options:'i'},'organizationId' : ?0, 'deleted' : ?2, 'countryTag' : false } ")
    List<Tag> findAllTagByOrganizationIdAndNameAndDeletedAndCountryTagFalse(Long organizatonId, String filterText, boolean deleted);

    @Query(value = "{ 'name' :{$regex:?1,$options:'i'},'organizationId' : ?0, 'masterDataType' : ?2, 'deleted' : ?3, 'countryTag' : false } ")
    List<Tag> findAllTagByOrganizationIdAndNameAndMasterDataTypeAndDeletedAndCountryTagFalse(Long organizatonId, String filterText, String masterDataTypeEnum, boolean deleted);

    Tag findTagByIdAndOrganizationIdAndDeletedAndCountryTagFalse(BigInteger id, Long organizatonId, boolean deleted);


    Tag findTagByCountryIdAndNameAndMasterDataTypeAndDeletedAndCountryTagTrue(Long countryId, String name, String masterDataTypeEnum,boolean deleted);

    Tag findTagByOrganizationIdAndNameAndMasterDataTypeAndDeletedAndCountryTagFalse(Long orgId, String name, String masterDataTypeEnum,boolean deleted);

    List<TagDTO> findAllTagsByIdIn(List<Long> tagIds);
}

