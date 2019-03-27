package com.kairos.service.expertise;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.ActionNotPermittedException;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.country.experties.*;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.MasterDataTypeEnum;
import com.kairos.enums.scheduler.JobFrequencyType;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.employment_type.EmploymentTypeQueryResult;
import com.kairos.persistence.model.country.experties.UnionServiceWrapper;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.pay_table.PayGrade;
import com.kairos.persistence.model.staff.StaffExpertiseRelationShip;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.CareDays;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseEmploymentTypeRelationship;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseSkillQueryResult;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseTagDTO;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.union.SectorGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.FunctionalPaymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.country.tag.TagService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationServiceService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getFirstDayOfMonth;
import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.DateUtils.getLocalDateTime;
import static com.kairos.commons.utils.ObjectUtils.isCollectionEmpty;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.*;
import static javax.management.timer.Timer.ONE_DAY;

/**
 * Created by prabjot on 28/10/16.
 */
@Service
@Transactional
public class ExpertiseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpertiseService.class);

    @Inject
    private
    CountryGraphRepository countryGraphRepository;
    @Inject
    private
    ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private
    StaffGraphRepository staffGraphRepository;
    @Inject
    private
    OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private
    OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private
    OrganizationServiceService organizationServiceService;
    @Inject
    private
    TagService tagService;
    @Inject
    private
    StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private
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
    @Inject
    private SchedulerServiceRestClient schedulerRestClient;
    @Inject
    private com.kairos.service.organization.OrganizationService organizationService;
    @Inject
    private SectorGraphRepository sectorGraphRepository;

    public ExpertiseResponseDTO saveExpertise(long countryId, ExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "country", countryId);
        }
        ExpertiseResponseDTO expertiseResponseDTO;
        Expertise expertise;
        if (!Optional.ofNullable(expertiseDTO.getId()).isPresent()) {
            boolean isExpertiseExists;
            if (Optional.ofNullable(expertiseDTO.getOrganizationLevelId()).isPresent())
                isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(expertiseDTO.getOrganizationLevelId(), "(?i)" + expertiseDTO.getName().trim(), -1L);
            else
                isExpertiseExists = expertiseGraphRepository.findExpertiseByUniqueName("(?i)" + expertiseDTO.getName().trim());
            if (isExpertiseExists) {
                exceptionService.duplicateDataException("message.duplicate", "expertise",expertiseDTO.getName());
            }
            Optional.ofNullable(expertiseDTO.getUnion()).ifPresent(unionIDNameDTO -> {
                if (expertiseDTO.isPublished() && (!Optional.ofNullable(unionIDNameDTO.getId()).isPresent() || !organizationGraphRepository.isPublishedUnion(unionIDNameDTO.getId()))) {
                    exceptionService.invalidRequestException("message.publish.expertise.union");
                }
            });
            expertise = new Expertise(expertiseDTO.getName().trim(), expertiseDTO.getDescription(), country, expertiseDTO.getStartDateMillis(), expertiseDTO.getEndDateMillis(), expertiseDTO.getFullTimeWeeklyMinutes() != null ? expertiseDTO.getFullTimeWeeklyMinutes() : FULL_TIME_WEEKLY_MINUTES,
                    expertiseDTO.getNumberOfWorkingDaysInWeek() != null ? expertiseDTO.getNumberOfWorkingDaysInWeek() : NUMBER_OF_WORKING_DAYS_IN_WEEK, expertiseDTO.getBreakPaymentSetting(), false, false, false,
                    getSector(expertiseDTO.getSector(), country));
            prepareExpertise(expertise, expertiseDTO, country,true);
            expertise.setTags(tagService.getCountryTagsByIdsAndMasterDataType(expertiseDTO.getTags(), MasterDataTypeEnum.EXPERTISE));
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.setFullTimeWeeklyMinutes(expertise.getFullTimeWeeklyMinutes());
            expertiseResponseDTO.setNumberOfWorkingDaysInWeek(expertise.getNumberOfWorkingDaysInWeek());
            expertiseResponseDTO.setEditable(expertise.isHistory());
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());
            expertiseResponseDTO.setSector(expertiseDTO.getSector());
            if (Optional.ofNullable(expertise.getSector()).isPresent()) {
                expertiseResponseDTO.getSector().setId(expertise.getSector().getId());
            }
            Optional.ofNullable(expertise.getUnion()).ifPresent(union -> {
                expertiseResponseDTO.getUnion().setId(expertise.getUnion().getId());
                organizationGraphRepository.linkUnionSector(expertise.getUnion().getId(), expertise.getSector().getId());
            });
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
            functionalPaymentGraphRepository.linkWithFunctionPayment(expertise.getId(), seniorityLevel.getId());
            expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());
        }
        return expertiseResponseDTO;
    }


    private boolean validateSeniorityLevel(List<SeniorityLevel> seniorityLevels, SeniorityLevelDTO seniorityLevelDTO, Long currentSeniorityLevelId) {
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
        return true;

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
        SeniorityLevelDTO seniorityLevelDTO;
        seniorityLevelDTO = objectMapper.convertValue(seniorityLevel, SeniorityLevelDTO.class);
        seniorityLevelDTO.setParentId(seniorityLevelFromDB.getId());

        return seniorityLevelDTO;
    }


    private void prepareExpertise(Expertise expertise, ExpertiseDTO expertiseDTO, Country country, boolean create) {
        expertise.setBreakPaymentSetting(expertiseDTO.getBreakPaymentSetting());
        expertise.setStartDateMillis(expertiseDTO.getStartDateMillis());
        expertise.setEndDateMillis(expertiseDTO.getEndDateMillis());
        Optional.ofNullable(expertiseDTO.getOrganizationLevelId()).ifPresent(orgLevelId -> {
            Level level = countryGraphRepository.getLevel(country.getId(), orgLevelId);
            if (!Optional.ofNullable(level).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "level", orgLevelId);
            }
            expertise.setOrganizationLevel(level);
        });
        Optional.ofNullable(expertiseDTO.getOrganizationServiceIds()).ifPresent(orgServiceIds -> {
            Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(orgServiceIds);
            if (!Optional.ofNullable(organizationService).isPresent() || organizationService.size() != orgServiceIds.size()) {
                exceptionService.dataNotFoundByIdException("message.multipleDataNotFound", "services");
            }
            expertise.setOrganizationServices(organizationService);
        });
        Optional.ofNullable(expertiseDTO.getUnion()).ifPresent(unionIDNameDTO -> {
            expertise.setUnion(getUnion(unionIDNameDTO.getId(), unionIDNameDTO.getName(), country));
        });
        if (expertiseDTO.getSeniorityLevel() != null &&  (create || isCollectionEmpty(expertise.getSeniorityLevel()))) {
            SeniorityLevel  seniorityLevel = new SeniorityLevel(expertiseDTO.getSeniorityLevel().getFrom(), expertiseDTO.getSeniorityLevel().getTo(), expertiseDTO.getSeniorityLevel().getPensionPercentage(), expertiseDTO.getSeniorityLevel().getFreeChoicePercentage(),
                    expertiseDTO.getSeniorityLevel().getFreeChoiceToPension(), false);
            seniorityLevel = addNewSeniorityLevelInExpertise(expertise, seniorityLevel, expertiseDTO.getSeniorityLevel());
            expertiseDTO.getSeniorityLevel().setId(seniorityLevel.getId());
        }
        expertiseGraphRepository.save(expertise);
        expertiseDTO.setId(expertise.getId());
        expertiseDTO.setPublished(expertise.isPublished());
        if (expertise.getUnion() != null)
            expertiseDTO.getUnion().setId(expertise.getUnion().getId());

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


    public ExpertiseResponseDTO updateExpertise(Long countryId, ExpertiseDTO expertiseDTO) {
        Expertise currentExpertise = expertiseGraphRepository.findOne(expertiseDTO.getId());
        if (!Optional.ofNullable(currentExpertise).isPresent() || currentExpertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "expertise", expertiseDTO.getId());
        }
        if (!currentExpertise.getName().equalsIgnoreCase(expertiseDTO.getName().trim())) {
            boolean isExpertiseExists;
            if (Optional.ofNullable(expertiseDTO.getOrganizationLevelId()).isPresent())
                isExpertiseExists = expertiseGraphRepository.checkExpertiseNameUniqueInOrganizationLevel(expertiseDTO.getOrganizationLevelId(), "(?i)" + expertiseDTO.getName().trim(), expertiseDTO.getId());
            else
                isExpertiseExists = expertiseGraphRepository.findExpertiseByUniqueName("(?i)" + expertiseDTO.getName().trim());
            if (isExpertiseExists) {
                exceptionService.duplicateDataException("message.duplicate", "expertise");
            }
        }
        Optional<SeniorityLevel> seniorityLevelToUpdate = Optional.empty();
        if (CollectionUtils.isNotEmpty(currentExpertise.getSeniorityLevel()) && Optional.ofNullable(expertiseDTO.getSeniorityLevel()).isPresent()) {
            seniorityLevelToUpdate = currentExpertise.getSeniorityLevel().stream().filter(seniorityLevel -> seniorityLevel.getId().equals(expertiseDTO.getSeniorityLevel().getId())).findFirst();
            if (!seniorityLevelToUpdate.isPresent()) {
                exceptionService.dataNotFoundByIdException("message.expertise.seniorityLevel.notFound", expertiseDTO.getSeniorityLevel().getId());
            }
        }
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "country", countryId);
        }
        ExpertiseResponseDTO expertiseResponseDTO;

        if (currentExpertise.isPublished()) {

            //current is published now we need to create a copy and update in that and return the updated copy
            Expertise copiedExpertise = new Expertise();
            BeanUtils.copyProperties(currentExpertise, copiedExpertise);
            copiedExpertise.setId(null);
            currentExpertise.setHasDraftCopy(true);
            currentExpertise.setHistory(true);
            copiedExpertise.setPublished(false);
            copiedExpertise.setParentExpertise(currentExpertise);
            copiedExpertise.setSector(getSector(expertiseDTO.getSector(), country));
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
            copiedExpertise.setUnion(getUnion(expertiseDTO.getUnion().getId(), expertiseDTO.getUnion().getName(), country));
            organizationGraphRepository.linkUnionSector(copiedExpertise.getUnion().getId(), copiedExpertise.getSector().getId());
            // NOW WE need to add the other seniority level which exists in expertise
            // since we have already
            seniorityLevelDTOList.addAll(copyExistingSeniorityLevelInExpertise(copiedExpertise, currentExpertise.getSeniorityLevel(), seniorityLevelToUpdate.get().getId()));
            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.setPublished(false);
            expertiseResponseDTO.setEditable(true);
            expertiseResponseDTO.setId(copiedExpertise.getId());
            expertiseResponseDTO.setParentId(currentExpertise.getId());
            expertiseResponseDTO.setSeniorityLevels(seniorityLevelDTOList);
            //expertiseResponseDTO.setSector(expertiseDTO.getSector());
            if (Optional.ofNullable(copiedExpertise.getSector()).isPresent()) {
                expertiseResponseDTO.getSector().setId(copiedExpertise.getSector().getId());
            }
            expertiseResponseDTO.getUnion().setId(copiedExpertise.getUnion().getId());

        } else {
            prepareExpertise(currentExpertise, expertiseDTO, country,false);
            // update in current expertise :)
            if (seniorityLevelToUpdate.isPresent()) {
                validateSeniorityLevel(currentExpertise.getSeniorityLevel(), expertiseDTO.getSeniorityLevel(), expertiseDTO.getSeniorityLevel().getId());
                boolean levelChanged = updateCurrentExpertise(countryId, currentExpertise, expertiseDTO);
                updateCurrentSeniorityLevel(expertiseDTO.getSeniorityLevel(), seniorityLevelToUpdate.get(), levelChanged);
            }
            // organization Level is changed so need to set new
            Optional.ofNullable(expertiseDTO.getSector()).ifPresent(sectorDTO -> {
                currentExpertise.setSector(getSector(expertiseDTO.getSector(), country));
            });
            Optional.ofNullable(expertiseDTO.getUnion()).ifPresent(unionIDNameDTO -> {
                currentExpertise.setUnion(getUnion(expertiseDTO.getUnion().getId(), expertiseDTO.getUnion().getName(), country));
            });

            expertiseGraphRepository.save(currentExpertise);
            expertiseDTO.setId(currentExpertise.getId());
            expertiseDTO.setPublished(currentExpertise.isPublished());

            expertiseResponseDTO = objectMapper.convertValue(expertiseDTO, ExpertiseResponseDTO.class);
            expertiseResponseDTO.getSeniorityLevels().add(expertiseDTO.getSeniorityLevel());
            if (Optional.ofNullable(currentExpertise.getSector()).isPresent() && Optional.ofNullable(expertiseResponseDTO.getSector()).isPresent()) {
                expertiseResponseDTO.getSector().setId(currentExpertise.getSector().getId());
            }
            if (Optional.ofNullable(currentExpertise.getUnion()).isPresent() && Optional.ofNullable(expertiseResponseDTO.getUnion()).isPresent()) {
                expertiseResponseDTO.getUnion().setId(currentExpertise.getUnion().getId());
            }
            if (currentExpertise.getUnion() != null && currentExpertise.getSector() != null) {
                organizationGraphRepository.linkUnionSector(currentExpertise.getUnion().getId(), currentExpertise.getSector().getId());
            }


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

    private boolean updateCurrentExpertise(Long countryId, Expertise expertise, ExpertiseDTO expertiseDTO) {
        boolean levelChanged = false;

        expertise.setName(expertiseDTO.getName().trim());
        expertise.setDescription(expertiseDTO.getDescription());
        expertise.setStartDateMillis(expertiseDTO.getStartDateMillis());
        expertise.setEndDateMillis(expertiseDTO.getEndDateMillis());
        expertise.setBreakPaymentSetting(expertiseDTO.getBreakPaymentSetting());
        if (expertiseDTO.getSeniorityLevel() != null) {
            PayGrade payGrade = seniorityLevelGraphRepository.getPayGradeBySeniorityLevelId(expertiseDTO.getSeniorityLevel().getId());
            if (payGrade != null && !payGrade.getId().equals(expertiseDTO.getSeniorityLevel().getPayGradeId())) {
                seniorityLevelGraphRepository.removePreviousPayGradeFromSeniorityLevel(expertiseDTO.getSeniorityLevel().getId());
            }
        }
        if (!expertise.getOrganizationLevel().getId().equals(expertiseDTO.getOrganizationLevelId())) {
            Level level = countryGraphRepository.getLevel(countryId, expertiseDTO.getOrganizationLevelId());
            if (!Optional.ofNullable(level).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.country.level.id.notFound", expertiseDTO.getOrganizationLevelId());

            }
            expertise.setOrganizationLevel(level);
            seniorityLevelGraphRepository.unlinkAllPayGradesFromExpertise(expertise.getId());
            levelChanged = true;
        }

        Set<OrganizationService> organizationService = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
        expertise.setOrganizationServices(organizationService);
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
            StaffExpertiseRelationShip staffExpertiseRelationShip = new StaffExpertiseRelationShip(currentStaff, currentExpertise, 0, DateUtils.getCurrentDate());
            staffExpertiseRelationShips.add(staffExpertiseRelationShip);
        }
        staffExpertiseRelationShipGraphRepository.saveAll(staffExpertiseRelationShips);
        return retrieveExpertiseDetails(currentStaff);

    }

    public Map<String, Object> getExpertiseToStaff(Long staffId) {
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
                    expertiseGraphRepository.addSkillInExpertise(expertiseId, skillId, DateUtils.getCurrentDate().getTime(), DateUtils.getCurrentDate().getTime());
                } else {
                    expertiseGraphRepository.updateExpertiseSkill(expertiseId, skillId, DateUtils.getCurrentDate().getTime());
                }
            }
        } else {
            expertiseGraphRepository.deleteExpertiseSkill(expertiseId, skillIds, DateUtils.getCurrentDate().getTime());
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



    public UnionServiceWrapper getUnionsAndService(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        UnionServiceWrapper unionServiceWrapper = new UnionServiceWrapper();
        unionServiceWrapper.setServices(organizationServiceService.getAllOrganizationService(countryId));
        unionServiceWrapper.setUnions(organizationGraphRepository.findAllUnionsByCountryId(countryId));
        unionServiceWrapper.setOrganizationLevels(countryGraphRepository.getLevelsByCountry(countryId));
        unionServiceWrapper.setSectors(ObjectMapperUtils.copyPropertiesOfListByMapper(sectorGraphRepository.findAllSectorsByCountryAndDeletedFalse(countryId), SectorDTO.class));
        return unionServiceWrapper;
    }

    /*This method is used to publish an expertise

     * */
    public ExpertiseQueryResult publishExpertise(Long expertiseId, Long publishedDateMillis) {
        List<SchedulerPanelDTO> schedulerPanelDTOS = new ArrayList<>();
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        validateExpertiseBeforePublishing(expertise);
        if (!Optional.ofNullable(expertise.getUnion().getId()).isPresent() || !organizationGraphRepository.isPublishedUnion(expertise.getUnion().getId())) {
            exceptionService.invalidRequestException("message.publish.expertise.union");
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
            parentExpertise.setEndDateMillis(new Date(publishedDateMillis - ONE_DAY).getTime());
            parentExpertise.setPublished(true);
            parentExpertise.setHistory(true);
            Expertise parentExp = expertiseGraphRepository.findOne(parentExpertise.getId());
            parentExp.setEndDateMillis(new Date(publishedDateMillis - ONE_DAY));
            parentExp.setHasDraftCopy(false);
            parentExp.setHistory(true);
            parentExp.setId(expertise.getId());
            expertiseGraphRepository.save(parentExp);
            if(isNotNull(parentExp.getEndDateMillis()) && parentExp.getEndDateMillis().after(DateUtils.getDate())) {
                schedulerPanelDTOS.add(new SchedulerPanelDTO(-1l,JobType.FUNCTIONAL, JobSubType.UNASSIGN_EXPERTISE_FROM_ACTIVITY, true, DateUtils.getLocalDateTimeFromDate(parentExp.getEndDateMillis()), BigInteger.valueOf(expertiseId), AppConstants.TIMEZONE_UTC));
            }
            expertise.setId(parentExpertise.getId());
            expertiseGraphRepository.save(expertise);
        }
        if(isNotNull(expertise.getEndDateMillis()) && expertise.getEndDateMillis().after(DateUtils.getDate())){
            schedulerPanelDTOS.add(new SchedulerPanelDTO(-1l, JobType.FUNCTIONAL, JobSubType.UNASSIGN_EXPERTISE_FROM_ACTIVITY, true, DateUtils.getLocalDateTimeFromDate(expertise.getEndDateMillis()), BigInteger.valueOf(expertiseId), AppConstants.TIMEZONE_UTC));
        }
        registerJobForUnassingExpertiesFromActivity(schedulerPanelDTOS);
        return parentExpertise;
    }


    private void validateExpertiseBeforePublishing(Expertise expertise) {
        if (expertise.isPublished()) {
            exceptionService.actionNotPermittedException("message.expertise.alreadyPublished");
        } else if (!Optional.ofNullable(expertise.getStartDateMillis()).isPresent()) {
            exceptionService.invalidRequestException("message.startDateMillis.null");
        } else if (!Optional.ofNullable(expertise.getBreakPaymentSetting()).isPresent()) {
            exceptionService.invalidRequestException("message.breakPaymentType.null");
        } else if (!Optional.ofNullable(expertise.getUnion()).isPresent()) {
            exceptionService.invalidRequestException("message.union.null");
        } else if (!Optional.ofNullable(expertise.getOrganizationServices()).isPresent()) {
            exceptionService.invalidRequestException("message.service.absent");
        } else if (!Optional.ofNullable(expertise.getOrganizationLevel()).isPresent()) {
            exceptionService.invalidRequestException("message.organizationLevel.null");
        } else if (!Optional.ofNullable(expertise.getSector()).isPresent()) {
            exceptionService.actionNotPermittedException("message.sector.absent");
        } else if (isCollectionEmpty(expertise.getSeniorityLevel())) {
            exceptionService.actionNotPermittedException("message.seniority_level.absent");
        }
    }


    public ExpertiseQueryResult getExpertiseById(Long expertiseId) {
        ExpertiseQueryResult expertise = expertiseGraphRepository.getExpertiseById(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", expertiseId);

        }
        return expertise;
    }


    private Map<String, Object> retrieveExpertiseDetails(Staff staff) {
        Map<String, Object> map = new HashMap<>();
        map.put("staffId", staff.getId());
        map.put("staffName", staff.getFirstName() + "   " + staff.getLastName());
        map.put("expertiseList", staffExpertiseRelationShipGraphRepository.getAllExpertiseByStaffId(staff.getId()));
        return map;
    }

    public List<ExpertiseQueryResult> getUnpublishedExpertise(Long countryId) {
        return expertiseGraphRepository.getUnpublishedExpertise(countryId);
    }


    public List<com.kairos.persistence.model.user.expertise.Response.ExpertiseDTO> getExpertiseByOrganizationSubType(Long countryId, Long organizationSubTypeId) {
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
    private void validateAgeRange(List<AgeRangeDTO> ageRangeDTO) {
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

    public CopyExpertiseDTO copyExpertise(Long expertiseId, CopyExpertiseDTO copyExpertiseDTO, Long countryId) {

        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "country", countryId);
        }
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
                copyExpertiseDTO.getNumberOfWorkingDaysInWeek() != null ? copyExpertiseDTO.getNumberOfWorkingDaysInWeek() : NUMBER_OF_WORKING_DAYS_IN_WEEK, copyExpertiseDTO.getBreakPaymentSetting(), false, false, false,
                getSector(copyExpertiseDTO.getSector(), country));

        prepareExpertiseWhileCopy(targetExpertise, copyExpertiseDTO, sourceExpertise.get(), country);
        copyExpertiseDTO.getSector().setId(targetExpertise.getSector().getId());
        return copyExpertiseDTO;
    }

    private void prepareExpertiseWhileCopy(Expertise targetExpertise, CopyExpertiseDTO expertiseDTO, Expertise sourceExpertise, Country country) {
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
        targetExpertise.setUnion(getUnion(expertiseDTO.getUnion().getId(), expertiseDTO.getUnion().getName(), country));
        expertiseGraphRepository.save(targetExpertise);
        createDefaultSettings(targetExpertise, sourceExpertise);
        expertiseDTO.setId(targetExpertise.getId());
        expertiseDTO.getUnion().setId(targetExpertise.getUnion().getId());

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
        List<ExpertiseEmploymentTypeRelationship> expertiseEmploymentList = new ArrayList<>();
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

    public SeniorAndChildCareDaysDTO getSeniorAndChildCareDays(Long expertiseId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        List<CareDaysDTO> childCareDays = ObjectMapperUtils.copyPropertiesOfListByMapper(expertise.getChildCareDays(), CareDaysDTO.class);
        List<CareDaysDTO> seniorDays = ObjectMapperUtils.copyPropertiesOfListByMapper(expertise.getSeniorDays(), CareDaysDTO.class);
        return new SeniorAndChildCareDaysDTO(seniorDays, childCareDays);
    }

    private Organization getUnion(Long unionId, String unionName, Country country) {
        Organization union;
        if (Optional.ofNullable(unionId).isPresent()) {
            union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unionId);
            if (!Optional.ofNullable(union).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "union", unionId);
            }
        } else {
            union = new Organization(unionName, true, country);
        }
        return union;
    }

    private Sector getSector(SectorDTO sectorDTO, Country country) {
        Sector sector = null;
        if (Optional.ofNullable(sectorDTO).isPresent()) {
            if (Optional.ofNullable(sectorDTO.getId()).isPresent()) {
                sector = new Sector(sectorDTO.getId(), sectorDTO.getName());
            } else {
                sector = new Sector(sectorDTO.getName());
                sector.setCountry(country);
            }
        }
        return sector;
    }

    public Map<String, Object> getPlannedTimeAndEmploymentTypeForUnit(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(organization).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.id.notFound", unitId);
        }
        return getPlannedTimeAndEmploymentType(organization.getCountry().getId());
    }

    //register a job for unassign expertise from activity and this method call when set enddate of publish expertise
    public boolean registerJobForUnassingExpertiesFromActivity(List<SchedulerPanelDTO> schedulerPanelDTOS)
    {
        LOGGER.info("create job for add planning period");
        schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, null, true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
        LOGGER.info("successfully job registered of add planning period");
        return isCollectionNotEmpty(schedulerPanelDTOS);
    }

}
