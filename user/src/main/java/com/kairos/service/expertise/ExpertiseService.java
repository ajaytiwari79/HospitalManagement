package com.kairos.service.expertise;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.custom_exception.ActionNotPermittedException;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.user.country.experties.*;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.employment_type.EmploymentTypeQueryResult;

import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.user.expertise.Response.*;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.persistence.model.country.experties.UnionServiceWrapper;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.pay_table.PayGrade;
import com.kairos.persistence.model.staff.StaffExpertiseRelationShip;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.CareDays;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseEmploymentTypeRelationship;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.FunctionalPaymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.utils.DateUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;
import static javax.management.timer.Timer.ONE_DAY;

/**
 * Created by prabjot on 28/10/16.
 */
@Service
@Transactional
public class ExpertiseService {

    @Inject
    private
    CountryGraphRepository countryGraphRepository;
    @Inject
    private
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
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;

    @Inject
    TagService tagService;
    @Inject
    StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private FunctionalPaymentGraphRepository functionalPaymentGraphRepository;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private ExpertiseEmploymentTypeRelationshipGraphRepository expertiseEmploymentTypeRelationshipGraphRepository;

    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;

    @Inject private com.kairos.service.organization.OrganizationService organizationService;

    public ExpertiseResponseDTO saveExpertise(long countryId, CountryExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "country", countryId);
        }
        ExpertiseResponseDTO expertiseResponseDTO = new ExpertiseResponseDTO();


        Expertise expertise;

        if (!Optional.ofNullable(expertiseDTO.getId()).isPresent()) {
            boolean isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(expertiseDTO.getOrganizationLevelId(), "(?i)" + expertiseDTO.getName().trim(), -1L);
            if (isExpertiseExists) {
                exceptionService.duplicateDataException("message.duplicate", "expertise");
            }
            expertise = new Expertise(expertiseDTO.getName().trim(), expertiseDTO.getDescription(), country, expertiseDTO.getStartDateMillis(), expertiseDTO.getEndDateMillis(), expertiseDTO.getFullTimeWeeklyMinutes() != null ? expertiseDTO.getFullTimeWeeklyMinutes() : FULL_TIME_WEEKLY_MINUTES,
                    expertiseDTO.getNumberOfWorkingDaysInWeek() != null ? expertiseDTO.getNumberOfWorkingDaysInWeek() : NUMBER_OF_WORKING_DAYS_IN_WEEK, expertiseDTO.getBreakPaymentSetting(), false, false, false);
            prepareExpertiseWhileCreate(expertise, expertiseDTO, countryId);
            expertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.setFullTimeWeeklyMinutes(expertise.getFullTimeWeeklyMinutes());
            expertiseResponseDTO.setNumberOfWorkingDaysInWeek(expertise.getNumberOfWorkingDaysInWeek());
            expertiseResponseDTO.setEditable(expertise.isHistory());
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());

            TimeSlot timeSlot = new TimeSlot(NIGHT_START_HOUR, NIGHT_END_HOUR);
            ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO = new ExpertiseNightWorkerSettingDTO(timeSlot, null,
                    null, null, null, null, countryId, expertise.getId());
            genericRestClient.publish(expertiseNightWorkerSettingDTO, countryId, false, IntegrationOperation.CREATE,
                    "/expertise/" + expertise.getId() + "/night_worker_setting", null);
        } else {
            // Expertise is already created only need to add Sr level
            expertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "expertise", expertiseDTO.getId());
            }
            validateSeniorityLevel(expertise.getSeniorityLevel(), expertiseDTO.getSeniorityLevel(), -1L);

            SeniorityLevel seniorityLevel = new SeniorityLevel(expertiseDTO.getSeniorityLevel().getFrom(), expertiseDTO.getSeniorityLevel().getTo(), expertiseDTO.getSeniorityLevel().getPensionPercentage(), expertiseDTO.getSeniorityLevel().getFreeChoicePercentage(),
                    expertiseDTO.getSeniorityLevel().getFreeChoiceToPension(), false);
            addNewSeniorityLevelInExpertise(expertise, seniorityLevel, expertiseDTO.getSeniorityLevel());
            expertiseGraphRepository.save(expertise);
            expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());
        }
        return expertiseResponseDTO;
    }


    private void validateSeniorityLevel(List<SeniorityLevel> seniorityLevels, SeniorityLevelDTO seniorityLevelDTO, Long currentSeniorityLevelId) {
        Collections.sort(seniorityLevels);
        for (SeniorityLevel seniorityLevel : seniorityLevels) {
            if (!seniorityLevel.getId().equals(currentSeniorityLevelId)) { // we are skipping the current
                if (seniorityLevelDTO.getTo() == null && seniorityLevel.getTo() == null) {
                    exceptionService.actionNotPermittedException("message.expertise.seniorityLevel.present", seniorityLevel.getId());

                } else if (seniorityLevelDTO.getTo() == null && seniorityLevel.getTo() != null) {
                    if (seniorityLevelDTO.getFrom() < seniorityLevel.getTo()) {
                        exceptionService.actionNotPermittedException("message.expertise.seniorityLevel.greaterThan", seniorityLevel.getTo(), seniorityLevel.getId());

                    }
                } else if (seniorityLevelDTO.getTo() != null && seniorityLevel.getTo() == null) {
                    if (seniorityLevelDTO.getTo() > seniorityLevel.getFrom()) {
                        exceptionService.actionNotPermittedException("message.expertise.seniorityLevel.lessThan", seniorityLevel.getFrom(), seniorityLevel.getId());
                    }
                } else {
                    if (seniorityLevelDTO.getFrom() < seniorityLevel.getFrom() && !(seniorityLevelDTO.getTo() <= seniorityLevel.getFrom())) {
                        throw new ActionNotPermittedException("Already a Sr level is present 1:" + seniorityLevel.getId());
                    } else if (seniorityLevelDTO.getFrom() > seniorityLevel.getFrom() && !(seniorityLevelDTO.getFrom() >= seniorityLevel.getTo())) {
                        throw new ActionNotPermittedException("Already a Sr level is present 2:" + seniorityLevel.getId());
                    } else if (seniorityLevelDTO.getFrom() == seniorityLevel.getFrom() || seniorityLevelDTO.getTo() == seniorityLevel.getTo()) {
                        throw new ActionNotPermittedException("Same Seniority level already exists");
                    }
                }
            }
        }

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

                SeniorityLevel seniorityLevel = new SeniorityLevel();
                BeanUtils.copyProperties(seniorityLevelFromDB, seniorityLevel);
                seniorityLevel.setId(null);
                Optional<SeniorityLevel> SeniorityLevel = seniorityLevelGraphRepository.findById(seniorityLevelFromDB.getId());

                if (Optional.ofNullable(SeniorityLevel.get().getPayGrade()).isPresent()) {
                    seniorityLevel.setPayGrade(SeniorityLevel.get().getPayGrade());
                }
                seniorityLevel.setFrom(seniorityLevelFromDB.getFrom());
                seniorityLevel.setTo(seniorityLevelFromDB.getTo());
                seniorityLevel.setPensionPercentage(seniorityLevelFromDB.getPensionPercentage());
                seniorityLevel.setFreeChoicePercentage(seniorityLevelFromDB.getFreeChoicePercentage());
                seniorityLevel.setFreeChoiceToPension(seniorityLevelFromDB.getFreeChoiceToPension());
                seniorityLevelGraphRepository.save(seniorityLevel);
                expertise.getSeniorityLevel().add(seniorityLevel);
                seniorityLevelResponse.add(getSeniorityLevelResponse(seniorityLevelFromDB, seniorityLevel));

            }
        }

        expertiseGraphRepository.save(expertise);
        // NOW linking this with functional table

        functionalPaymentGraphRepository.linkFunctionalPaymentExpertise(expertise.getParentExpertise().getId(), expertise.getId());


        return seniorityLevelResponse;

    }


    /*This method is responsible for generating Seniority Level response */
    private SeniorityLevelDTO getSeniorityLevelResponse(SeniorityLevel seniorityLevelFromDB, SeniorityLevel seniorityLevel) {
        SeniorityLevelDTO seniorityLevelDTO = new SeniorityLevelDTO();
        seniorityLevelDTO = objectMapper.convertValue(seniorityLevel, SeniorityLevelDTO.class);
        seniorityLevelDTO.setParentId(seniorityLevelFromDB.getId());

        return seniorityLevelDTO;
    }


    private void prepareExpertiseWhileCreate(Expertise expertise, CountryExpertiseDTO expertiseDTO, Long countryId) {
        Level level = countryGraphRepository.getLevel(countryId, expertiseDTO.getOrganizationLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "level", expertiseDTO.getOrganizationLevelId());
        }
        expertise.setOrganizationLevel(level);
        Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
        if (!Optional.ofNullable(organizationService).isPresent() || organizationService.size() != expertiseDTO.getOrganizationServiceIds().size()) {
            exceptionService.dataNotFoundByIdException("message.multipleDataNotFound", "services");
        }
        expertise.setOrganizationServices(organizationService);
        Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(expertiseDTO.getUnionId());
        if (!Optional.ofNullable(union).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "union", expertiseDTO.getUnionId());
        }
        expertise.setUnion(union);


        SeniorityLevel seniorityLevel = null;
        if (expertiseDTO.getSeniorityLevel() != null) {
            seniorityLevel = new SeniorityLevel(expertiseDTO.getSeniorityLevel().getFrom(), expertiseDTO.getSeniorityLevel().getTo(), expertiseDTO.getSeniorityLevel().getPensionPercentage(), expertiseDTO.getSeniorityLevel().getFreeChoicePercentage(),
                    expertiseDTO.getSeniorityLevel().getFreeChoiceToPension(), false);
            seniorityLevel = addNewSeniorityLevelInExpertise(expertise, seniorityLevel, expertiseDTO.getSeniorityLevel());

        }

        expertiseGraphRepository.save(expertise);
        expertiseDTO.setId(expertise.getId());
        expertiseDTO.setPublished(expertise.isPublished());
        if (expertiseDTO.getSeniorityLevel() != null) {
            expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
        }

    }

    private SeniorityLevel addNewSeniorityLevelInExpertise(Expertise expertise, SeniorityLevel seniorityLevel, SeniorityLevelDTO seniorityLevelDTO) {

        PayGrade payGrade = payGradeGraphRepository.findOne(seniorityLevelDTO.getPayGradeId());
        if (!Optional.ofNullable(payGrade).isPresent() || payGrade.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "pay-grade", seniorityLevelDTO.getPayGradeId());
        }
        seniorityLevel.setPayGrade(payGrade);
        expertise.getSeniorityLevel().add(seniorityLevel);
        seniorityLevelGraphRepository.save(seniorityLevel);
        return seniorityLevel;
    }


    public List<ExpertiseQueryResult> getAllExpertise(long countryId) {
        return expertiseGraphRepository.getAllExpertiseByCountryId(countryId);
    }


    public ExpertiseResponseDTO updateExpertise(Long countryId, ExpertiseUpdateDTO expertiseDTO) {
        Expertise currentExpertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
        if (!Optional.ofNullable(currentExpertise).isPresent() || currentExpertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "expertise", expertiseDTO.getId());
        }
        if (!currentExpertise.getName().equalsIgnoreCase(expertiseDTO.getName().trim())) {
            boolean isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(expertiseDTO.getOrganizationLevelId(), "(?i)" + expertiseDTO.getName().trim(), expertiseDTO.getId());
            if (isExpertiseExists) {
                exceptionService.duplicateDataException("message.duplicate", "expertise");
            }
        }

        Optional<SeniorityLevel> seniorityLevelToUpdate =
                currentExpertise.getSeniorityLevel().stream().filter(seniorityLevel -> seniorityLevel.getId().equals(expertiseDTO.getSeniorityLevel().getId())).findFirst();

        if (!seniorityLevelToUpdate.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.seniorityLevel.notFound", expertiseDTO.getSeniorityLevel().getId());

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
            // copiedExpertise.getSeniorityLevelFunction().clear();
            // Calling this function to get any updates or updated value from DTO.
            updateCurrentExpertise(countryId, copiedExpertise, expertiseDTO);

            List<SeniorityLevelDTO> seniorityLevelDTOList = new ArrayList<>();
            //  Adding the currently edited  Sr level in expertise.

            SeniorityLevel seniorityLevel = new SeniorityLevel(expertiseDTO.getSeniorityLevel().getFrom(), expertiseDTO.getSeniorityLevel().getTo(), expertiseDTO.getSeniorityLevel().getPensionPercentage(), expertiseDTO.getSeniorityLevel().getFreeChoicePercentage(),
                    expertiseDTO.getSeniorityLevel().getFreeChoiceToPension(), false);
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

            boolean levelChanged = updateCurrentExpertise(countryId, currentExpertise, expertiseDTO);
            updateCurrentSeniorityLevel(expertiseDTO.getSeniorityLevel(), seniorityLevelToUpdate.get(), levelChanged);
            // organization Level is changed so need to set new


            expertiseGraphRepository.save(currentExpertise);
            expertiseDTO.setId(currentExpertise.getId());
            expertiseDTO.setPublished(currentExpertise.isPublished());

            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());


        }
        return expertiseResponseDTO;
    }

    private void updateCurrentSeniorityLevel(SeniorityLevelDTO seniorityLevelDTO, SeniorityLevel seniorityLevel, boolean levelChanged) {

        PayGrade previousPayGrade = seniorityLevelGraphRepository.getPayGradeBySeniorityLevelId(seniorityLevel.getId());
        seniorityLevel.setFrom(seniorityLevelDTO.getFrom());
        seniorityLevel.setTo(seniorityLevelDTO.getTo());
        seniorityLevel.setPensionPercentage(seniorityLevelDTO.getPensionPercentage());
        seniorityLevel.setFreeChoicePercentage(seniorityLevelDTO.getFreeChoicePercentage());
        seniorityLevel.setFreeChoiceToPension(seniorityLevelDTO.getFreeChoiceToPension());

        if (previousPayGrade == null || !seniorityLevelDTO.getPayGradeId().equals(previousPayGrade.getId())) {
            if (!levelChanged) {
                seniorityLevelGraphRepository.removePreviousPayGradeFromSeniorityLevel(seniorityLevelDTO.getId());
            }

            PayGrade payGrade = payGradeGraphRepository.findOne(seniorityLevelDTO.getPayGradeId(), 0);

            if (!Optional.ofNullable(payGrade).isPresent() || payGrade.isDeleted()) {
                exceptionService.dataNotFoundByIdException("message.expertise.payGradeId.notFound", seniorityLevelDTO.getPayGradeId());
            }
            seniorityLevel.setPayGrade(payGrade);
        }


    }

    private boolean updateCurrentExpertise(Long countryId, Expertise expertise, ExpertiseUpdateDTO expertiseDTO) {
        boolean levelChanged = false;
        expertise.setName(expertiseDTO.getName().trim());
        expertise.setDescription(expertiseDTO.getDescription());
        expertise.setStartDateMillis(expertiseDTO.getStartDateMillis());
        expertise.setEndDateMillis(expertiseDTO.getEndDateMillis());
        expertise.setBreakPaymentSetting(expertiseDTO.getBreakPaymentSetting());
        if (!expertise.getOrganizationLevel().getId().equals(expertiseDTO.getOrganizationLevelId())) {
            Level level = countryGraphRepository.getLevel(countryId, expertiseDTO.getOrganizationLevelId());
            if (!Optional.ofNullable(level).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.country.level.id.notFound", expertiseDTO.getOrganizationLevelId());

            }
            expertise.setOrganizationLevel(level);
            seniorityLevelGraphRepository.unlinkAllPayGradesFromExpertise(expertise.getId());
            levelChanged = true;
        }

        Set<Long> previousOrganizationServicesIds = expertise.getOrganizationServices().stream().map(UserBaseEntity::getId).collect(Collectors.toSet());
        if (!previousOrganizationServicesIds.equals(expertiseDTO.getOrganizationServiceIds())) {
            Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
            if (!Optional.ofNullable(organizationService).isPresent() || organizationService.size() != expertiseDTO.getOrganizationServiceIds().size()) {
                exceptionService.dataNotFoundByIdException("message.expertise.serviceNotFound");

            }
            expertise.setOrganizationServices(organizationService);
        }

        if (!expertise.getUnion().getId().equals(expertiseDTO.getUnionId())) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(expertiseDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.expertise.unionId.notFound", expertiseDTO.getUnionId());

            }
            expertise.setUnion(union);
        }
        expertise.setFullTimeWeeklyMinutes(expertiseDTO.getFullTimeWeeklyMinutes());
        expertise.setNumberOfWorkingDaysInWeek(expertiseDTO.getNumberOfWorkingDaysInWeek());
        return levelChanged;
    }

    public ExpertiseQueryResult deleteExpertise(Long expertiseId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        if (expertise.isPublished()) {
            exceptionService.actionNotPermittedException("message.expertise.cannotRemoved");


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
        expertiseGraphRepository.save(expertise);
        return parentExpertise;
    }


    public boolean removeSeniorityLevelFromExpertise(Long expertiseId, Long seniorityLevelId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        if (expertise.isPublished()) {
            exceptionService.actionNotPermittedException("message.expertise.seniorityLevel.idCannotRemoved");

        }

        if (Optional.ofNullable(expertise.getSeniorityLevel()).isPresent() && !expertise.getSeniorityLevel().isEmpty()) {
            for (SeniorityLevel seniorityLevel : expertise.getSeniorityLevel())
                if (seniorityLevel.getId().equals(seniorityLevelId)) {
                    seniorityLevel.setDeleted(true);
                    break;
                }
        }
        expertiseGraphRepository.save(expertise);
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
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

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
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        if (expertise.isPublished()) {
            exceptionService.actionNotPermittedException("message.expertise.alreadyPublished");
        }
        List<Long> seniorityLevelId = new ArrayList<>();
        for (SeniorityLevel seniorityLevel : expertise.getSeniorityLevel()) {
            seniorityLevel.setPublished(true);
            seniorityLevelId.add(seniorityLevel.getId());
        }
        boolean payGradesExistsForSeniorityLevels = seniorityLevelGraphRepository.checkPayGradesInSeniorityLevel(seniorityLevelId);
        if (!payGradesExistsForSeniorityLevels) {
            exceptionService.actionNotPermittedException("message.seniorityLevel.payGrade.missing");

        }
        expertise.setPublished(true);
        expertise.setStartDateMillis(new Date(publishedDateMillis));

        expertiseGraphRepository.save(expertise);
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
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        return expertise;
    }


    public Map<String, Object> retrieveExpertiseDetails(Staff staff) {
        Map<String, Object> map = new HashMap<>();
        map.put("staffId", staff.getId());
        map.put("staffName", staff.getFirstName() + "   " + staff.getLastName());
        map.put("expertiseList", staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staff.getId()));
        return map;
    }

    public List<ExpertiseQueryResult> getUnpublishedExpertise(Long countryId) {
        return expertiseGraphRepository.getUnpublishedExpertise(countryId);
    }


    public List<ExpertiseDTO> getExpertiseByOrganizationSubType(Long countryId, Long organizationSubTypeId) {
        //Long selectedDateInLong = (selectedDate != null) ? DateUtil.getIsoDateInLong(selectedDate) : DateUtil.getCurrentDateMillis();
        return expertiseGraphRepository.getExpertiseByOrganizationSubType(countryId, organizationSubTypeId);
    }

    public List<AgeRangeDTO> updateAgeRangeInExpertise(Long expertiseId, List<AgeRangeDTO> ageRangeDTO, String wtaType) {
        Optional<Expertise> expertise = expertiseGraphRepository.findById(expertiseId);
        if (!expertise.isPresent() || expertise.get().isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        validateAgeRange(ageRangeDTO);

        List<CareDays> careDays = ObjectMapperUtils.copyPropertiesOfListByMapper(ageRangeDTO, CareDays.class);
        if (wtaType.equalsIgnoreCase(SENIOR_DAYS)) {
            expertise.get().setSeniorDays(careDays);
        } else if (wtaType.equalsIgnoreCase(CHILD_CARE)) {
            expertise.get().setChildCareDays(careDays);
        }
        expertiseGraphRepository.save(expertise.get());
        ageRangeDTO = ObjectMapperUtils.copyPropertiesOfListByMapper((wtaType.equals(CHILD_CARE) ? expertise.get().getChildCareDays() : expertise.get().getSeniorDays()), AgeRangeDTO.class);
        return ageRangeDTO;
    }


    //Validating age range
    public void validateAgeRange(List<AgeRangeDTO> ageRangeDTO) {
        Collections.sort(ageRangeDTO);
        for (int i = 0; i < ageRangeDTO.size(); i++) {
            if (ageRangeDTO.get(i).getTo() != null && (ageRangeDTO.get(i).getFrom() > ageRangeDTO.get(i).getTo()))
                exceptionService.actionNotPermittedException("message.expertise.age.rangeInvalid", ageRangeDTO.get(i).getFrom(), ageRangeDTO.get(i).getTo());
            if (ageRangeDTO.size() > 1 && i < ageRangeDTO.size() - 1 && ageRangeDTO.get(i).getTo() > ageRangeDTO.get(i + 1).getFrom())
                exceptionService.actionNotPermittedException("message.expertise.age.overlap");

        }

    }

    public Boolean addPlannedTimeInExpertise(Long expertiseId, ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        Optional<Expertise> expertise = expertiseGraphRepository.findById(expertiseId);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseEmploymentTypeDTO.getExpertiseId());
        }
        linkPlannedTimeTypeWithExpertise(expertiseEmploymentTypeDTO, expertise.get());
        return true;
    }

    public List<ExpertisePlannedTimeQueryResult> getPlannedTimeInExpertise(Long expertiseId) {
        Optional<Expertise> expertise = expertiseGraphRepository.findById(expertiseId);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);
        }
        return expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(expertiseId);
    }

    public Boolean updatePlannedTimeInExpertise(Long expertiseId, ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        Optional<Expertise> expertise = expertiseGraphRepository.findById(expertiseId);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);
        }
        expertiseEmploymentTypeRelationshipGraphRepository.removeAllPreviousEmploymentType(expertiseId);
        linkPlannedTimeTypeWithExpertise(expertiseEmploymentTypeDTO, expertise.get());
        return true;
    }

    private void linkPlannedTimeTypeWithExpertise(ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO, Expertise expertise) {
        Iterable<EmploymentType> employmentTypes = employmentTypeGraphRepository.findAllById(expertiseEmploymentTypeDTO.getEmploymentTypeIds());
        List<ExpertiseEmploymentTypeRelationship> expertiseEmploymentList = new ArrayList<>();
        employmentTypes.forEach(employmentType -> {
            ExpertiseEmploymentTypeRelationship expertiseEmploymentTypeRelationship = new ExpertiseEmploymentTypeRelationship(expertise,
                    employmentType, expertiseEmploymentTypeDTO.getIncludedPlannedTime(), expertiseEmploymentTypeDTO.getExcludedPlannedTime());
            expertiseEmploymentList.add(expertiseEmploymentTypeRelationship);

        });
        expertiseEmploymentTypeRelationshipGraphRepository.saveAll(expertiseEmploymentList);
    }

    public Map<String, Object> getPlannedTimeAndEmploymentType(Long countryId) {
        List<EmploymentTypeQueryResult> employmentTypes = employmentTypeGraphRepository.getEmploymentTypeByCountry(countryId, false);
        Map<String, Object> countryDetail = new HashMap<>();
        countryDetail.put("countryId", countryId);
        List<PresenceTypeDTO> presenceTypes = ObjectMapperUtils.copyPropertiesOfListByMapper
                (genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/plannedTimeType", countryDetail), PresenceTypeDTO.class);

        countryDetail.put("employmentTypes", employmentTypes);
        countryDetail.put("presenceTypes", presenceTypes);
        return countryDetail;
    }

    public List<ExpertiseTagDTO> getExpertiseForOrgCTA(long unitId) {
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        return expertiseGraphRepository.getAllExpertiseWithTagsByCountry(countryId);
    }

    public CopyExpertiseDTO copyExpertise(Long expertiseId, CopyExpertiseDTO copyExpertiseDTO) {

        boolean isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(copyExpertiseDTO.getOrganizationLevelId(), "(?i)" + copyExpertiseDTO.getName(), -1L);
        if (isExpertiseExists) {
            exceptionService.duplicateDataException("message.duplicate", "expertise");
        }

        Optional<Expertise> sourceExpertise = expertiseGraphRepository.findById(expertiseId);
        if (!sourceExpertise.isPresent() || sourceExpertise.get().isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);
        }

        Expertise targetExpertise = new Expertise(copyExpertiseDTO.getName(), copyExpertiseDTO.getDescription(), sourceExpertise.get().getCountry(), DateUtils.getDateFromLocalDate(copyExpertiseDTO.getStartDate()), DateUtils.getDateFromLocalDate(copyExpertiseDTO.getEndDate()),
                copyExpertiseDTO.getFullTimeWeeklyMinutes() != null ? copyExpertiseDTO.getFullTimeWeeklyMinutes() : FULL_TIME_WEEKLY_MINUTES,
                copyExpertiseDTO.getNumberOfWorkingDaysInWeek() != null ? copyExpertiseDTO.getNumberOfWorkingDaysInWeek() : NUMBER_OF_WORKING_DAYS_IN_WEEK, copyExpertiseDTO.getBreakPaymentSetting(), false, false, false);

        prepareExpertiseWhileCopy(targetExpertise, copyExpertiseDTO, sourceExpertise.get());

        return copyExpertiseDTO;
    }

    private void prepareExpertiseWhileCopy(Expertise targetExpertise, CopyExpertiseDTO expertiseDTO, Expertise sourceExpertise) {
        if (!expertiseDTO.getOrganizationLevelId().equals(sourceExpertise.getOrganizationLevel().getId())) {
            Level level = countryGraphRepository.getLevel(sourceExpertise.getCountry().getId(), expertiseDTO.getOrganizationLevelId());
            if (!Optional.ofNullable(level).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "level", expertiseDTO.getOrganizationLevelId());
            }
            targetExpertise.setOrganizationLevel(level);
        } else {
            targetExpertise.setOrganizationLevel(sourceExpertise.getOrganizationLevel());
        }

        addSeniorityLevelsInExpertise(targetExpertise, expertiseDTO, sourceExpertise);
        Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
        if (!Optional.ofNullable(organizationService).isPresent() || organizationService.size() != expertiseDTO.getOrganizationServiceIds().size()) {
            exceptionService.dataNotFoundByIdException("message.multipleDataNotFound", "services");
        }
        targetExpertise.setOrganizationServices(organizationService);
        Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(expertiseDTO.getUnionId());
        if (!Optional.ofNullable(union).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "union", expertiseDTO.getUnionId());
        }
        targetExpertise.setUnion(union);
        expertiseGraphRepository.save(targetExpertise);
        createDefaultSettings(targetExpertise, sourceExpertise);
        expertiseDTO.setId(targetExpertise.getId());
        // small object so not creating map
        expertiseDTO.getSeniorityLevels().forEach(seniorityLevelDTO -> targetExpertise.getSeniorityLevel().forEach(current -> {
            if (current.getFrom().equals(seniorityLevelDTO.getFrom())) {
                seniorityLevelDTO.setId(current.getId());
            }
        }));
    }

    private void addSeniorityLevelsInExpertise(Expertise targetExpertise, CopyExpertiseDTO expertiseDTO, Expertise sourceExpertise) {
        Set<Long> payGradeIds = expertiseDTO.getSeniorityLevels().stream().map(SeniorityLevelDTO::getPayGradeId).collect(Collectors.toSet());
        List<PayGrade> payGrades = payGradeGraphRepository.getAllPayGradesById(payGradeIds);
        Map<Long, PayGrade> payGradeMap = payGrades.stream().collect(Collectors.toMap(PayGrade::getId, v -> v));
        expertiseDTO.getSeniorityLevels().forEach(seniorityLevelDTO -> {
            SeniorityLevel seniorityLevel = new SeniorityLevel(seniorityLevelDTO.getFrom(), seniorityLevelDTO.getTo(), payGradeMap.get(seniorityLevelDTO.getPayGradeId()), seniorityLevelDTO.getPensionPercentage(), seniorityLevelDTO.getFreeChoicePercentage(),
                    seniorityLevelDTO.getFreeChoiceToPension(), false);
            targetExpertise.addSeniorityLevel(seniorityLevel);
        });
        sourceExpertise.getChildCareDays().forEach(careDays -> targetExpertise.addChildCareDay(new CareDays(careDays.getFrom(), careDays.getTo(), careDays.getLeavesAllowed())));

        sourceExpertise.getSeniorDays().forEach(careDays -> targetExpertise.addSeniorDay(new CareDays(careDays.getFrom(), careDays.getTo(), careDays.getLeavesAllowed())));
    }

    private void createDefaultSettings(Expertise targetExpertise, Expertise sourceExpertise) {
        List<ExpertiseEmploymentTypeRelationship> expertiseEmploymentList = new ArrayList();
        ExpertisePlannedTimeQueryResult expertiseEmploymentTypeRelationships = expertiseEmploymentTypeRelationshipGraphRepository.getPlannedTimeConfigurationByExpertise(sourceExpertise.getId());
        if (Optional.ofNullable(expertiseEmploymentTypeRelationships).isPresent()) {
            expertiseEmploymentTypeRelationships.employmentTypes.forEach(employmentType -> {
                ExpertiseEmploymentTypeRelationship expertiseEmploymentTypeRelationship = new ExpertiseEmploymentTypeRelationship(targetExpertise,
                        employmentType, expertiseEmploymentTypeRelationships.getIncludedPlannedTime(), expertiseEmploymentTypeRelationships.getExcludedPlannedTime());
                expertiseEmploymentList.add(expertiseEmploymentTypeRelationship);
            });
            expertiseEmploymentTypeRelationshipGraphRepository.saveAll(expertiseEmploymentList);
        }
        functionalPaymentGraphRepository.linkFunctionalPaymentInExpertise(sourceExpertise.getId(), targetExpertise.getId());

        TimeSlot timeSlot = new TimeSlot(NIGHT_START_HOUR, NIGHT_END_HOUR);
        ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO = new ExpertiseNightWorkerSettingDTO(timeSlot, null,
                null, null, null, null, targetExpertise.getCountry().getId(), targetExpertise.getId());
        genericRestClient.publish(expertiseNightWorkerSettingDTO, targetExpertise.getCountry().getId(), false, IntegrationOperation.CREATE,
                "/expertise/" + targetExpertise.getId() + "/night_worker_setting", null);

    }
}
