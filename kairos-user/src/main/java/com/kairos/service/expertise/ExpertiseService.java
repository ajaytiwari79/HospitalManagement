package com.kairos.service.expertise;

import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseDTO;
import com.kairos.persistence.model.user.expertise.ExpertiseSkillQueryResult;
import com.kairos.persistence.model.user.expertise.ExpertiseTagDTO;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.response.dto.web.experties.CountryExpertiseDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.tag.TagService;
import com.kairos.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 28/10/16.
 */
@Service
@Transactional
public class ExpertiseService extends UserBaseService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    StaffGraphRepository staffGraphRepository;
    @Inject
    TagService tagService;


    public Map<String, Object> saveExpertise(long countryId, CountryExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (country == null){
            return null;
        }
        Expertise expertise = new Expertise();
        expertise.setName(expertiseDTO.getName());
        expertise.setDescription(expertiseDTO.getDescription());
        expertise.setCountry(country);
        expertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
        save(expertise);
        return expertise.retrieveDetails();
    }

    public List<ExpertiseTagDTO> getAllExpertise(long countryId) {
        return expertiseGraphRepository.getAllExpertiseWithTagsByCountry(countryId);
    }


    public Map<String, Object> updateExpertise(CountryExpertiseDTO expertiseDTO) {
        Expertise currentExpertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
        if (currentExpertise == null) {
            return null;
        }
        currentExpertise.setName(expertiseDTO.getName());
        currentExpertise.setDescription(expertiseDTO.getDescription());
        currentExpertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
        save(currentExpertise);
        return currentExpertise.retrieveDetails();
    }

    public boolean deleteExpertise(long expertiseId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (expertise != null) {
            expertise.setEnabled(false);
            save(expertise);
            return true;
        }
        return false;
    }


    public Map<String,Object> setExpertiseToStaff( Long staffId, List<Long> expertiseIds) {
        Staff currentStaff= staffGraphRepository.findOne(staffId);
        currentStaff.setExpertise(expertiseGraphRepository.getExpertiseByIdsIn(expertiseIds));
        Staff staff = staffGraphRepository.save(currentStaff);
        return  staff.retrieveExpertiseDetails();
    }

    public Map<String, Object> getExpertiseToStaff(Long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff.getExpertise()==null){
            return null;
        }
        return staff.retrieveExpertiseDetails();
    }

    /**
     * @author prabjot
     * this method will update the relationship of expertise and skill based on parameter {isSelected},if parameter value is true
     * new relationship b/w expertise and skill will be created or updated(if relationship already exist) if parameter value is false
     * then relationship will be inactive (isEnabled param of relationship will set to false)
     * @param expertiseId
     * @param skillIds
     * @param isSelected
     */
    public void addSkillInExpertise(long expertiseId,List<Long> skillIds,boolean isSelected){

        if(isSelected){
            for(long skillId : skillIds){
                if(expertiseGraphRepository.expertiseHasAlreadySkill(expertiseId,skillId) == 0){
                    expertiseGraphRepository.addSkillInExpertise(expertiseId,skillId, DateUtil.getCurrentDate().getTime(),DateUtil.getCurrentDate().getTime());
                } else {
                    expertiseGraphRepository.updateExpertiseSkill(expertiseId,skillId,DateUtil.getCurrentDate().getTime());
                }
            }
        } else {
            expertiseGraphRepository.deleteExpertiseSkill(expertiseId,skillIds,DateUtil.getCurrentDate().getTime());
        }
    }

    /**
     * to get skills of expertise,data will be in form of tree hierarchy
     * @param expertiseId
     * @param countryId
     * @return
     */
    public List<Map<String,Object>> getExpertiseSkills(long expertiseId,long countryId){

        ExpertiseSkillQueryResult expertiseSkillQueryResult = expertiseGraphRepository.getExpertiseSkills(expertiseId,countryId);
        return expertiseSkillQueryResult.getSkills();
    }

    public List<ExpertiseDTO> getAllFreeExpertise(List<Long> expertiseIds){
        return expertiseGraphRepository.getAllFreeExpertises(expertiseIds);
    }

    public Expertise getExpertiseByCountryId(Long countryId){
        return expertiseGraphRepository.getExpertiesByCountry(countryId);
    }
}
