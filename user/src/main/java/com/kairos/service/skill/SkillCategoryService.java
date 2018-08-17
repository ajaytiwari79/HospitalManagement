package com.kairos.service.skill;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.skill.SkillCategory;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.skill.SkillCategoryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SkillCategoryService
 */
@Service
@Transactional
public class SkillCategoryService {

    @Inject
    SkillCategoryGraphRepository skillCategoryGraphRepository;

    @Inject
    CountryGraphRepository countryGraphRepository;

    @Inject
    private ExceptionService exceptionService;
    public Object createSkillCategory(long countryId, SkillCategory skillCategory)  {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null) {
            return null;
        }
        String name = "(?i)"+skillCategory.getName();
        if (!skillCategoryGraphRepository.checkDuplicateSkillCategory(countryId,name).isEmpty()){
            exceptionService.duplicateDataException("message.skillcategory.name.duplicate");

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
//        String[] types = skillCategoryGraphRepository.findAll();
//        logger.info("\n"+"Categories found: "+types[0]+types[1]+"\n");
//        List<SkillWrapper> skillWrappers = new ArrayList<>();
//        for (String type:
//             types) {
//         SkillWrapper skillWrapper = new SkillWrapper(type,skillCategoryGraphRepository.getThisCategorySkills(type));
//            skillWrappers.add(skillWrapper);
//
//        }
        return  skillCategoryGraphRepository.findAll();
    }

    public Map<String, Object> updateSkillCategory(SkillCategory skillCategory, Long countryId) {

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


    public List<Object> getAllSkillCategoryOfCountry(Long countryId) {
        List<Object> objectList = new ArrayList<>();
         List<Map<String,Object>> mapList = skillCategoryGraphRepository.findSkillCategoryByCountryId(countryId);
        for(Map<String,Object> map :  mapList){
            Object o = map.get("result");
            objectList.add(o);
        }
        return objectList;
    }


    public List<Object> findSkillCategoryByUnitId(Long unitId) {
        List<Object> objectList = new ArrayList<>();
        List<Map<String,Object>> mapList = skillCategoryGraphRepository.findSkillCategoryByUnitId(unitId);
        for(Map<String,Object> map :  mapList){
            Object o = map.get("result");
            objectList.add(o);
        }
        return objectList;
    }
}
