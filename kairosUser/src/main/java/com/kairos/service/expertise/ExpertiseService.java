package com.kairos.service.expertise;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_table.PayGrade;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.model.user.staff.StaffExpertiseRelationShip;

import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;

import com.kairos.persistence.repository.user.expertise.SeniorityLevelFunctionRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayTableGraphRepository;

import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
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
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.FULL_TIME_WEEKLY_MINUTES;
import static com.kairos.constants.AppConstants.NUMBER_OF_WORKING_DAYS_IN_WEEK;
import static javax.management.timer.Timer.ONE_DAY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    StaffGraphRepository staffGraphRepository;
    @Inject
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    OrganizationServiceRepository organizationServiceRepository;
    @Inject
    OrganizationServiceService organizationServiceService;
    @Inject
    private PayTableGraphRepository payTableGraphRepository;
    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    TagService tagService;
    @Inject
    StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private SeniorityLevelFunctionRelationshipGraphRepository seniorityLevelFunctionRelationshipGraphRepository;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;

    public ExpertiseResponseDTO saveExpertise(long countryId, CountryExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country Id");
        }
        ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();


        Expertise expertise = null;

        if (!Optional.ofNullable(expertiseDTO.getId()).isPresent()) {
            boolean isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(expertiseDTO.getOrganizationLevelId(), "(?i)" + expertiseDTO.getName().trim(), -1L);
            if (isExpertiseExists) {
                throw new DuplicateDataException("Already a expertise is available with same name");
            }
            expertise = new Expertise();
            expertise.setCountry(country);
            prepareExpertiseWhileCreate(expertise, expertiseDTO, countryId);
            expertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.setFullTimeWeeklyMinutes(expertise.getFullTimeWeeklyMinutes());
            expertiseResponseDTO.setNumberOfWorkingDaysInWeek(expertise.getNumberOfWorkingDaysInWeek());
            expertiseResponseDTO.setEditable(expertise.isHistory());
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());

        } else {
            // Expertise is already created only need to add Sr level
            expertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Invalid expertise Id");
            }
            validateSeniorityLevel(expertise.getSeniorityLevel(), expertiseDTO.getSeniorityLevel(), -1L);
            if (expertise.isPublished()) {
                expertiseResponseDTO = createCopyOfExpertise(expertise, expertiseDTO, countryId);
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


    private void validateSeniorityLevel(List<SeniorityLevel> seniorityLevels, SeniorityLevelDTO seniorityLevelDTO, Long currentSeniorityLevelId) {
        Collections.sort(seniorityLevels);
        for (int i = 0; i < seniorityLevels.size(); i++) {
            if (!seniorityLevels.get(i).getId().equals(currentSeniorityLevelId)) { // we are skipping the current
                if (seniorityLevelDTO.getTo() == null && seniorityLevels.get(i).getTo() == null) {
                    throw new ActionNotPermittedException("Already a more than is present in seniority level " + seniorityLevels.get(i).getId());
                } else if (seniorityLevelDTO.getTo() == null && seniorityLevels.get(i).getTo() != null) {
                    if (seniorityLevelDTO.getFrom() > seniorityLevels.get(i).getTo()) {
                        throw new ActionNotPermittedException("The start must be greater than " + seniorityLevels.get(i).getTo() + " " + seniorityLevels.get(i).getId());
                    }
                    break;
                } else if (seniorityLevelDTO.getTo() != null && seniorityLevels.get(i).getTo() == null) {
                    if (seniorityLevelDTO.getTo() > seniorityLevels.get(i).getFrom()) {
                        throw new ActionNotPermittedException("The end must be less than " + seniorityLevels.get(i).getFrom() + " " + seniorityLevels.get(i).getId());
                    }

                } else {
                    if (seniorityLevelDTO.getFrom() < seniorityLevels.get(i).getFrom() && !(seniorityLevelDTO.getTo() <= seniorityLevels.get(i).getFrom())) {
                        throw new ActionNotPermittedException("Already a Sr level is present 1:" + seniorityLevels.get(i).getId());
                    } else if (seniorityLevelDTO.getFrom() > seniorityLevels.get(i).getFrom() && !(seniorityLevelDTO.getFrom() >= seniorityLevels.get(i).getTo())) {
                        throw new ActionNotPermittedException("Already a Sr level is present 2:" + seniorityLevels.get(i).getId());
                    } else if (seniorityLevelDTO.getFrom() == seniorityLevels.get(i).getFrom() || (seniorityLevelDTO.getTo() == seniorityLevels.get(i).getTo())) {
                        throw new ActionNotPermittedException("Same Seniority level already exists");
                    }
                }
            }
        }

    }

    public ExpertiseResponseDTO createCopyOfExpertise(Expertise expertise, CountryExpertiseDTO expertiseDTO, Long countryId) {

        ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();

        Expertise copiedExpertise = new Expertise();
        BeanUtils.copyProperties(expertise, copiedExpertise);
        copiedExpertise.setId(null);
        expertise.setHasDraftCopy(true);
        expertise.setHistory(true);
        copiedExpertise.setHistory(false);
        copiedExpertise.setPublished(false);
        copiedExpertise.setParentExpertise(expertise);
        copiedExpertise.setSeniorityLevel(null);

        List<SeniorityLevelDTO> seniorityLevelDTOList = new ArrayList<>();
        //  Adding the currently added Sr level in expertise.
        SeniorityLevel seniorityLevel = new SeniorityLevel();
        addNewSeniorityLevelInExpertise(copiedExpertise, seniorityLevel, expertiseDTO.getSeniorityLevel());
        expertiseDTO.getSeniorityLevel().setParentId(expertiseDTO.getSeniorityLevel().getId());
        expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
        seniorityLevelDTOList.add(expertiseDTO.getSeniorityLevel());


        seniorityLevelDTOList.addAll(copyExistingSeniorityLevelInExpertise(copiedExpertise, expertise.getSeniorityLevel(), expertiseDTO.getSeniorityLevel().getParentId()));

        expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
        expertiseResponseDTO.setId(copiedExpertise.getId());
        expertiseResponseDTO.setPublished(false);
        expertiseResponseDTO.setEditable(true);
        // setting previous Id as parent id
        expertiseResponseDTO.setParentId(expertise.getId());
        expertiseResponseDTO.setSeniorityLevels(seniorityLevelDTOList);
        return expertiseResponseDTO;
    }


    /* Add previously added seniority level in current.
        This method is used to to copy sr level from and expertise and add in another expertise.
        Third parameter is optional if you wish to exclude any seniority level while  copy
    */
    private List<SeniorityLevelDTO> copyExistingSeniorityLevelInExpertise(Expertise expertise, List<SeniorityLevel> seniorityLevels, Long seniorityLevelToSkip) {
        List<SeniorityLevelDTO> seniorityLevelResponse = new ArrayList<>();
        // Iterating on all object from DB and now copying to new Object
        for (SeniorityLevel seniorityLevelFromDB : seniorityLevels) {
            if (!seniorityLevelFromDB.getId().equals(seniorityLevelToSkip)) {
                List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList<>();

                SeniorityLevel seniorityLevel = new SeniorityLevel();
                BeanUtils.copyProperties(seniorityLevelFromDB, seniorityLevel);
                seniorityLevel.setId(null);
                FunctionAndSeniorityLevelQueryResult functionAndSeniorityLevel = seniorityLevelGraphRepository.getFunctionAndPayGroupAreaBySeniorityLevelId(seniorityLevelFromDB.getId());

                // TODO java.lang.ClassCastException: java.util.Collections$UnmodifiableMap cannot be cast to FunctionsDTO

                if (Optional.ofNullable(functionAndSeniorityLevel.getFunctions()).isPresent() && !functionAndSeniorityLevel.getFunctions().isEmpty()) {
                    for (Map<String, Object> currentObject : functionAndSeniorityLevel.getFunctions()) {
                        BigDecimal functionAmount = new BigDecimal(currentObject.get("amount").toString());
                        Function currentFunction = new Function();
                        convertToFunctionObjectFromMap(currentFunction, currentObject);
                        SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, currentFunction, functionAmount);
                        seniorityLevelFunctionsRelationships.add(functionsRelationship);
                    }
                }

                if (Optional.ofNullable(functionAndSeniorityLevel.getPayGroupAreas()).isPresent() && !functionAndSeniorityLevel.getPayGroupAreas().isEmpty()) {
                    seniorityLevel.setPayGroupAreas(functionAndSeniorityLevel.getPayGroupAreas());
                }
                if (Optional.ofNullable(functionAndSeniorityLevel.getPayGrade()).isPresent()) {
                    seniorityLevel.setPayGrade(functionAndSeniorityLevel.getPayGrade());
                }

                seniorityLevel.setFrom(seniorityLevelFromDB.getFrom());
                seniorityLevel.setTo(seniorityLevelFromDB.getTo());


                seniorityLevel.setPensionPercentage(seniorityLevelFromDB.getPensionPercentage());
                seniorityLevel.setFreeChoicePercentage(seniorityLevelFromDB.getFreeChoicePercentage());
                seniorityLevel.setFreeChoiceToPension(seniorityLevelFromDB.getFreeChoiceToPension());
                save(seniorityLevel);
                expertise.getSeniorityLevel().add(seniorityLevel);
                seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
                seniorityLevelResponse.add(getSeniorityLevelResponse(seniorityLevelFromDB, seniorityLevel, functionAndSeniorityLevel));

            }
        }
        save(expertise);
        return seniorityLevelResponse;

    }

    private void convertToFunctionObjectFromMap(Function currentFunction, Map<String, Object> currentObject) {
        currentFunction.setName(currentObject.get("name").toString());
        currentFunction.setDescription(currentObject.get("description").toString());
        Long startDateMillis = new Long(currentObject.get("startDate").toString());
        currentFunction.setStartDate(new Date(startDateMillis));
        Long endDateMillis = (currentObject.get("endDate") != null) ? new Long(currentObject.get("endDate").toString()) : null;
        currentFunction.setEndDate((endDateMillis != null) ? new Date(endDateMillis) : null);
        currentFunction.setId((Long) currentObject.get("functionId")); // setting the ID so that no new NODE is created

    }

    /*This method is responsible for generating Seniority Level response */
    private SeniorityLevelDTO getSeniorityLevelResponse(SeniorityLevel seniorityLevelFromDB, SeniorityLevel seniorityLevel, FunctionAndSeniorityLevelQueryResult functionAndSeniorityLevel) {
        SeniorityLevelDTO seniorityLevelDTO = new SeniorityLevelDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevelDTO = objectMapper.convertValue(seniorityLevel, SeniorityLevelDTO.class);
        seniorityLevelDTO.setParentId(seniorityLevelFromDB.getId());
        if (Optional.ofNullable(functionAndSeniorityLevel.getPayGroupAreas()).isPresent() && !functionAndSeniorityLevel.getPayGroupAreas().isEmpty()) {
            Set<Long> payGroupAreasId = functionAndSeniorityLevel.getPayGroupAreas().stream().map(PayGroupArea::getId).collect(Collectors.toSet());
            seniorityLevelDTO.setPayGroupAreasIds(payGroupAreasId);
        }

        if (Optional.ofNullable(functionAndSeniorityLevel.getFunctions()).isPresent() && !functionAndSeniorityLevel.getFunctions().isEmpty()) {
            List<FunctionsDTO> allFunctions = new ArrayList<>();
            for (Map<String, Object> currentFunction : functionAndSeniorityLevel.getFunctions()) {
                BigDecimal functionAmount = new BigDecimal(currentFunction.get("amount").toString());
                Long currentFunctionId = (Long) currentFunction.get("functionId");
                FunctionsDTO function = new FunctionsDTO(functionAmount, currentFunctionId);
                allFunctions.add(function);
            }
            seniorityLevelDTO.setFunctions(allFunctions);
        }
        return seniorityLevelDTO;
    }


    private void prepareExpertiseWhileCreate(Expertise expertise, CountryExpertiseDTO expertiseDTO, Long countryId) {
        expertise.setName(expertiseDTO.getName().trim());
        expertise.setDescription(expertiseDTO.getDescription());
        expertise.setStartDateMillis(expertiseDTO.getStartDateMillis());
        expertise.setEndDateMillis(expertiseDTO.getEndDateMillis());
        Level level = countryGraphRepository.getLevel(countryId, expertiseDTO.getOrganizationLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level id " + expertiseDTO.getOrganizationLevelId());
        }
        expertise.setOrganizationLevel(level);
        Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
        if (!Optional.ofNullable(organizationService).isPresent() || organizationService.size() != expertiseDTO.getOrganizationServiceIds().size()) {
            throw new DataNotFoundByIdException("All services are not found");
        }
        expertise.setOrganizationServices(organizationService);
        Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(expertiseDTO.getUnionId());
        if (!Optional.ofNullable(union).isPresent()) {
            throw new DataNotFoundByIdException("Invalid union id " + expertiseDTO.getUnionId());
        }
        expertise.setUnion(union);
        expertise.setFullTimeWeeklyMinutes(expertiseDTO.getFullTimeWeeklyMinutes() != null ? expertiseDTO.getFullTimeWeeklyMinutes() : FULL_TIME_WEEKLY_MINUTES);
        expertise.setNumberOfWorkingDaysInWeek(expertiseDTO.getNumberOfWorkingDaysInWeek() != null ? expertiseDTO.getNumberOfWorkingDaysInWeek() : NUMBER_OF_WORKING_DAYS_IN_WEEK);


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
        seniorityLevel.setFrom(seniorityLevelDTO.getFrom());
        seniorityLevel.setTo(seniorityLevelDTO.getTo());
        PayGrade payGrade = payGradeGraphRepository.findOne(seniorityLevelDTO.getPayGradeId());
        if (!Optional.ofNullable(payGrade).isPresent() || payGrade.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid pay grade id " + seniorityLevelDTO.getPayGradeId());
        }
        seniorityLevel.setPayGrade(payGrade);

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
        save(seniorityLevel);
        seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
        return seniorityLevel;
    }


    public List<ExpertiseQueryResult> getAllExpertise(long countryId, String selectedDate) throws ParseException {
        Long selectedDateInLong = (selectedDate != null) ? DateUtil.getIsoDateInLong(selectedDate) : DateUtil.getCurrentDateMillis();
        return expertiseGraphRepository.getAllExpertiseByCountryId(countryId, selectedDateInLong);
    }


    public ExpertiseResponseDTO updateExpertise(Long countryId, ExpertiseUpdateDTO expertiseDTO) {
        Expertise currentExpertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
        if (!Optional.ofNullable(currentExpertise).isPresent() || currentExpertise.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid expertise Id");
        }
        if (!currentExpertise.getName().equalsIgnoreCase(expertiseDTO.getName().trim())) {
            boolean isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(expertiseDTO.getOrganizationLevelId(), "(?i)" + expertiseDTO.getName().trim(), expertiseDTO.getId());
            if (isExpertiseExists) {
                throw new DuplicateDataException("Already a expertise is available with same name");
            }
        }

        Optional<SeniorityLevel> seniorityLevelToUpdate =
                currentExpertise.getSeniorityLevel().stream().filter(seniorityLevel -> seniorityLevel.getId().equals(expertiseDTO.getSeniorityLevel().getId())).findFirst();

        if (!seniorityLevelToUpdate.isPresent()) {
            throw new DataNotFoundByIdException("Seniority Level not found in expertise" + expertiseDTO.getSeniorityLevel().getId());
        }


        ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();
        if (currentExpertise.isPublished()) {

            // current is published now we need to create a copy and update in that and return the updated copy

            Expertise copiedExpertise = new Expertise();
            BeanUtils.copyProperties(currentExpertise, copiedExpertise);
            copiedExpertise.setId(null);

            currentExpertise.setHasDraftCopy(true);
            currentExpertise.setHistory(true);
            copiedExpertise.setPublished(false);
            copiedExpertise.setParentExpertise(currentExpertise);
            // copiedExpertise.getSeniorityLevel().clear();
            // Calling this function to get any updates or updated value from DTO.
            updateCurrentExpertise(countryId, copiedExpertise, expertiseDTO);

            List<SeniorityLevelDTO> seniorityLevelDTOList = new ArrayList<>();
            //  Adding the currently edited  Sr level in expertise.
            SeniorityLevel seniorityLevel = new SeniorityLevel();
            copiedExpertise.setSeniorityLevel(null);

            addNewSeniorityLevelInExpertise(copiedExpertise, seniorityLevel, expertiseDTO.getSeniorityLevel());
            expertiseDTO.getSeniorityLevel().setParentId(expertiseDTO.getSeniorityLevel().getId());

            expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
            seniorityLevelDTOList.add(expertiseDTO.getSeniorityLevel());


            // NOW WE need to add the other seniority level which exists in expertise
            // since we have already
            seniorityLevelDTOList.addAll(copyExistingSeniorityLevelInExpertise(copiedExpertise, currentExpertise.getSeniorityLevel(), seniorityLevelToUpdate.get().getId()));

            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.setPublished(false);
            expertiseResponseDTO.setEditable(true);
            expertiseResponseDTO.setId(copiedExpertise.getId());
            expertiseResponseDTO.setParentId(currentExpertise.getId());
            expertiseResponseDTO.setSeniorityLevels(seniorityLevelDTOList);


        } else {
            // update in current expertise :)
            validateSeniorityLevel(currentExpertise.getSeniorityLevel(), expertiseDTO.getSeniorityLevel(), expertiseDTO.getSeniorityLevel().getId());
            updateCurrentSeniorityLevel(expertiseDTO.getSeniorityLevel(), seniorityLevelToUpdate.get());
            updateCurrentExpertise(countryId, currentExpertise, expertiseDTO);
            save(currentExpertise);
            expertiseDTO.setId(currentExpertise.getId());
            expertiseDTO.setPublished(currentExpertise.isPublished());

            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());


        }
        return expertiseResponseDTO;
    }

    private void updateCurrentSeniorityLevel(SeniorityLevelDTO seniorityLevelDTO, SeniorityLevel seniorityLevel) {

        FunctionAndSeniorityLevelQueryResult functionAndSeniorityLevel = seniorityLevelGraphRepository.getFunctionAndPayGroupAreaBySeniorityLevelId(seniorityLevel.getId());
        if (Optional.ofNullable(seniorityLevelDTO.getFunctions()).isPresent()) {
            List<SeniorityLevelFunctionsRelationship> seniorityLevelFunctionsRelationships = new ArrayList();
            seniorityLevelGraphRepository.removeAllPreviousFunctionsFromSeniorityLevel(seniorityLevelDTO.getId());
            Set<Long> functionIds = seniorityLevelDTO.getFunctions().stream().map(FunctionsDTO::getFunctionId).collect(Collectors.toSet());
            List<Function> functions = functionGraphRepository.findAllFunctionsById(functionIds);
            for (FunctionsDTO functionDTO : seniorityLevelDTO.getFunctions()) {
                Function currentFunction = functions.stream().filter(f -> f.getId().equals(functionDTO.getFunctionId())).findFirst().get();
                SeniorityLevelFunctionsRelationship functionsRelationship = new SeniorityLevelFunctionsRelationship(seniorityLevel, currentFunction, functionDTO.getAmount());
                seniorityLevelFunctionsRelationships.add(functionsRelationship);
            }
            seniorityLevelFunctionRelationshipGraphRepository.saveAll(seniorityLevelFunctionsRelationships);
        }

        if (Optional.ofNullable(seniorityLevelDTO.getPayGroupAreasIds()).isPresent()) {
            Set<Long> previousPayGroups = functionAndSeniorityLevel.getPayGroupAreas() != null ? functionAndSeniorityLevel.getPayGroupAreas().stream().map(PayGroupArea::getId).collect(Collectors.toSet()) : Collections.emptySet();
            if (seniorityLevelDTO.getPayGroupAreasIds().isEmpty()) {
                // user removed all PayGroupAreas
                if (!previousPayGroups.isEmpty())
                    seniorityLevelGraphRepository.removeAllPreviousPayGroupAreaFromSeniorityLevel(seniorityLevelDTO.getId());
            } else {
                if (!seniorityLevelDTO.getPayGroupAreasIds().equals(previousPayGroups)) {
                    seniorityLevelGraphRepository.removeAllPreviousPayGroupAreaFromSeniorityLevel(seniorityLevelDTO.getId());
                    List<PayGroupArea> payGroupAreas = payGroupAreaGraphRepository.findAllById(seniorityLevelDTO.getPayGroupAreasIds());
                    if (payGroupAreas.size() != seniorityLevelDTO.getPayGroupAreasIds().size())
                        throw new ActionNotPermittedException("Unable to get all payGroup Areas");
                    seniorityLevel.setPayGroupAreas(payGroupAreas);
                }

            }
        }
        seniorityLevel.setFrom(seniorityLevelDTO.getFrom());
        seniorityLevel.setTo(seniorityLevelDTO.getTo());


        if (!seniorityLevelDTO.getPayGradeId().equals(functionAndSeniorityLevel.getPayGrade().getId())) {
            seniorityLevelGraphRepository.removePreviousPayGradeFromSeniorityLevel(seniorityLevelDTO.getId());
            PayGrade payGrade = payGradeGraphRepository.findOne(seniorityLevelDTO.getPayGradeId());
            if (!Optional.ofNullable(payGrade).isPresent() || payGrade.isDeleted()) {
                throw new DataNotFoundByIdException("Invalid pay grade id " + seniorityLevelDTO.getPayGradeId());
            }
            seniorityLevel.setPayGrade(payGrade);
        }

        seniorityLevel.setPensionPercentage(seniorityLevelDTO.getPensionPercentage());
        seniorityLevel.setFreeChoicePercentage(seniorityLevelDTO.getFreeChoicePercentage());
        seniorityLevel.setFreeChoiceToPension(seniorityLevelDTO.getFreeChoiceToPension());

    }

    private void updateCurrentExpertise(Long countryId, Expertise expertise, ExpertiseUpdateDTO expertiseDTO) {
        expertise.setName(expertiseDTO.getName().trim());
        expertise.setDescription(expertiseDTO.getDescription());
        expertise.setStartDateMillis(expertiseDTO.getStartDateMillis());
        expertise.setEndDateMillis(expertiseDTO.getEndDateMillis());
        if (!expertise.getOrganizationLevel().getId().equals(expertiseDTO.getOrganizationLevelId())) {
            Level level = countryGraphRepository.getLevel(countryId, expertiseDTO.getOrganizationLevelId());
            if (!Optional.ofNullable(level).isPresent()) {
                throw new DataNotFoundByIdException("Invalid level id " + expertiseDTO.getOrganizationLevelId());
            }
            expertise.setOrganizationLevel(level);
        }

        Set<Long> previousOrganizationLevelIds = expertise.getOrganizationServices().stream().map(organizationService1 -> organizationService1.getId()).collect(Collectors.toSet());
        if (!previousOrganizationLevelIds.equals(expertiseDTO.getOrganizationServiceIds())) {
            Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
            if (!Optional.ofNullable(organizationService).isPresent() || organizationService.size() != expertiseDTO.getOrganizationServiceIds().size()) {
                throw new DataNotFoundByIdException("All services are not found");
            }
            expertise.setOrganizationServices(organizationService);
        }

        if (!expertise.getUnion().getId().equals(expertiseDTO.getUnionId())) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(expertiseDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                throw new DataNotFoundByIdException("Invalid union id " + expertiseDTO.getUnionId());
            }
            expertise.setUnion(union);
        }

        expertise.setFullTimeWeeklyMinutes(expertiseDTO.getFullTimeWeeklyMinutes());
        expertise.setNumberOfWorkingDaysInWeek(expertiseDTO.getNumberOfWorkingDaysInWeek());
        expertise.setPaidOutFrequency(expertiseDTO.getPaidOutFrequency());


    }

    public ExpertiseQueryResult deleteExpertise(Long expertiseId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid expertise Id");
        }
        if (expertise.isPublished()) {
            throw new ActionNotPermittedException("Expertise can't be removed, Its already published");

        }
        ExpertiseQueryResult parentExpertise = expertiseGraphRepository.getParentExpertiseByExpertiseId(expertiseId);
        if (Optional.ofNullable(parentExpertise).isPresent()) {
            // remove link and unlink
            expertiseGraphRepository.unlinkExpertiseAndMakeEditable(parentExpertise.getId(), false, false);

            parentExpertise.setHistory(false);
        }
        expertise.setDeleted(true);
        if (Optional.ofNullable(expertise.getSeniorityLevel()).isPresent() && !expertise.getSeniorityLevel().isEmpty()) {
            for (SeniorityLevel seniorityLevel : expertise.getSeniorityLevel())
                seniorityLevel.setDeleted(true);
        }
        save(expertise);
        return parentExpertise;
    }


    public boolean removeSeniorityLevelFromExpertise(Long expertiseId, Long seniorityLevelId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid expertise Id");
        }
        if (expertise.isPublished()) {
            throw new ActionNotPermittedException("SeniorityLevelId can't be removed, expertise is already published");
        }

        if (Optional.ofNullable(expertise.getSeniorityLevel()).isPresent() && !expertise.getSeniorityLevel().isEmpty()) {
            for (SeniorityLevel seniorityLevel : expertise.getSeniorityLevel())
                if (seniorityLevel.getId().equals(seniorityLevelId)) {
                    seniorityLevel.setDeleted(true);
                    break;
                }
        }
        save(expertise);
        return true;
    }

    public Map<String, Object> setExpertiseToStaff(Long staffId, List<Long> expertiseIds) {
        Staff currentStaff = staffGraphRepository.findOne(staffId);
        List<StaffExpertiseRelationShip> staffExpertiseRelationShips = new ArrayList<>();
        List<Expertise> expertise = expertiseGraphRepository.getExpertiseByIdsIn(expertiseIds);
        for (Expertise currentExpertise : expertise) {
            StaffExpertiseRelationShip staffExpertiseRelationShip = new StaffExpertiseRelationShip(currentStaff, currentExpertise, 0, DateUtil.getCurrentDate());
            staffExpertiseRelationShips.add(staffExpertiseRelationShip);
        }
        staffExpertiseRelationShipGraphRepository.saveAll(staffExpertiseRelationShips);
        return retrieveExpertiseDetails(currentStaff);
//        currentStaff.setParentExpertise(expertiseGraphRepository.getExpertiseByIdsIn(expertiseIds));
//        Staff staff = staffGraphRepository.save(currentStaff);
//        return  staff.retrieveExpertiseDetails();

    }

    public Map<String, Object> getExpertiseToStaff(Long staffId) {
        //staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staffId);
        Staff staff = staffGraphRepository.findOne(staffId);
        return retrieveExpertiseDetails(staff);
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
        unionServiceWrapper.setServices(organizationServiceService.getAllOrganizationService(countryId));
        unionServiceWrapper.setUnions(organizationGraphRepository.findAllUnionsByCountryId(countryId));
        unionServiceWrapper.setOrganizationLevels(countryGraphRepository.getLevelsByCountry(countryId));
        return unionServiceWrapper;
    }

    /*This method is used to publish an expertise
    * */
    public ExpertiseQueryResult publishExpertise(Long expertiseId, Long publishedDateMillis) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            throw new DataNotFoundByIdException("Invalid expertise id");
        }
        if (expertise.isPublished()) {
            throw new ActionNotPermittedException("expertise is already published");
        }
        expertise.setPublished(true);
        expertise.setStartDateMillis(new Date(publishedDateMillis));
        for (SeniorityLevel seniorityLevel : expertise.getSeniorityLevel()) {
            seniorityLevel.setPublished(true);
        }
        save(expertise);
        ExpertiseQueryResult parentExpertise = expertiseGraphRepository.getParentExpertiseByExpertiseId(expertiseId);
        if (Optional.ofNullable(parentExpertise).isPresent()) {
            expertiseGraphRepository.setEndDateToExpertise(parentExpertise.getId(), publishedDateMillis - ONE_DAY);
            parentExpertise.setEndDateMillis(new Date(publishedDateMillis - ONE_DAY).getTime());
            parentExpertise.setPublished(true);
            parentExpertise.setHistory(true);
        }
        return parentExpertise;
    }


    public ExpertiseQueryResult getExpertiseById(Long expertiseId) {
        ExpertiseQueryResult expertise = expertiseGraphRepository.getExpertiseById(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertise Id " + expertiseId);
        }
        return expertise;
    }


    public Map<String, Object> retrieveExpertiseDetails(Staff staff) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffId", staff.getId());
        map.put("staffName", staff.getFirstName() + "   " + staff.getLastName());
        map.put("expertiseList", staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staff.getId()));
        return map;
    }

    public List<ExpertiseQueryResult> getUnpublishedExpertise(Long countryId) {
        return expertiseGraphRepository.getUnpublishedExpertise(countryId);
    }

    public List<ExpertiseDTO> getExpertiseByOrganizationSubType(Long countryId, Long organizationSubTypeId) {
        return expertiseGraphRepository.getExpertiseByOrganizationSubType(countryId, organizationSubTypeId);
    }
}
