package com.kairos.service.country.tag;

import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prerna on 10/11/17.
 */
@Service
@Transactional
public class TagService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TagGraphRepository tagGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private SkillGraphRepository skillGraphRepository;

    @Inject
    private TeamGraphRepository teamGraphRepository;

    @Inject
    private ExceptionService exceptionService;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);
                    }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) ){
            exceptionService.duplicateDataException("message.tag.name.alreadyExist",tagDTO.getName());

        }
        return tagGraphRepository.createCountryTag(countryId,tagDTO.getName(), tagDTO.getMasterDataType().toString(), DateUtil.getCurrentDate().getTime());
    }

    public Tag  updateCountryTag(Long countryId, Long tagId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        Tag tag = tagGraphRepository.getCountryTagByIdAndDataType(tagId, countryId, tagDTO.getMasterDataType().toString(), false);
        if( tag == null) {
            exceptionService.dataNotFoundByIdException("message.tab.id.notFound",tagId);

        }

        /*if(! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagMongoRepository.findTagByNameIgnoreCaseAndCountryIdAndMasterDataTypeAndDeletedAndCountryTagTrue(tagDTO.getName(), countryId, tagDTO.getMasterDataType(), false) != null ){
            throw new DuplicateDataException("Tag already exists with name " +tagDTO.getName() );
        }*/
        if( ! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false) ){
            exceptionService.duplicateDataException("message.tag.name.alreadyExist",tagDTO.getName());

        }
        tag.setName(tagDTO.getName());
        return tagGraphRepository.save(tag);
    }

    public HashMap<String,Object> getListOfCountryTags(Long countryId, String filterText, MasterDataTypeEnum masterDataType){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

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

    public Boolean deleteCountryTag(Long countryId, Long tagId){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        Tag tag = tagGraphRepository.getCountryTag(countryId, tagId, false);
        if( tag == null) {
            exceptionService.dataNotFoundByIdException("message.tab.id.notFound",tagId);

        }
        tag.setDeleted(true);
        tagGraphRepository.save(tag);
        return true;
    }

    /*public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO) {
        return tagGraphRepository.addOrganizationTag(tagDTO.getName(), organizationId, tagDTO.getMasterDataType(), DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime() );
    }*/

    public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO, String type) {
        if(type.equalsIgnoreCase("team")){
            organizationId = teamGraphRepository.getOrganizationIdByTeam(organizationId);
        }
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",organizationId);

        }
        logger.info("tagDTO : "+tagDTO.getMasterDataType());
        if( tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false) ){
            exceptionService.duplicateDataException("message.tag.name.alreadyExist",tagDTO.getName());

        }
        return tagGraphRepository.createOrganizationTag(organizationId,tagDTO.getName(), tagDTO.getMasterDataType().toString(), DateUtil.getCurrentDate().getTime());
    }

    public Tag  updateOrganizationTag(Long organizationId, Long tagId, TagDTO tagDTO, String type) {
        if(type.equalsIgnoreCase("team")){
            organizationId = teamGraphRepository.getOrganizationIdByTeam(organizationId);
        }
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",organizationId);

        }
        Tag tag = tagGraphRepository.getOrganizationTagByIdAndDataType(tagId, organizationId, tagDTO.getMasterDataType().toString(), false);
        if( tag == null){
            exceptionService.dataNotFoundByIdException("message.tab.id.notFound",tagId);

        }
        if( ! ( tag.getName().equalsIgnoreCase(tagDTO.getName()) ) && tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false) ){
            exceptionService.duplicateDataException("message.tag.name.alreadyExist",tagDTO.getName());

        }
        tag.setName(tagDTO.getName());
        tagGraphRepository.save(tag);
        return tag;
        //return tagGraphRepository.updateOrganizationTag(tagId, organizationId, tagDTO.getName(), DateUtil.getCurrentDate().getTime());
    }

    public Boolean deleteOrganizationTag(Long orgId, Long tagId, String type){
        if(type.equalsIgnoreCase("team")){
            orgId = teamGraphRepository.getOrganizationIdByTeam(orgId);
        }
        Organization organization = organizationGraphRepository.findOne(orgId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",orgId);

        }
        Tag tag = tagGraphRepository.getOrganizationTag(tagId, orgId, false);
        if( tag == null) {
            exceptionService.dataNotFoundByIdException("message.tab.id.notFound",tagId);

        }
        tag.setDeleted(true);
        tagGraphRepository.save(tag);
        return true;
    }

    public HashMap<String, Object> getListOfOrganizationTags(Long organizationId, String filterText, MasterDataTypeEnum masterDataType, String type){
        if(type.equalsIgnoreCase("team")){
            organizationId = teamGraphRepository.getOrganizationIdByTeam(organizationId);
        }
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",organizationId);

        }
        if(filterText == null){
            filterText = "";
        }
        HashMap<String,Object> tagsData = new HashMap<>();
        if(masterDataType == null){
            tagsData.put("tags",tagGraphRepository.getListOfOrganizationTags(organizationId, false, filterText));
        } else {
            tagsData.put("tags",tagGraphRepository.getListOfOrganizationTagsByMasterDataType(organizationId, false, filterText,  masterDataType.toString()));
        }

        return tagsData;
    }

    public Boolean updateShowCountryTagSettingOfOrganization(Long organizationId, boolean showCountryTags) {
        Organization organization = organizationGraphRepository.findOne(organizationId, 0);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",organizationId);

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

    public List<Tag> getOrganizationTagsByIdsAndMasterDataType(Long orgId, List<Long> tagsId, MasterDataTypeEnum masterDataType){
        logger.info("tagsId : "+tagsId);
        if (tagsId != null && tagsId.size() > 0) {
            List<Tag> tags = tagGraphRepository.getOrganizationTagsById(orgId, tagsId, masterDataType.toString(), false);
            logger.info("tags : "+tags);
            return tags;
        } else {
            return new ArrayList<Tag>();
        }
    }

    public List<Tag> getCountryTagsOfSkill(long countryId, long skillId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        return tagGraphRepository.getCountryTagsOfSkillByIdAndDeleted(skillId, filterText, false);
    }

    public List<Tag> getCountryTagsOfExpertise(long countryId, long expertiseId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        return tagGraphRepository.getCountryTagsOfExpertiseByIdAndDeleted(expertiseId, filterText, false);
    }

    /*public List<Tag> getCountryTagsOfWTA(long countryId, long wtaId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        return tagGraphRepository.getCountryTagsOfWTAByIdAndDeleted(wtaId, filterText, false);
    }*/

    public List<Tag> getCountryTagsOfRuleTemplateCategory(long countryId, long ruleTmplCategoryId, String filterText){
        Country country = countryGraphRepository.findOne(countryId,0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound",countryId);

        }
        return tagGraphRepository.getCountryTagsOfRuleTemplateCategoryByIdAndDeleted(ruleTmplCategoryId, filterText, false);
    }

    public HashMap<String,Object> getListOfMasterDataType(){
        HashMap<String, Object> tagCategoryData = new HashMap<String,Object>();
        tagCategoryData.put("tagCategories",MasterDataTypeEnum.getListOfMasterDataType());
        return tagCategoryData;
    }

    public HashMap<String,Object> getListOfMasterDataType(Long orgId){
        Organization organization = organizationGraphRepository.findOne(orgId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unit.id.notFound",orgId);

        }
        HashMap<String, Object> tagCategoryData = new HashMap<String,Object>();
        tagCategoryData.put("tagCategories",MasterDataTypeEnum.getListOfMasterDataType());
        tagCategoryData.put("showCountryTags", organization.isShowCountryTags() != null ? organization.isShowCountryTags(): false);
        return tagCategoryData;
    }

    public boolean updateOrganizationTagsOfSkill(Long skillId, Long orgId, List<Long> tagsId){
        Skill skill = skillGraphRepository.findOne(skillId);
        skillGraphRepository.removeAllOrganizationTags(orgId,skillId);
        List<Tag> listOfTags = tagGraphRepository.getTagsOfSkillByDeleted(skillId, false);
        listOfTags.addAll(getOrganizationTagsByIdsAndMasterDataType(orgId,tagsId,MasterDataTypeEnum.SKILL));
        skill.setTags(listOfTags);
        skillGraphRepository.save(skill);
        return true;

    }
    // Methods created for test cases

    public Tag getCountryTagByName(long countryId, String name, MasterDataTypeEnum masterDataTypeEnum){
        return tagGraphRepository.getCountryTagByNameAndType(countryId, name, masterDataTypeEnum.toString(), false);
    }

    public Tag getOrganizationTagByName(long orgId, String name, MasterDataTypeEnum masterDataTypeEnum){
        return tagGraphRepository.getOrganizationTagByName(orgId, name, masterDataTypeEnum.toString(), false);
    }
}

