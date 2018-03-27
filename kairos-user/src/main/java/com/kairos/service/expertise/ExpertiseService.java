package com.kairos.service.expertise;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_table.PayGrade;
import com.kairos.persistence.model.user.pay_table.PayTable;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.response.dto.web.experties.CountryExpertiseDTO;
import com.kairos.response.dto.web.experties.SeniorityLevelDTO;
import com.kairos.response.dto.web.experties.UnionServiceWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by prabjot on 28/10/16.
 */
@Service
@Transactional
public class ExpertiseService extends UserBaseService {

    @Inject
    CountryGraphRepository countryGraphRepository;
    @Inject
    ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    TagService tagService;
    @Inject
    StaffGraphRepository staffGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    OrganizationServiceService organizationService;
    @Inject
    OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;


    public Map<String, Object> saveExpertise(long countryId, CountryExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country Id");
        }
        Expertise expertise = null;
        if (!Optional.ofNullable(expertiseDTO.getId()).isPresent()) {
            expertise = new Expertise();
            expertise.setCountry(country);
            prepareExpertise(expertise, expertiseDTO, countryId);
            expertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
        } else {
            // Expertise is already created only need to add Sr level
            expertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Invalid expertise Id");
            }
            addSeniorityLevelInExpertise(expertise, expertiseDTO.getSeniorityLevel());
        }


        save(expertise);
        return expertise.retrieveDetails();
    }

    private void prepareExpertise(Expertise expertise, CountryExpertiseDTO expertiseDTO, Long countryId) {
        expertise.setName(expertiseDTO.getName().trim());
        expertise.setDescription(expertiseDTO.getDescription());
        expertise.setStartDateMillis(expertiseDTO.getStartDateMillis());
        expertise.setEndDateMillis(expertiseDTO.getEndDateMillis());
        Level level = countryGraphRepository.getLevel(countryId, expertiseDTO.getOrganizationLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level id " + expertiseDTO.getOrganizationLevelId());
        }
        expertise.setOrganizationLevel(level);

        OrganizationService organizationService = organizationServiceRepository.findOne(expertiseDTO.getServiceId());
        if (!Optional.ofNullable(organizationService).isPresent()) {
            throw new DataNotFoundByIdException("Invalid service id " + expertiseDTO.getServiceId());
        }
        expertise.setOrganizationService(organizationService);
        Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(expertiseDTO.getUnionId());
        if (!Optional.ofNullable(union).isPresent()) {
            throw new DataNotFoundByIdException("Invalid union id " + expertiseDTO.getUnionId());
        }
        expertise.setUnion(union);
        expertise.setFullTimeWeeklyMinutes(expertiseDTO.getFullTimeWeeklyMinutes());
        expertise.setNumberOfWorkingDaysInWeek(expertiseDTO.getNumberOfWorkingDaysInWeek());
        PayTable payTable = payTableGraphRepository.findOne(expertiseDTO.getPayTableId());
        if (!Optional.ofNullable(payTable).isPresent()) {
            throw new DataNotFoundByIdException("Invalid pay Table id " + expertiseDTO.getPayTableId());
        }
        expertise.setPayTable(payTable);
        expertise.setPaidOutFrequency(expertiseDTO.getPaidOutFrequency());
        if (expertiseDTO.getSeniorityLevel() != null) {
            addSeniorityLevelInExpertise(expertise, expertiseDTO.getSeniorityLevel());
        }

    }

    private void addSeniorityLevelInExpertise(Expertise expertise, SeniorityLevelDTO seniorityLevelDTO) {
        SeniorityLevel seniorityLevel = new SeniorityLevel();
        if (seniorityLevelDTO.getMoreThan() != null)
            seniorityLevel.setMoreThan(seniorityLevelDTO.getMoreThan());
        else {
            seniorityLevel.setFrom(seniorityLevelDTO.getFrom());
            seniorityLevel.setTo(seniorityLevelDTO.getTo());
        }
        seniorityLevel.setBasePayGrade(seniorityLevelDTO.getBasePayGrade());
        seniorityLevel.setPensionPercentage(seniorityLevelDTO.getPensionPercentage());
        seniorityLevel.setFreeChoicePercentage(seniorityLevelDTO.getFreeChoicePercentage());
        seniorityLevel.setFreeChoiceToPension(seniorityLevelDTO.getFreeChoiceToPension());
        if (!seniorityLevelDTO.getPayGroupAreas().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(seniorityLevelDTO.getPayGroupAreas());
            if (payGroupAreas.size() != seniorityLevelDTO.getPayGroupAreas().size())
                throw new ActionNotPermittedException("Unable to get all payGroup Areas");
            seniorityLevel.setPayGroupAreas(payGroupAreas);
        }
        if (!Optional.ofNullable(expertise.getSeniorityLevel()).isPresent()) {
            List<SeniorityLevel> seniorityLevels = new ArrayList<>(1);
            seniorityLevels.add(seniorityLevel);
            expertise.setSeniorityLevel(seniorityLevels);
        } else {
            expertise.getSeniorityLevel().add(seniorityLevel);
        }


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


    public Map<String, Object> setExpertiseToStaff(Long staffId, Long expertiseId) {
        Staff currentStaff = staffGraphRepository.findOne(staffId);
        currentStaff.setExpertise(expertiseGraphRepository.findOne(expertiseId));
        Staff staff = staffGraphRepository.save(currentStaff);
        return staff.retrieveExpertiseDetails();
    }

    public Map<String, Object> getExpertiseToStaff(Long staffId) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (staff.getExpertise() == null) {
            return null;
        }
        return staff.retrieveExpertiseDetails();
    }

    /**
     * @param expertiseId
     * @param skillIds
     * @param isSelected
     * @author prabjot
     * this method will update the relationship of expertise and skill based on parameter {isSelected},if parameter value is true
     * new relationship b/w expertise and skill will be created or updated(if relationship already exist) if parameter value is false
     * then relationship will be inactive (isEnabled param of relationship will set to false)
     */
    public void addSkillInExpertise(long expertiseId, List<Long> skillIds, boolean isSelected) {

        if (isSelected) {
            for (long skillId : skillIds) {
                if (expertiseGraphRepository.expertiseHasAlreadySkill(expertiseId, skillId) == 0) {
                    expertiseGraphRepository.addSkillInExpertise(expertiseId, skillId, DateUtil.getCurrentDate().getTime(), DateUtil.getCurrentDate().getTime());
                } else {
                    expertiseGraphRepository.updateExpertiseSkill(expertiseId, skillId, DateUtil.getCurrentDate().getTime());
                }
            }
        } else {
            expertiseGraphRepository.deleteExpertiseSkill(expertiseId, skillIds, DateUtil.getCurrentDate().getTime());
        }
    }

    /**
     * to get skills of expertise,data will be in form of tree hierarchy
     *
     * @param expertiseId
     * @param countryId
     * @return
     */
    public List<Map<String, Object>> getExpertiseSkills(long expertiseId, long countryId) {

        ExpertiseSkillQueryResult expertiseSkillQueryResult = expertiseGraphRepository.getExpertiseSkills(expertiseId, countryId);
        return expertiseSkillQueryResult.getSkills();
    }

    public List<ExpertiseDTO> getAllFreeExpertise(List<Long> expertiseIds) {
        return expertiseGraphRepository.getAllFreeExpertises(expertiseIds);
    }

    public Expertise getExpertiseByCountryId(Long countryId) {
        return expertiseGraphRepository.getExpertiesByCountry(countryId);
    }

    public UnionServiceWrapper getUnionsAndService(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country Id");
        }
        UnionServiceWrapper unionServiceWrapper = new UnionServiceWrapper();
        unionServiceWrapper.setServices(organizationService.getAllOrganizationService(countryId));
        unionServiceWrapper.setUnions(organizationGraphRepository.findAllUnionsByCountryId(countryId));
        unionServiceWrapper.setOrganizationLevels(countryGraphRepository.getLevelsByCountry(countryId));
        return unionServiceWrapper;
    }
}
