package com.kairos.service.country.tag;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.tag.PenaltyScore;
import com.kairos.persistence.model.country.tag.Tag;
import com.kairos.persistence.model.country.tag.TagQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationBaseEntity;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.TeamGraphRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TagGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
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

    @Inject
    private OrganizationService organizationService;

    @Inject
    private StaffService staffService;

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Inject
    private ActivityIntegrationService activityIntegrationService;

    public Tag addCountryTag(Long countryId, TagDTO tagDTO) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        }
        if (tagGraphRepository.isCountryTagExistsWithSameNameAndDataType("(?i)" +tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());

        }
        Tag tag = new Tag(tagDTO.getName(), tagDTO.getMasterDataType(), true, tagDTO.getOrgTypeId(), tagDTO.getOrgSubTypeIds(), tagDTO.getColor(),tagDTO.getShortName(),tagDTO.getUltraShortName());
        if (CollectionUtils.isNotEmpty(country.getTags())) {
            country.getTags().add(tag);
        } else {
            country.setTags(Arrays.asList(tag));
        }
        countryGraphRepository.save(country);
        if(MasterDataTypeEnum.STAFF.equals(tagDTO.getMasterDataType())){
            mappingTagAtOrganization(tagDTO);
        }
        return tag;
    }

    private void mappingTagAtOrganization(TagDTO tagDTO) {
        List<Organization> organizations = organizationGraphRepository.getOrganizationsBySubOrgTypeIds(tagDTO.getOrgSubTypeIds());
        if(isCollectionNotEmpty(organizations)) {
            for(Organization org : organizations) {
                Tag tag = new Tag(tagDTO.getName(), tagDTO.getMasterDataType(), false, new PenaltyScore(ScoreLevel.SOFT,0), tagDTO.getColor(),tagDTO.getShortName(),tagDTO.getUltraShortName());
                if (isCollectionNotEmpty(org.getTags())) {
                    org.getTags().add(tag);
                } else {
                    org.setTags(Arrays.asList(tag));
                }
            }
            organizationGraphRepository.saveAll(organizations);
        }
    }

    public List<Tag> getCountryTagByOrgSubTypes(Long countryId, List<Long> orgSubTypeId){
        Country country = countryGraphRepository.findOne(countryId);
        List<Tag> tags = new ArrayList<>();
        for(Tag tag : country.getTags().stream().filter(tag -> MasterDataTypeEnum.STAFF.equals(tag.getMasterDataType()) && CollectionUtils.containsAny(tag.getOrgSubTypeIds(),orgSubTypeId)).collect(Collectors.toList())){
            tags.add(new Tag(tag.getName(),tag.getMasterDataType(),false,new PenaltyScore(ScoreLevel.SOFT,0),tag.getColor(),tag.getShortName(),tag.getUltraShortName()));
        }
        return  tags;
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
        if (!(tag.getName().equalsIgnoreCase(tagDTO.getName())) && tagGraphRepository.isCountryTagExistsWithSameNameAndDataType("(?i)" + tagDTO.getName(), countryId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());

        }
        tag.setName(tagDTO.getName());
        tag.setColor(tagDTO.getColor());
        tag.setShortName(tagDTO.getShortName());
        tag.setUltraShortName(tagDTO.getUltraShortName());
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
            List<TagQueryResult> tagQueryResults =tagGraphRepository.getListOfCountryTags(countryId, false, filterText);
            tagQueryResults.forEach(tagQueryResult -> {
                tagQueryResult.setCountryId(countryId);
                tagQueryResult.setTranslations(TranslationUtil.getTranslatedData(tagQueryResult.getTranslatedNames(),tagQueryResult.getTranslatedDescriptions()));
            });
            tagsData.put("tags",tagQueryResults );
        } else {
            List<TagQueryResult> tagQueryResultList =tagGraphRepository.getListOfCountryTagsByMasterDataType(countryId, false, filterText, masterDataType.toString());
            tagQueryResultList.forEach(tagQueryResult -> {
                tagQueryResult.setCountryId(countryId);
                tagQueryResult.setTranslations(TranslationUtil.getTranslatedData(tagQueryResult.getTranslatedNames(),tagQueryResult.getTranslatedDescriptions()));
            });
            tagsData.put("tags",tagQueryResultList );
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
        OrganizationBaseEntity org = organizationBaseRepository.findOne(organizationId, 0);
        if (!Optional.ofNullable(org).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, organizationId);
        }
        if (tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType("(?i)" + tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());
        }
        Tag tag = new Tag(tagDTO.getName(), tagDTO.getMasterDataType(), false, ObjectMapperUtils.copyPropertiesByMapper(tagDTO.getPenaltyScore(), PenaltyScore.class), tagDTO.getColor(),tagDTO.getShortName(),tagDTO.getUltraShortName());
        if (CollectionUtils.isNotEmpty(org.getTags())) {
            org.getTags().add(tag);
        } else {
            org.setTags(Arrays.asList(tag));
        }
        organizationBaseRepository.save(org);
        return tag;
    }

    public Tag updateOrganizationTag(Long organizationId, Long tagId, TagDTO tagDTO) {
        OrganizationBaseEntity org = organizationBaseRepository.findOne(organizationId, 0);
        if (org == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, organizationId);

        }
        Tag tag = tagGraphRepository.getOrganizationTagByIdAndDataType(tagId, organizationId, tagDTO.getMasterDataType().toString(), false);
        if (tag == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TAB_ID_NOTFOUND, tagId);
        }
        if (!(tag.getName().equalsIgnoreCase(tagDTO.getName())) && tagGraphRepository.isOrganizationTagExistsWithSameNameAndDataType("(?i)" + tagDTO.getName(), organizationId, tagDTO.getMasterDataType().toString(), false)) {
            exceptionService.duplicateDataException(MESSAGE_TAG_NAME_ALREADYEXIST, tagDTO.getName());
        }
        tag.setName(tagDTO.getName());
        if(MasterDataTypeEnum.STAFF.equals(tagDTO.getMasterDataType())){
            tag.getPenaltyScore().setPenaltyScoreLevel(tagDTO.getPenaltyScore().getPenaltyScoreLevel());
            tag.getPenaltyScore().setValue(tagDTO.getPenaltyScore().getValue());
            tag.setColor(tagDTO.getColor());
            tag.setShortName(tagDTO.getShortName());
            tag.setUltraShortName(tagDTO.getUltraShortName());
        }
        tagGraphRepository.save(tag);
        return tag;
    }

    public Boolean deleteOrganizationTag(Long orgId, Long tagId) {
        OrganizationBaseEntity org = organizationBaseRepository.findOne(orgId, 0);
        if (org == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, orgId);
        }
        Tag tag = tagGraphRepository.getOrganizationTag(tagId, orgId, false);
        if (tag == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_TAB_ID_NOTFOUND, tagId);
        }
        tag.setDeleted(true);
        staffService.unlinkTagFromStaff(tagId);
        activityIntegrationService.unlinkTagFromActivity(orgId, tagId);
        tagGraphRepository.save(tag);
        return true;
    }

    public Map<String, Object> getListOfOrganizationTags(Long organizationId, String filterText, MasterDataTypeEnum masterDataType) {
        Unit unit = unitGraphRepository.findOne(organizationId);
        if(isNotNull(unit)){
            organizationId = organizationBaseRepository.findParentOrgId(organizationId);
        }
        if (filterText == null) {
            filterText = "";
        }
        Map<String, Object> tagsData = new HashMap<>();
        if (masterDataType == null) {
            List<TagQueryResult> tagQueryResults =tagGraphRepository.getListOfOrganizationTags(organizationId, false, filterText);
            tagQueryResults.forEach(tagQueryResult -> {
                tagQueryResult.setUnutId(unit.getId());
                tagQueryResult.setTranslations(TranslationUtil.getTranslatedData(tagQueryResult.getTranslatedNames(),tagQueryResult.getTranslatedDescriptions()));
            });
            tagsData.put("tags", tagQueryResults);
        }else {
            List<TagQueryResult> tagQueryResults = tagGraphRepository.getListOfOrganizationTagsByMasterDataType(organizationId, false, filterText, masterDataType.toString());
            tagQueryResults.forEach(tagQueryResult -> {
                tagQueryResult.setCountryId(UserContext.getUserDetails().getCountryId());
                tagQueryResult.setTranslations(TranslationUtil.getTranslatedData(tagQueryResult.getTranslatedNames(),tagQueryResult.getTranslatedDescriptions()));
            });
            tagsData.put("tags", tagQueryResults);
        }
        return tagsData;
    }

    public Boolean updateShowCountryTagSettingOfOrganization(Long organizationId, boolean showCountryTags) {
        OrganizationBaseEntity org = organizationBaseRepository.findOne(organizationId, 0);
        if (org == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_UNIT_ID_NOTFOUND, organizationId);
        }
        org.setShowCountryTags(showCountryTags);
        organizationBaseRepository.save(org);
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

    public List<TagDTO> getTagsByOrganizationIdAndMasterDataType(Long orgId, MasterDataTypeEnum masterDataType) {
        Unit unit = unitGraphRepository.findOne(orgId);
        if(isNotNull(unit)){
            orgId = organizationBaseRepository.findParentOrgId(orgId);
        }
        List<TagQueryResult> tagQueryResults = tagGraphRepository.getListOfStaffOrganizationTags(orgId,false,"", masterDataType.toString());
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(tagQueryResults,TagDTO.class);
    }

    public Map<String, TranslationInfo> updateTranslationOfTag(Long tagId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        Tag tag =tagGraphRepository.findOne(tagId);
        tag.setTranslatedNames(translatedNames);
        tag.setTranslatedDescriptions(translatedDescriptios);
        tagGraphRepository.save(tag);
        return tag.getTranslatedData();
    }
}

