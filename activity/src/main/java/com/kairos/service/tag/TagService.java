package com.kairos.service.tag;

import com.kairos.controller.staffing_level.StaffingLevelController;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.tag.Tag;
import com.kairos.persistence.repository.tag.TagMongoRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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
    private GenericIntegrationService genericIntegrationService;

    @Autowired
    TagMongoRepository tagMongoRepository;
    @Autowired
    ExceptionService exceptionService;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        if ( !genericIntegrationService.isCountryExists(countryId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagMongoRepository.findTagByNameIgnoreCaseAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false)  != null){
           exceptionService.duplicateDataException("message.tag.name",tagDTO.getName() );
        }
        return this.save(buildTag(tagDTO, true, countryId));
    }

    public Tag  updateCountryTag(Long countryId, BigInteger tagId, TagDTO tagDTO) {
        if ( !genericIntegrationService.isCountryExists(countryId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }

        Tag tag = tagMongoRepository.findTagByIdAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagId, countryId, tagDTO.getMasterDataType().toString(), false);
        if(  tag  == null){
            exceptionService.dataNotFoundByIdException("message.tag.id",tagId);
        }
        if(! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagMongoRepository.findTagByNameIgnoreCaseAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) != null ){
            exceptionService.duplicateDataException("message.tag.name",tagDTO.getName());
        }
        tag.setName(tagDTO.getName());
        this.save(tag);
        return tag;
    }

    public HashMap<String,Object> getListOfCountryTags(Long countryId, String filterText, MasterDataTypeEnum masterDataType){
        if ( !genericIntegrationService.isCountryExists(countryId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
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
        if ( !genericIntegrationService.isCountryExists(countryId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",countryId);
        }
        Tag tag = tagMongoRepository.findTagByIdAndCountryIdAndDeletedAndCountryTagTrue(tagId, countryId, false);
        if( tag == null) {
            exceptionService.dataNotFoundByIdException("message.tag.id",tagId);
        }
        tag.setDeleted(true);
        this.save(tag);
        return true;
    }


    public List<Tag> getCountryTagsByIdsAndMasterDataType(List<BigInteger> tags, com.kairos.enums.MasterDataTypeEnum type){
        return null;
    }



    public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO, String type) {
        if(type.equalsIgnoreCase("team")){
            organizationId = genericIntegrationService.getOrganizationIdByTeam(organizationId);
        }
        if ( !genericIntegrationService.isExistOrganization(organizationId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",organizationId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagMongoRepository.findTagByNameIgnoreCaseAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false)  != null){
           exceptionService.duplicateDataException("message.tag.name",tagDTO.getName());
        }
        return this.save(buildTag(tagDTO, false, organizationId));
    }

    public Tag  updateOrganizationTag(Long organizationId, BigInteger tagId, TagDTO tagDTO, String type) {
        if(type.equalsIgnoreCase("team")){
            organizationId = genericIntegrationService.getOrganizationIdByTeam(organizationId);
        }
        if ( !genericIntegrationService.isExistOrganization(organizationId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",organizationId);
        }

        Tag tag = tagMongoRepository.findTagByIdAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(tagId, organizationId, tagDTO.getMasterDataType().toString(), false);
        if(  tag  == null){
            exceptionService.duplicateDataException("message.tag.id",tagId);
        }
        if(! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagMongoRepository.findTagByNameIgnoreCaseAndOrganizationIdAndMasterDataTypeAndDeletedAndCountryTagFalse(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false) != null ){
            exceptionService.duplicateDataException("message.tag.name",tagDTO.getName());
        }
        tag.setName(tagDTO.getName());
        this.save(tag);
        return tag;
    }

    public HashMap<String,Object> getListOfOrganizationTags(Long organizationId, String filterText, MasterDataTypeEnum masterDataType, String type){
        if(type.equalsIgnoreCase("team")){
            organizationId = genericIntegrationService.getOrganizationIdByTeam(organizationId);
        }
        if ( !genericIntegrationService.isExistOrganization(organizationId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",organizationId);
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
        if(genericIntegrationService.showCountryTagForOrganization(organizationId)){
            Long countryId = genericIntegrationService.getCountryIdOfOrganization(organizationId);
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
            organizationId = genericIntegrationService.getOrganizationIdByTeam(organizationId);
        }
        if ( !genericIntegrationService.isExistOrganization(organizationId)) {
            exceptionService.dataNotFoundByIdException("message.country.id",organizationId);
        }
        Tag tag = tagMongoRepository.findTagByIdAndOrganizationIdAndDeletedAndCountryTagFalse(tagId, organizationId, false);
        if( tag == null) {
            exceptionService.dataNotFoundByIdException("message.tag.id",tagId);
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

    public static Tag buildTag(TagDTO tagDTO, boolean countryTag, long countryOrOrgId){
        return new Tag(tagDTO.getName(), tagDTO.getMasterDataType(), countryTag, countryOrOrgId);
    }
}
