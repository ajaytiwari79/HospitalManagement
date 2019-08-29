package com.kairos.service.country.tag;

import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.UserMessagesConstants.*;

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
    private OrganizationBaseRepository organizationBaseRepository;

    @Inject
    private UnitGraphRepository unitGraphRepository;

    @Inject
    private SkillGraphRepository skillGraphRepository;

    @Inject
    private TeamGraphRepository teamGraphRepository;

    @Inject
    private ExceptionService exceptionService;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        if (tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());

        }
        Tag tag = new Tag(tagDTO.getName(), tagDTO.getMasterDataType(), true);
        if (CollectionUtils.isNotEmpty(country.getTags())) {
            country.getTags().add(tag);
        } else {
            country.setTags(Arrays.asList(tag));
        }
        countryGraphRepository.save(country);
        return tag;
    }

    public Tag updateCountryTag(Long countryId, Long tagId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        Tag tag = tagGraphRepository.getCountryTagByIdAndDataType(tagId, countryId, tagDTO.getMasterDataType().toString(), false);
        if (tag == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TAB_ID_NOTFOUND, tagId);
        }
        if (!(tag.getName().equalsIgnoreCase(tagDTO.getName())) && tagGraphRepository.isCountryTagExistsWithSameNameAndDataType(tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());

        }
        tag.setName(tagDTO.getName());
        return tagGraphRepository.save(tag);
    }

    public Map<String, Object> getListOfCountryTags(Long countryId, String filterText, MasterDataTypeEnum masterDataType) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }

        if (filterText == null) {
            filterText = "";
        }
        Map<String, Object> tagsData = new HashMap<>();
        if (masterDataType == null) {
            tagsData.put("tags", tagGraphRepository.getListOfCountryTags(countryId, false, filterText));
        } else {
            tagsData.put("tags", tagGraphRepository.getListOfCountryTagsByMasterDataType(countryId, false, filterText, masterDataType.toString()));
        }

        return tagsData;
    }

    public Boolean deleteCountryTag(Long countryId, Long tagId) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        Tag tag = tagGraphRepository.getCountryTag(countryId, tagId, false);
        if (tag == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TAB_ID_NOTFOUND, tagId);

        }
        tag.setDeleted(true);
        tagGraphRepository.save(tag);
        return true;
    }


    public Tag addOrganizationTag(Long organizationId, TagDTO tagDTO) {
        Unit unit = unitGraphRepository.findOne(organizationId, 0);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, organizationId);

        }
        if (tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());

        }
        Tag tag = new Tag(tagDTO.getName(), tagDTO.getMasterDataType(), false);
        if (CollectionUtils.isNotEmpty(unit.getTags())) {
            unit.getTags().add(tag);
        } else {
            unit.setTags(Arrays.asList(tag));
        }
        unitGraphRepository.save(unit);
        return tag;
    }

    public Tag updateOrganizationTag(Long organizationId, Long tagId, TagDTO tagDTO) {
        Unit unit = unitGraphRepository.findOne(organizationId, 0);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, organizationId);

        }
        Tag tag = tagGraphRepository.getOrganizationTagByIdAndDataType(tagId, organizationId, tagDTO.getMasterDataType().toString(), false);
        if (tag == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TAB_ID_NOTFOUND, tagId);

        }
        if (!(tag.getName().equalsIgnoreCase(tagDTO.getName())) && tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType(tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());

        }
        tag.setName(tagDTO.getName());
        tagGraphRepository.save(tag);
        return tag;
    }

    public Boolean deleteOrganizationTag(Long orgId, Long tagId) {
        Unit unit = unitGraphRepository.findOne(orgId, 0);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, orgId);

        }
        Tag tag = tagGraphRepository.getOrganizationTag(tagId, orgId, false);
        if (tag == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TAB_ID_NOTFOUND, tagId);

        }
        tag.setDeleted(true);
        tagGraphRepository.save(tag);
        return true;
    }

    public Map<String, Object> getListOfOrganizationTags(Long organizationId, String filterText, MasterDataTypeEnum masterDataType) {
        if (filterText == null) {
            filterText = "";
        }
        Map<String, Object> tagsData = new HashMap<>();
        if (masterDataType == null) {
            tagsData.put("tags", tagGraphRepository.getListOfOrganizationTags(organizationId, false, filterText));
        } else {
            tagsData.put("tags", tagGraphRepository.getListOfOrganizationTagsByMasterDataType(organizationId, false, filterText, masterDataType.toString()));
        }

        return tagsData;
    }

    public Boolean updateShowCountryTagSettingOfOrganization(Long organizationId, boolean showCountryTags) {
        Unit unit = unitGraphRepository.findOne(organizationId, 0);
        if (unit == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, organizationId);

        }
        unit.setShowCountryTags(showCountryTags);
        unitGraphRepository.save(unit);
        return showCountryTags;
    }


    public List<Tag> getCountryTagsByIdsAndMasterDataType(List<Long> tagsId, MasterDataTypeEnum masterDataType) {
        logger.info("tagsId : " + tagsId);
        if (tagsId != null && tagsId.size() > 0) {
            List<Tag> tags = tagGraphRepository.getCountryTagsById(tagsId, masterDataType.toString(), false);
            logger.info("tags : " + tags);
            return tags;
        } else {
            return new ArrayList<>();
        }
    }

    //todo delete
    private List<Tag> getOrganizationTagsByIdsAndMasterDataType(Long orgId, List<Long> tagsId) {
        logger.info("tagsId : " + tagsId);
        return CollectionUtils.isNotEmpty(tagsId) ? tagGraphRepository.getOrganizationTagsById(orgId, tagsId, MasterDataTypeEnum.SKILL.toString(), false) : new ArrayList<>();

    }

    public List<Tag> getCountryTagsOfSkill(long countryId, long skillId, String filterText) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        return tagGraphRepository.getCountryTagsOfSkillByIdAndDeleted(skillId, filterText, false);
    }

    public List<Tag> getCountryTagsOfExpertise(long countryId, long expertiseId, String filterText) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        return tagGraphRepository.getCountryTagsOfExpertiseByIdAndDeleted(expertiseId, filterText, false);
    }

    public List<Tag> getCountryTagsOfRuleTemplateCategory(long countryId, long ruleTmplCategoryId, String filterText) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        return tagGraphRepository.getCountryTagsOfRuleTemplateCategoryByIdAndDeleted(ruleTmplCategoryId, filterText, false);
    }

    public HashMap<String, Object> getListOfMasterDataType() {
        HashMap<String, Object> tagCategoryData = new HashMap<>();
        tagCategoryData.put("tagCategories", MasterDataTypeEnum.getListOfMasterDataType());
        return tagCategoryData;
    }

    public Map<String, Object> getListOfMasterDataType(Long orgId) {
        OrganizationBaseEntity organizationBaseEntity = organizationBaseRepository.findOne(orgId);
        if (organizationBaseEntity == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, orgId);
        }
        Map<String, Object> tagCategoryData = new HashMap<>();
        tagCategoryData.put("tagCategories", MasterDataTypeEnum.getListOfMasterDataType());
        tagCategoryData.put("showCountryTags", organizationBaseEntity.getShowCountryTags() != null ? organizationBaseEntity.getShowCountryTags() : false);
        return tagCategoryData;
    }

    public boolean updateOrganizationTagsOfSkill(Long skillId, Long orgId, List<Long> tagsId) {
        skillGraphRepository.removeAllOrganizationTags(orgId, skillId);
        Skill skill = skillGraphRepository.findOne(skillId);
        skill.setTags(getOrganizationTagsByIdsAndMasterDataType(orgId, tagsId));
        skillGraphRepository.save(skill);
        return true;

    }
}

