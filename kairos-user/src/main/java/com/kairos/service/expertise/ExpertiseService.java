package com.kairos.service.expertise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_table.PayTable;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelFunctionRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.response.dto.web.experties.*;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    @Inject
    private FunctionGraphRepository functionGraphRepository;

    @Inject
    private SeniorityLevelFunctionRelationshipGraphRepository seniorityLevelFunctionRelationshipGraphRepository;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;

    public ExpertiseResponseDTO saveExpertise(long countryId, CountryExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country Id");
        }
        ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
        Expertise expertise = null;

        if (!Optional.ofNullable(expertiseDTO.getId()).isPresent()) {
            expertise = new Expertise();
            expertise.setCountry(country);
            prepareExpertise(expertise, expertiseDTO, countryId);
            expertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());

        } else {
            // Expertise is already created only need to add Sr level
            expertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Invalid expertise Id");
            }
            if (expertise.isPublished()) {
                // Expertise is already published Now we need to maintain a tempCopy of it.

            } else {
                SeniorityLevel seniorityLevel = new SeniorityLevel();
                addNewSeniorityLevelInExpertise(expertise, seniorityLevel, expertiseDTO.getSeniorityLevel());
                save(expertise);
                expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
                expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
                expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());
            }
        }

        return expertiseResponseDTO;
    }

    public ExpertiseResponseDTO createCopyOfExpertise(Expertise expertise, CountryExpertiseDTO expertiseDTO, Long countryId) {
        ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
        expertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
        Expertise copiedExpertise = new Expertise();
        BeanUtils.copyProperties(expertise, copiedExpertise);
        copiedExpertise.setId(null);
        expertise.setHasDraftCopy(true);
        copiedExpertise.setExpertise(expertise);
        copiedExpertise.setSeniorityLevel(null);
        List<SeniorityLevelDTO> seniorityLevelDTOList = copySeniorityLevelInExpertise(copiedExpertise, expertise.getSeniorityLevel(), expertiseDTO);

        expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
        expertiseResponseDTO.setId(copiedExpertise.getId());
        expertiseResponseDTO.setPublished(false);
        expertiseResponseDTO.setSeniorityLevels(seniorityLevelDTOList);
        return expertiseResponseDTO;
    }


    /* Add previously added seniority level in current.
        This method is used to to copy sr level from and expertise and add in another expertise.
*/
    private List<SeniorityLevelDTO> copySeniorityLevelInExpertise(Expertise expertise, List<SeniorityLevel> seniorityLevels, CountryExpertiseDTO expertiseDTO) {
        List<SeniorityLevelDTO> seniorityLevelResponse = new ArrayList<>();
        //  Adding the currently added Sr level in expertise.
        SeniorityLevel seniorityLevel = new SeniorityLevel();

        addNewSeniorityLevelInExpertise(expertise, seniorityLevel, expertiseDTO.getSeniorityLevel());
        expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
        seniorityLevelResponse.add(expertiseDTO.getSeniorityLevel());


        List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();
        // Iterating on all object from DB and now copying to new Object
        for (SeniorityLevel seniorityLevelFromDB : seniorityLevels) {

            seniorityLevel = new SeniorityLevel();
            BeanUtils.copyProperties(seniorityLevelFromDB, seniorityLevel);
            seniorityLevel.setId(null);
            FunctionAndSeniorityLevelQueryResult functionAndSeniorityLevel = seniorityLevelGraphRepository.getFunctionAndPayGroupAreaBySeniorityLevelId(seniorityLevelFromDB.getId());

            // TODO java.lang.ClassCastException: java.util.Collections$UnmodifiableMap cannot be cast to com.kairos.response.dto.web.experties.FunctionsDTO

            if (Optional.ofNullable(functionAndSeniorityLevel.getFunctions()).isPresent() && !functionAndSeniorityLevel.getFunctions().isEmpty()) {

                for (Map<String, Object> currentObje : functionAndSeniorityLevel.getFunctions()) {
                    BigDecimal functionAmount = new BigDecimal(currentObje.get("amount").toString());
                    Long currentFunctionId = (Long) currentObje.get("functionId");
                    SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, new Function(currentFunctionId), functionAmount);
                    seniorityLevelFunctionsRelationships.add(functionsRelationship);
                }
            }

            if (Optional.ofNullable(functionAndSeniorityLevel.getPayGroupAreas()).isPresent() && !functionAndSeniorityLevel.getPayGroupAreas().isEmpty()) {
                seniorityLevel.setPayGroupAreas(functionAndSeniorityLevel.getPayGroupAreas());
            }

            if (seniorityLevelFromDB.getMoreThan() != null)
                seniorityLevel.setMoreThan(seniorityLevelFromDB.getMoreThan());
            else {
                seniorityLevel.setFrom(seniorityLevelFromDB.getFrom());
                seniorityLevel.setTo(seniorityLevelFromDB.getTo());
            }
            seniorityLevel.setBasePayGrade(seniorityLevelFromDB.getBasePayGrade());
            seniorityLevel.setPensionPercentage(seniorityLevelFromDB.getPensionPercentage());
            seniorityLevel.setFreeChoicePercentage(seniorityLevelFromDB.getFreeChoicePercentage());
            seniorityLevel.setFreeChoiceToPension(seniorityLevelFromDB.getFreeChoiceToPension());

            expertise.getSeniorityLevel().add(seniorityLevel);
            seniorityLevelResponse.add(getSeniorityLevelResponse(seniorityLevel, functionAndSeniorityLevel));

        }
        save(expertise);
        seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);


        return seniorityLevelResponse;

    }

    /*This method is responsible for generating SL response */
    private SeniorityLevelDTO getSeniorityLevelResponse(SeniorityLevel seniorityLevel, FunctionAndSeniorityLevelQueryResult functionAndSeniorityLevel) {
        SeniorityLevelDTO seniorityLevelDTO = new SeniorityLevelDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevelDTO = objectMapper.convertValue(seniorityLevel, SeniorityLevelDTO.class);
        if (Optional.ofNullable(functionAndSeniorityLevel.getPayGroupAreas()).isPresent() && !functionAndSeniorityLevel.getPayGroupAreas().isEmpty()) {
            Set<Long> payGroupAreasId = functionAndSeniorityLevel.getPayGroupAreas().stream().map(PayGroupArea::getId).collect(Collectors.toSet());
            seniorityLevelDTO.setPayGroupAreasIds(payGroupAreasId);
        }

        if (Optional.ofNullable(functionAndSeniorityLevel.getFunctions()).isPresent() && !functionAndSeniorityLevel.getFunctions().isEmpty()) {
            List<FunctionsDTO> allFunctions = new ArrayList<>();
            for (Map<String, Object> currentObje : functionAndSeniorityLevel.getFunctions()) {
                BigDecimal functionAmount = new BigDecimal(currentObje.get("amount").toString());
                Long currentFunctionId = (Long) currentObje.get("functionId");
                FunctionsDTO function = new FunctionsDTO(functionAmount, currentFunctionId);
                allFunctions.add(function);
            }
            seniorityLevelDTO.setFunctions(allFunctions);
        }
        return seniorityLevelDTO;
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
        SeniorityLevel seniorityLevel = null;
        if (expertiseDTO.getSeniorityLevel() != null) {
            seniorityLevel = new SeniorityLevel();
            seniorityLevel = addNewSeniorityLevelInExpertise(expertise, seniorityLevel, expertiseDTO.getSeniorityLevel());

        }
        save(expertise);
        expertiseDTO.setId(expertise.getId());
        expertiseDTO.setPublished(expertise.isPublished());
        if (expertiseDTO.getSeniorityLevel() != null) {
            expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
        }

    }

    private SeniorityLevel addNewSeniorityLevelInExpertise(Expertise expertise, SeniorityLevel seniorityLevel, SeniorityLevelDTO seniorityLevelDTO) {
        List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();
        if (Optional.ofNullable(seniorityLevelDTO.getFunctions()).isPresent() && !seniorityLevelDTO.getFunctions().isEmpty()) {
            Set<Long> functionIds = seniorityLevelDTO.getFunctions().stream().map(FunctionsDTO::getFunctionId).collect(Collectors.toSet());
            List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
            if (functions.size() != functionIds.size()) {
                throw new ActionNotPermittedException("unable to get all functions");
            }
            for (FunctionsDTO functionDTO : seniorityLevelDTO.getFunctions()) {
                Function currentFunction = functions.stream().filter(f -> f.getId().equals(functionDTO.getFunctionId())).findFirst().get();
                SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, currentFunction, functionDTO.getAmount());
                seniorityLevelFunctionsRelationships.add(functionsRelationship);
            }
        }
        if (Optional.ofNullable(seniorityLevelDTO.getPayGroupAreasIds()).isPresent() && !seniorityLevelDTO.getPayGroupAreasIds().isEmpty()) {
            List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(seniorityLevelDTO.getPayGroupAreasIds());
            if (payGroupAreas.size() != seniorityLevelDTO.getPayGroupAreasIds().size())
                throw new ActionNotPermittedException("Unable to get all payGroup Areas");
            seniorityLevel.setPayGroupAreas(payGroupAreas);
        }
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
        if (!Optional.ofNullable(expertise.getSeniorityLevel()).isPresent()) {
            List<SeniorityLevel> seniorityLevels = new ArrayList<>(1);
            seniorityLevels.add(seniorityLevel);
            expertise.setSeniorityLevel(seniorityLevels);
        } else {
            expertise.getSeniorityLevel().add(seniorityLevel);
        }
        seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
        return seniorityLevel;
    }


    public List<ExpertiseQueryResult> getAllExpertise(long countryId) {
        return expertiseGraphRepository.getAllExpertiseByCountryId(countryId);
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
