package com.kairos.service.country.tag;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.tag.Tag;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.response.dto.web.tag.TagDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by prerna on 10/11/17.
 */
@Service
@Transactional
public class TagService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TagGraphRepository tagGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag already exists with same name " +tagDTO.getName() );
        }
        return tagGraphRepository.createCountryTag(countryId,tagDTO.getName(), tagDTO.getMasterDataType().toString());
        /*country.setTags();
        CountryTagRelationship countryTagRelationship = new CountryTagRelationship();
        countryTagRelationship.setCountry(country);
        countryTagRelationship.setTag(new Tag(tagDTO, true));
        countryTagRelationship.setMasterDataType(tagDTO.getMasterDataType());
        countryTagRelationship.setCreationDate(new Date().getTime());
        countryTagRelationship.setLastModificationDate(new Date().getTime());
        countryTagRelationshipGraphRepository.save(countryTagRelationship);

        tagDTO.setId(countryTagRelationship.getTag().getId());
        return tagDTO;*/
    }

    public Tag  updateCountryTag(Long countryId, Long tagId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        if(! tagGraphRepository.isCountryTagExistsWithDataType(tagId, countryId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag does not exist with id " +tagId );
        }
        if( tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag already exists with name " +tagDTO.getName() );
        }
        return tagGraphRepository.updateCountryTag(tagId, countryId, tagDTO.getName(), new Date().getTime());
    }

    public HashMap<String,Object> getListOfCountryTags(Long countryId, String filterText, MasterDataTypeEnum masterDataType){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }

        if(filterText == null){
            filterText = "";
        }
//        String filterTextRegex = "~'.*"+filterText+".*'";

        HashMap<String,Object> tagsData = new HashMap<>();
        if(masterDataType == null){
            tagsData.put("tags",tagGraphRepository.getListOfCountryTags(countryId, false, filterText));
        } else {
            tagsData.put("tags",tagGraphRepository.getListOfCountryTagsByMasterDataType (countryId, false, filterText,  masterDataType.toString()));
        }

        return tagsData;
    }

    /*public HashMap<String,Object> getTagsOfSkill(Long countryId, Long skillId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }

        if(filterText == null){
            filterText = "";
        }
//        String filterTextRegex = "~'.*"+filterText+".*'";

        HashMap<String,Object> tagsData = new HashMap<>();
        if(masterDataType == null){
            tagsData.put("tags",tagGraphRepository.getListOfCountryTags(countryId, false, filterText));
        } else {
            tagsData.put("tags",tagGraphRepository.getListOfCountryTagsByMasterDataType (countryId, false, filterText,  masterDataType.toString()));
        }

        return tagsData;
    }*/

    public boolean deleteCountryTag(Long countryId, Long tagId){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            throw new DataNotFoundByIdException("Incorrect country id " + countryId);
        }
        Tag tag = tagGraphRepository.getCountryTag(countryId, tagId, false);
        if( tag == null) {
            throw new DataNotFoundByIdException("Incorrect tag id " + tagId);
        }
        tag.setDeleted(true);
        save(tag);
        return true;
    }

    /*public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO) {
        return tagGraphRepository.addOrganizationTag(tagDTO.getName(), organizationId, tagDTO.getMasterDataType(), new Date().getTime(), new Date().getTime() );
    }*/


    public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + organizationId);
        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag already exists with same name " +tagDTO.getName() );
        }
        return tagGraphRepository.createOrganizationTag(organizationId,tagDTO.getName(), tagDTO.getMasterDataType().toString());
        /*OrganizationTagRelationship organizationTagRelationship = new OrganizationTagRelationship();
        organizationTagRelationship.setOrganization(organization);
        organizationTagRelationship.setTag(new Tag(tagDTO.getName()));
        organizationTagRelationship.setMasterDataType(tagDTO.getMasterDataType());
        organizationTagRelationship.setCreationDate(new Date().getTime());
        organizationTagRelationship.setLastModificationDate(new Date().getTime());
        organizationTagRelationshipGraphRepository.save(organizationTagRelationship);

        tagDTO.setId(organizationTagRelationship.getTag().getId());
        return tagDTO;*/
    }
    /*public Tag updateOrganizationTag(Long organizationId, TagDTO tagDTO, Long tagId) {
        return tagGraphRepository.updateOrganizationTag(tagId, organizationId, tagDTO.getName(), new Date().getTime());
    }*/


    public Tag  updateOrganizationTag(Long organizationId, Long tagId, TagDTO tagDTO) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + organizationId);
        }
        if(! tagGraphRepository.isOrganizationTagExistsWithDataType(tagId, organizationId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag does not exist with id " +tagId );
        }
        if( tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false) ){
            throw new DuplicateDataException("Tag already exists with name " +tagDTO.getName() );
        }
        return tagGraphRepository.updateOrganizationTag(tagId, organizationId, tagDTO.getName(), new Date().getTime());
    }

    public boolean deleteOrganizationTag(Long orgId, Long tagId){
        Organization organization = organizationGraphRepository.findOne(orgId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + orgId);
        }
        Tag tag = tagGraphRepository.getOrganizationTag(tagId, orgId, false);
        if( tag == null) {
            throw new DataNotFoundByIdException("Incorrect tag id " + tagId);
        }
        tag.setDeleted(true);
        save(tag);
        return true;
    }

    public HashMap<String, Object> getListOfOrganizationTags(Long organizationId, String filterText, MasterDataTypeEnum masterDataType){
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + organizationId);
        }
        if(filterText == null){
            filterText = "";
        }
//        String filterTextRegex = "~'.*"+filterText+".*'";
        HashMap<String,Object> tagsData = new HashMap<>();
        if(masterDataType == null){
            tagsData.put("tags",tagGraphRepository.getListOfOrganizationTags(organizationId, false, filterText));
        } else {
            tagsData.put("tags",tagGraphRepository.getListOfOrganizationTagsByMasterDataType(organizationId, false, filterText,  masterDataType.toString()));
        }

        return tagsData;
    }

    public boolean updateShowCountryTagSettingOfOrganization(Long organizationId, boolean showCountryTags) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            throw new DataNotFoundByIdException("Incorrect Unit Id " + organizationId);
        }
        organization.setShowCountryTags(showCountryTags);
        organizationGraphRepository.save(organization);
        return showCountryTags;
    }

    public List<Tag> getTagsByIds(List<Long> tagsId){
        if (tagsId != null && tagsId.size() > 0) {
            return tagGraphRepository.getTagsById(tagsId, false);
        } else {
            return new ArrayList<Tag>();
        }
    }

    public List<Tag> getCountryTagsByIdsAndMasterDataType(List<Long> tagsId, MasterDataTypeEnum masterDataType){
        logger.info("tagsId : "+tagsId);
        if (tagsId != null && tagsId.size() > 0) {
            List<Tag> tags = tagGraphRepository.getCountryTagsById(tagsId, masterDataType.toString(), false);
            logger.info("tags : "+tags);
            return tags;
        } else {
            return new ArrayList<Tag>();
        }
    }

    /*public List<TagQueryResult> getTagsOfSkill(long skillId, String filterText){

    }*/
}

