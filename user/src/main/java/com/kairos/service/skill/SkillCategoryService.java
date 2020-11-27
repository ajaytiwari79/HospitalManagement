package com.kairos.service.skill;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.user.country.skill.SkillDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.model.user.skill.SkillCategoryQueryResults;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillCategoryGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_SKILLCATEGORY_NAME_DUPLICATE;

/**
 * SkillCategoryService
 */
@Service
@Transactional
public class SkillCategoryService {

    @Inject
    private SkillCategoryGraphRepository skillCategoryGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private SkillGraphRepository skillGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    public Object createSkillCategory(long countryId, SkillCategory skillCategory)  {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        String name = "(?i)"+skillCategory.getName();
        if (!skillCategoryGraphRepository.checkDuplicateSkillCategory(countryId,name).isEmpty()){
            exceptionService.duplicateDataException(MESSAGE_SKILLCATEGORY_NAME_DUPLICATE);

        }else {
            skillCategory.setCountry(country);
            skillCategoryGraphRepository.save(skillCategory);
            return skillCategory.retieveDetails();
        }

        return null;
    }


    public SkillCategory getSkillCategorybyId(Long id) {
        return skillCategoryGraphRepository.findOne(id);
    }


    public boolean deleteSkillCategorybyId(long skillCategory) {
        SkillCategory category = skillCategoryGraphRepository.findOne(skillCategory);
        if (category!=null){
            category.setEnabled(false);
            skillCategoryGraphRepository.save(category);
            return true;
        }
        return false;
    }


    /**
     * List all SkillCategory
     *
     * @return
     */
    public List<SkillCategory> getAllSkillCategory(){
        return  skillCategoryGraphRepository.findAll();
    }

    public Map<String, Object> updateSkillCategory(SkillCategory skillCategory) {

        if (skillCategory!=null){
            SkillCategory currentCategory= skillCategoryGraphRepository.findOne(skillCategory.getId());
            if (currentCategory!=null) {
                currentCategory.setName(skillCategory.getName());
                currentCategory.setDescription(skillCategory.getDescription());
                skillCategoryGraphRepository.save(currentCategory);


                Map<String, Object> response =skillCategory.retieveDetails();
                response.put("skills", skillCategoryGraphRepository.getThisCategorySkills(currentCategory.getId()));
                return response;
            }
        }
        return null;
    }


    public List<SkillCategoryQueryResults> getAllSkillCategoryOfCountryOrUnit(Long countryOrUnitId, boolean isCountry) {
         List<SkillCategoryQueryResults> skillCategoryQueryResultsList= isCountry ? skillCategoryGraphRepository.findSkillCategoryByCountryId(countryOrUnitId) : skillCategoryGraphRepository.findSkillCategoryByUnitId(countryOrUnitId);;
        skillCategoryQueryResultsList.forEach(skillCategoryQueryResults -> {
            List<SkillDTO> skillDTOS =new ArrayList<>();
            if(isCollectionNotEmpty(skillCategoryQueryResults.getSkillList())){
              skillDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(skillCategoryQueryResults.getSkillList(),SkillDTO.class);
            }
            for(SkillDTO skillDTO :skillDTOS){
                if(isCountry) {
                    skillDTO.setCountryId(countryOrUnitId);
                }
                skillDTO.setTranslations(TranslationUtil.getTranslatedData(skillDTO.getTranslatedNames(),skillDTO.getTranslatedDescriptions()));
            }
            skillCategoryQueryResults.setSkillList(skillDTOS);
            if(isCountry) {
                skillCategoryQueryResults.setCountryId(countryOrUnitId);
            }
            skillCategoryQueryResults.setTranslations(TranslationUtil.getTranslatedData(skillCategoryQueryResults.getTranslatedNames(),skillCategoryQueryResults.getTranslatedDescriptions()));
        });
        return skillCategoryQueryResultsList;
    }

    public Map<String, TranslationInfo> updateTranslation(Long id, Map<String,TranslationInfo> translationInfoMap) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translationInfoMap.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        SkillCategory skillCategory = skillCategoryGraphRepository.findOne(id);
        if(isNotNull(skillCategory)) {
            skillCategory.setTranslatedNames(translatedNames);
            skillCategory.setTranslatedDescriptions(translatedDescriptios);
            skillCategoryGraphRepository.save(skillCategory);
            return skillCategory.getTranslatedData();
        }else {
            Skill skill =skillGraphRepository.findOne(id);
            skill.setTranslatedNames(translatedNames);
            skill.setTranslatedDescriptions(translatedDescriptios);
            skillGraphRepository.save(skill);
            return skill.getTranslatedData();
        }
    }
}
