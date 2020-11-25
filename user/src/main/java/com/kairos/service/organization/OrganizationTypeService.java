package com.kairos.service.organization;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.LevelDTO;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.dto.user.organization.OrganizationTypeDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization_type.OrganizationTypeSubTypeAndServicesQueryResult;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.model.user.skill.SkillCategoryQueryResults;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.OrganizationTypeAndSubTypeDto;
import com.kairos.wrapper.UpdateOrganizationTypeDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.UserMessagesConstants.*;

/**
 * Created by oodles on 18/10/16.
 */
@Transactional
@Service
public class OrganizationTypeService{
    @Inject
    private OrganizationTypeGraphRepository organizationTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public List<OrgTypeLevelWrapper> getOrgTypesByCountryId(Long countryId) {

        List<OrgTypeLevelWrapper> orgTypeLevelWrappers = organizationTypeGraphRepository.getOrganizationTypeByCountryId(countryId);
        orgTypeLevelWrappers.forEach(orgTypeLevelWrapper -> {
            List<LevelDTO> levelDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(orgTypeLevelWrapper.getLevels(),LevelDTO.class);
            levelDTOS.forEach(levelDTO -> {
                levelDTO.setTranslations(TranslationUtil.getTranslatedData(levelDTO.getTranslatedNames(),levelDTO.getTranslatedDescriptions()));
            });
            orgTypeLevelWrapper.setCountryId(countryId);
            orgTypeLevelWrapper.setTranslations(TranslationUtil.getTranslatedData(orgTypeLevelWrapper.getTranslatedNames(),orgTypeLevelWrapper.getTranslatedDescriptions()));
            orgTypeLevelWrapper.setLevels(levelDTOS);

        });
        return orgTypeLevelWrappers;
    }

    public OrganizationType createOrganizationTypeForCountry(Long countryId, OrganizationTypeDTO organizationTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        OrganizationType isAlreadyExist = organizationTypeGraphRepository.findByName(countryId, organizationTypeDTO.getName().trim());
        if (Optional.ofNullable(isAlreadyExist).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_ORGANIZATIONTYPE_NAME_DUPLICATE);

        }
        List<Level> levels = countryGraphRepository.getLevelsByIdsIn(countryId, organizationTypeDTO.getLevels());

        OrganizationType organizationType = new OrganizationType(organizationTypeDTO.getName(), country, levels);
        return prepareResponse(organizationTypeGraphRepository.save(organizationType));
    }


    public OrganizationType getOrganizationTypeById(Long organizationTypeId) {
        return organizationTypeGraphRepository.findOne(organizationTypeId);

    }

    public OrganizationType createOrganizationType(OrganizationType organizationType) {
        return organizationTypeGraphRepository.save(organizationType);
    }


    public List<OrganizationType> getAllOrganizationTypes() {
        return organizationTypeGraphRepository.findAll();
    }

    public boolean deleteOrganizationType(Long organizationTypeId) {
        OrganizationType organizationType = organizationTypeGraphRepository.findOne(organizationTypeId);
        if (organizationType != null) {
            organizationType.setEnable(false);
            organizationTypeGraphRepository.save(organizationType);
            return true;
        }
        return false;

    }

    public List<OrganizationTypeDTO> getAllOrganizationTypeAndSubType(long countryId) {
        List<OrganizationTypeAndSubType> organizationTypeAndSubTypes = organizationTypeGraphRepository.getAllOrganizationTypeAndSubType(countryId);
        List<OrganizationTypeDTO> organizationTypeDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(organizationTypeAndSubTypes,OrganizationTypeDTO.class);
        return organizationTypeDTOS;
    }


    public OrganizationType updateOrganizationType(UpdateOrganizationTypeDTO updateOrganizationTypeDTO) {
        OrganizationType orgTypeToUpdate = organizationTypeGraphRepository.findOne(updateOrganizationTypeDTO.getId());
        if (!Optional.ofNullable(orgTypeToUpdate).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATIONTYPE_ID_NOTFOUND, updateOrganizationTypeDTO.getId());

        }
        if (!updateOrganizationTypeDTO.getLevelsToDelete().isEmpty()) {
            organizationTypeGraphRepository.removeLevelRelationshipFromOrganizationType(updateOrganizationTypeDTO.getId(), updateOrganizationTypeDTO.getLevelsToDelete());
            orgTypeToUpdate.setLevels(null);
        }
        if (!updateOrganizationTypeDTO.getLevelsToUpdate().isEmpty()) {
            List<Level> levels = countryGraphRepository.getLevelsByIdsIn(orgTypeToUpdate.getCountry().getId(), updateOrganizationTypeDTO.getLevelsToUpdate());
            orgTypeToUpdate.setLevels(levels);
        }
        orgTypeToUpdate.setName(updateOrganizationTypeDTO.getName().trim());
        return prepareResponse(organizationTypeGraphRepository.save(orgTypeToUpdate));
    }

    private OrganizationType prepareResponse(OrganizationType organizationType) {
        OrganizationType response = new OrganizationType();
        response.setId(organizationType.getId());
        response.setName(organizationType.getName());
        List<Level> activeLevels = organizationType.getLevels().parallelStream().filter(level -> !level.isDeleted()).collect(Collectors.toList());
        response.setLevels(activeLevels);
        return response;
    }

    public Map<String, Object> addOrganizationTypeSubType(OrganizationType organizationType, Long organizationTypeId) {
        OrganizationType type = organizationTypeGraphRepository.findOne(organizationTypeId);
        if (type != null) {
            organizationType = organizationTypeGraphRepository.save(organizationType);
            organizationTypeGraphRepository.createSubTypeRelation(organizationType.getId(), organizationTypeId);
            return organizationType.retrieveDetails();
        }
        return null;
    }

    public List<OrgTypeLevelWrapper> getOrgSubTypesByTypeId(Long organizationTypeId) {
        List<OrgTypeLevelWrapper> organizationSubTypes = organizationTypeGraphRepository.getOrganizationSubTypeByTypeId(organizationTypeId);
        return organizationSubTypes;
    }

    /**
     * @param skillId
     * @param orgTypeId
     * @param isSelected
     * @author prabjot
     * this method will update the relationship of skill and organization Type based on parameter {isSelected},if parameter value is true
     * new relationship b/w skill and organization type will be created or updated(if relationship already exist) if parameter value is false
     * then relationship will be inactive (deleted param of relationship will set to true)
     */
    public List<SkillCategoryQueryResults> addSkillInOrgType(long orgTypeId, long skillId, boolean isSelected) {
        if (isSelected) {
            organizationTypeGraphRepository.addSkillInOrgType(orgTypeId, skillId, DateUtils.getCurrentDateMillis(), DateUtils.getCurrentDateMillis());
        } else {
            organizationTypeGraphRepository.deleteSkillFromOrgType(orgTypeId, skillId, DateUtils.getCurrentDateMillis());
        }
        return organizationTypeGraphRepository.getSkillsOfOrganizationType(orgTypeId);
    }

    /**
     * to get skills for particular organization type
     *
     * @param orgTypeId
     * @return
     */
    public List<SkillCategoryQueryResults> getSkillsByOrganizationTypeId( long orgTypeId) {
        List<SkillCategoryQueryResults> skillCategoryQueryResultsList = organizationTypeGraphRepository.getSkillsOfOrganizationType(orgTypeId);
        skillCategoryQueryResultsList.forEach(skillCategoryQueryResults -> {
            skillCategoryQueryResults.setTranslations(TranslationUtil.getTranslatedData(skillCategoryQueryResults.getTranslatedNames(),skillCategoryQueryResults.getTranslatedDescriptions()));
            List<SkillDTO> skillDTOS = new ArrayList<>();
            if(isCollectionNotEmpty(skillCategoryQueryResults.getSkillList())){
                skillDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(skillCategoryQueryResults.getSkillList(), SkillDTO.class);
            }
            for(SkillDTO skillDTO :skillDTOS){
                skillDTO.setTranslations(TranslationUtil.getTranslatedData(skillDTO.getTranslatedNames(),skillDTO.getTranslatedDescriptions()));
            }
            skillCategoryQueryResults.setSkillList(skillDTOS);
        });
        return  skillCategoryQueryResultsList;
    }

    public OrganizationTypeHierarchyQueryResult getOrganizationTypeHierarchy(long countryId, Set<Long> orgSubServiceId) {
        return organizationTypeGraphRepository.getOrganizationTypeHierarchy(countryId, orgSubServiceId);
    }

    public List<Unit> getOrganizationByOrganizationTypeId(long organizationTypeId) {
        return organizationTypeGraphRepository.getOrganizationsByOrganizationType(organizationTypeId);
    }

    public void linkOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId) {
        organizationTypeGraphRepository.linkOrganizationTypeWithService(orgTypeId, serviceId);
    }

    public void deleteLinkingOfOrganizationTypeAndService(Set<Long> orgTypeId, long serviceId) {
        organizationTypeGraphRepository.deleteRelOrganizationTypeWithService(orgTypeId, serviceId);
    }


    public List<OrganizationTypeAndSubTypeDto> getAllOrganizationTypeAndServiceAndSubServices(Long countryId) {
        List<Map> organizationType = organizationTypeGraphRepository.getAllOrganizationTypeAndServiceAndSubServices(countryId);
        List<OrganizationTypeAndSubTypeDto> list = new ArrayList<>();
        organizationType.forEach(o -> list.add(ObjectMapperUtils.copyPropertiesByMapper(o.get("organizationType"), OrganizationTypeAndSubTypeDto.class)));
        return list;
    }

    public OrganizationTypeSubTypeAndServicesQueryResult getOrgTypesServicesAndSubServicesListByUnitId(Long unitId)
    {
      return organizationTypeGraphRepository.getOrganizationTypeSubTypesServiceAndSubServices(unitId);
    }

    public List<Long> getOrganizationIdsByOrgSubTypeIdsAndSubServiceIds(List<Long> organizationSubTypeIds, List<Long> organizationSubServicesIds) {
        return organizationTypeGraphRepository.getOrganizationIdsByOrgSubTypeIdsAndOrgSubServiceIds(organizationSubTypeIds, organizationSubServicesIds);
    }

    public Map<String, TranslationInfo> updateTranslation(Long orgTypeId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        OrganizationType organizationType =organizationTypeGraphRepository.findOne(orgTypeId);
        organizationType.setTranslatedNames(translatedNames);
        organizationType.setTranslatedDescriptions(translatedDescriptios);
        organizationTypeGraphRepository.save(organizationType);
        return organizationType.getTranslatedData();
    }


}
