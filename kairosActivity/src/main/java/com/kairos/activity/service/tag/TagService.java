package com.kairos.activity.service.tag;

import com.kairos.activity.client.CountryRestClient;
import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.controller.staffing_level.StaffingLevelController;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.tag.MasterDataTypeEnum;
import com.kairos.activity.persistence.model.tag.Tag;
import com.kairos.activity.persistence.repository.tag.TagMongoRepository;
import com.kairos.activity.response.dto.tag.TagDTO;
import com.kairos.activity.service.MongoBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 20/11/17.
 */
@Transactional
@Service
public class TagService extends MongoBaseService {

    private Logger logger= LoggerFactory.getLogger(StaffingLevelController.class);

    @Autowired
    CountryRestClient countryRestClient;

    @Autowired
    OrganizationRestClient organizationRestClient;

    @Autowired
    TagMongoRepository tagMongoRepository;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        if ( !countryRestClient.isCountryExists(countryId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagMongoRepository.findTagByNameIgnoreCaseAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false)  != null){
            throw new DuplicateDataException("Tag already exists with same name " +tagDTO.getName() );
        }
        return this.save(tagDTO.buildTag(tagDTO, true, countryId));
    }

    public Tag  updateCountryTag(Long countryId, BigInteger tagId, TagDTO tagDTO) {
        if ( !countryRestClient.isCountryExists(countryId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }

        Tag tag = tagMongoRepository.findTagByIdAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagId, countryId, tagDTO.getMasterDataType().toString(), false);
        if(  tag  == null){
            throw new DataNotFoundByIdException("Tag does not exist with Id " +tagId );
        }
        if(! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagMongoRepository.findTagByNameIgnoreCaseAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) != null ){
            throw new DuplicateDataException("Tag already exists with name " +tagDTO.getName() );
        }
        tag.setName(tagDTO.getName());
        this.save(tag);
        return tag;
    }

    public HashMap<String,Object> getListOfCountryTags(Long countryId, String filterText, MasterDataTypeEnum masterDataType){
        if ( !countryRestClient.isCountryExists(countryId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }

        if(filterText == null){
            filterText = "";
        }/* else {
            filterText = "/"+filterText+"/i";
        }*/

        HashMap<String,Object> tagsData = new HashMap<>();
        if(masterDataType == null){
            tagsData.put("tags", tagMongoRepository.findAllTagByCountryIdAndNameAndDeletedAndCountryTagTrue(countryId, filterText, false));
        } else {
            tagsData.put("tags", tagMongoRepository.findAllTagByCountryIdAndNameAndMasterDataTypeAndDeletedAndCountryTagTrue(countryId, filterText, masterDataType.toString(), false));
        }

        return tagsData;
    }

    public boolean deleteCountryTag(Long countryId, BigInteger tagId){
        if ( !countryRestClient.isCountryExists(countryId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Tag tag = tagMongoRepository.findTagByIdAndCountryIdAndDeletedAndCountryTagTrue(tagId, countryId, false);
        if( tag == null) {
            throw new DataNotFoundByIdException("Incorrect tag id " + tagId);
        }
        tag.setDeleted(true);
        this.save(tag);
        return true;
    }




    public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO, String type) {
        if(type.equalsIgnoreCase("team")){
            organizationId = organizationRestClient.getOrganizationIdByTeam(organizationId);
        }
        if ( !organizationRestClient.isExistOrganization(organizationId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + organizationId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagMongoRepository.findTagByNameIgnoreCaseAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false)  != null){
            throw new DuplicateDataException("Tag already exists with same name " +tagDTO.getName() );
        }
        return this.save(tagDTO.buildTag(tagDTO, false, organizationId));
    }

    public Tag  updateOrganizationTag(Long organizationId, BigInteger tagId, TagDTO tagDTO, String type) {
        if(type.equalsIgnoreCase("team")){
            organizationId = organizationRestClient.getOrganizationIdByTeam(organizationId);
        }
        if ( !organizationRestClient.isExistOrganization(organizationId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + organizationId);
        }

        Tag tag = tagMongoRepository.findTagByIdAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(tagId, organizationId, tagDTO.getMasterDataType().toString(), false);
        if(  tag  == null){
            throw new DuplicateDataException("Tag does not exist with Id " +tagId );
        }
        if(! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagMongoRepository.findTagByNameIgnoreCaseAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false) != null ){
            throw new DuplicateDataException("Tag already exists with name " +tagDTO.getName() );
        }
        tag.setName(tagDTO.getName());
        this.save(tag);
        return tag;
    }

    public HashMap<String,Object> getListOfOrganizationTags(Long organizationId, String filterText, MasterDataTypeEnum masterDataType, String type){
        if(type.equalsIgnoreCase("team")){
            organizationId = organizationRestClient.getOrganizationIdByTeam(organizationId);
        }
        if ( !organizationRestClient.isExistOrganization(organizationId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + organizationId);
        }

        if(filterText == null){
            filterText = "";
        } /*else {
            filterText = "/"+filterText+"/i";
        }*/

        HashMap<String,Object> tagsData = new HashMap<>();
        List<Tag> tags = null;
        if(masterDataType == null){
            tags =  tagMongoRepository.findAllTagByOrganizationIdAndNameAndDeletedAndCountryTagFalse(organizationId, filterText, false);
        } else {
            tags = tagMongoRepository.findAllTagByOrganizationIdAndNameAndMasterDataTypeAndDeletedAndCountryTagFalse(organizationId, filterText, masterDataType.toString(), false);
        }
        if(organizationRestClient.showCountryTagForOrganization(organizationId)){
            Long countryId = organizationRestClient.getCountryIdOfOrganization(organizationId);
            if(masterDataType == null){
                tags.addAll( tagMongoRepository.findAllTagByCountryIdAndNameAndDeletedAndCountryTagTrue(countryId, filterText, false));
            } else {
                tags.addAll( tagMongoRepository.findAllTagByCountryIdAndNameAndMasterDataTypeAndDeletedAndCountryTagTrue(countryId, filterText, masterDataType.toString(), false));
            }
        }
        tagsData.put("tags",tags);
        return tagsData;
    }

    public boolean deleteOrganizationTag(Long organizationId, BigInteger tagId, String type){
        if(type.equalsIgnoreCase("team")){
            organizationId = organizationRestClient.getOrganizationIdByTeam(organizationId);
        }
        if ( !organizationRestClient.isExistOrganization(organizationId)) {
            throw new DataNotFoundByIdException("Incorrect country id " + organizationId);
        }
        Tag tag = tagMongoRepository.findTagByIdAndOrganizationIdAndDeletedAndCountryTagFalse(tagId, organizationId, false);
        if( tag == null) {
            throw new DataNotFoundByIdException("Incorrect tag id " + tagId);
        }
        tag.setDeleted(true);
        this.save(tag);
        return true;
    }

    public Tag getCountryTagByName(Long countryId, String nameOfTag, MasterDataTypeEnum masterDataTypeEnum){
        return tagMongoRepository.findTagByCountryIdAndNameAndMasterDataTypeAndDeletedAndCountryTagTrue(countryId, nameOfTag, masterDataTypeEnum.toString(), false);

    }

    public Tag getOrganizationTagByName(Long orgId, String nameOfTag, MasterDataTypeEnum masterDataTypeEnum){
        return tagMongoRepository.findTagByOrganizationIdAndNameAndMasterDataTypeAndDeletedAndCountryTagFalse(orgId, nameOfTag, masterDataTypeEnum.toString(), false);

    }
}
