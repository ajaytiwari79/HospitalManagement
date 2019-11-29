package com.kairos.service.expertise;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.ActionNotPermittedException;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.dto.user.country.experties.ExpertiseEmploymentTypeDTO;
import com.kairos.dto.user.country.experties.SeniorityLevelDTO;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.dto.user.organization.union.SectorDTO;
import com.kairos.dto.user.organization.union.UnionIDNameDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.employment_type.EmploymentTypeQueryResult;
import com.kairos.persistence.model.country.experties.UnionServiceWrapper;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.Unit;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.union.Sector;
import com.kairos.persistence.model.pay_table.PayGrade;
import com.kairos.persistence.model.query_wrapper.CountryHolidayCalendarQueryResult;
import com.kairos.persistence.model.user.expertise.*;
import com.kairos.persistence.model.user.expertise.response.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.organization.OrganizationServiceRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.organization.union.SectorGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.FunctionalPaymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.country.CountryService;
import com.kairos.service.employment.EmploymentService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationServiceService;
import com.kairos.dto.user_context.UserContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getEndOfDayFromLocalDate;
import static com.kairos.commons.utils.DateUtils.getLocalDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.DAY;
import static com.kairos.constants.UserMessagesConstants.*;

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
    private CountryService countryService;
    @Inject
    private
    UnitGraphRepository unitGraphRepository;
    @Inject
    private
    OrganizationServiceRepository organizationServiceRepository;
    @Inject
    private
    OrganizationServiceService organizationServiceService;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private EmploymentService employmentService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
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
    private SectorGraphRepository sectorGraphRepository;


    public ExpertiseQueryResult saveExpertise(Long countryId, ExpertiseDTO expertiseDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, COUNTRY, countryId);
        }
        boolean isExpertiseExists = expertiseGraphRepository.findExpertiseByUniqueName("(?i)" + expertiseDTO.getName().trim());
        if (isExpertiseExists) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, EXPERTISE, expertiseDTO.getName());
        }
        Optional.ofNullable(expertiseDTO.getUnion()).ifPresent(unionIDNameDTO -> {
            if (expertiseDTO.isPublished() && (!Optional.ofNullable(unionIDNameDTO.getId()).isPresent() || !unitGraphRepository.isPublishedUnion(unionIDNameDTO.getId()))) {
                exceptionService.invalidRequestException(MESSAGE_PUBLISH_EXPERTISE_UNION);
            }
        });
        validateSeniorityLevels(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertiseDTO.getSeniorityLevels(), SeniorityLevel.class));
        ExpertiseLine expertiseLine = createExpertiseLine(expertiseDTO);
        Expertise expertise = new Expertise(expertiseDTO.getName(), expertiseDTO.getDescription(), expertiseDTO.getStartDate(), expertiseDTO.getEndDate(), country, expertiseDTO.isPublished(), Collections.singletonList(expertiseLine),expertiseDTO.getBreakPaymentSetting());
        setBasicDetails(expertiseDTO, country, expertise);
        addSeniorityLevelsInExpertise(expertiseLine, expertiseDTO, expertise);
        linkProtectedDaysOffSetting(new ArrayList<>(), Arrays.asList(expertise));
        TimeSlot timeSlot = new TimeSlot(NIGHT_START_HOUR, NIGHT_END_HOUR);
        ExpertiseNightWorkerSettingDTO expertiseNightWorkerSettingDTO = new ExpertiseNightWorkerSettingDTO(timeSlot, null,
                null, null, null, null, countryId, expertise.getId());
        genericRestClient.publish(expertiseNightWorkerSettingDTO, countryId, false, IntegrationOperation.CREATE,
                "/expertise/" + expertise.getId() + "/night_worker_setting", null);
        return updatedExpertiseData(expertise);
    }

    private void setBasicDetails(ExpertiseDTO expertiseDTO, Country country, Expertise expertise) {
        countryGraphRepository.addLevel(expertise.getId(),expertiseDTO.getOrganizationLevelId());
        if(expertiseDTO.getSector()!=null){
            organizationGraphRepository.addSector(expertise.getId(),expertiseDTO.getSector().getId());
        }
        if(expertiseDTO.getUnion()!=null){
            organizationGraphRepository.addUnion(expertise.getId(),expertiseDTO.getUnion().getId());
        }
    }

    public ExpertiseQueryResult updateExpertise(Long countryId, ExpertiseDTO expertiseDTO, Long expertiseId) {
        if (StringUtils.isBlank(expertiseDTO.getName())) {
            exceptionService.actionNotPermittedException("error.Expertise.name.notEmpty");
        }
        if (expertiseDTO.getEndDate() != null && expertiseDTO.getStartDate().isAfter(expertiseDTO.getEndDate())) {
            exceptionService.actionNotPermittedException("message.start_date.less_than.end_date");
        }
        expertiseDTO.setId(expertiseId);
        Expertise currentExpertise = expertiseGraphRepository.findOne(expertiseId);
        Country country = countryGraphRepository.findOne(countryId);
        validateExpertiseDetails(countryId, expertiseDTO, expertiseId, currentExpertise, country);
        currentExpertise.setName(expertiseDTO.getName());
        currentExpertise.setStartDate(expertiseDTO.getStartDate());
        currentExpertise.setEndDate(expertiseDTO.getEndDate());
        if (!currentExpertise.isPublished()) {
            currentExpertise.getExpertiseLines().get(0).setEndDate(currentExpertise.getEndDate());
            currentExpertise.setBreakPaymentSetting(expertiseDTO.getBreakPaymentSetting());
            setBasicDetails(expertiseDTO, country, currentExpertise);
        } else {
            currentExpertise.getExpertiseLines().sort(Comparator.comparing(ExpertiseLine::getStartDate));
            LocalDate startDateOfLastLine = currentExpertise.getExpertiseLines().get(currentExpertise.getExpertiseLines().size() - 1).getStartDate();
            if ((expertiseDTO.getEndDate() != null && !startDateOfLastLine.isBefore(expertiseDTO.getEndDate()))) {
                exceptionService.actionNotPermittedException("message.start_date.less_than.end_date");
            }
            currentExpertise.setEndDate(expertiseDTO.getEndDate());
            currentExpertise.getExpertiseLines().get(currentExpertise.getExpertiseLines().size()-1).setEndDate(expertiseDTO.getEndDate());
            employmentService.setEndDateInEmploymentOfExpertise(expertiseDTO);
        }
        expertiseGraphRepository.save(currentExpertise);
        return updatedExpertiseData(currentExpertise);
    }

    private void validateExpertiseDetails(Long countryId, ExpertiseDTO expertiseDTO, Long expertiseId, Expertise currentExpertise, Country country) {
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, COUNTRY, countryId);
        }
        if (!Optional.ofNullable(currentExpertise).isPresent() || currentExpertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, EXPERTISE, expertiseId);
        }
        if (!currentExpertise.getName().equalsIgnoreCase(expertiseDTO.getName().trim())) {
            boolean isExpertiseExists = expertiseGraphRepository.findExpertiseByUniqueName("(?i)" + expertiseDTO.getName().trim());
            if (isExpertiseExists) {
                exceptionService.duplicateDataException(MESSAGE_DUPLICATE, EXPERTISE);
            }
        }
    }

    public ExpertiseQueryResult updateExpertiseLine(ExpertiseDTO expertiseDTO, Long expertiseId, Long expertiseLineId) {
        expertiseDTO.setExpertiseLineId(expertiseLineId);
        Expertise expertise = expertiseGraphRepository.findById(expertiseId, 2).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_DATANOTFOUND, EXPERTISE, expertiseDTO.getId())));
        validateSeniorityLevels(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertiseDTO.getSeniorityLevels(), SeniorityLevel.class));
        ExpertiseLine currentExpertiseLine = expertise.getExpertiseLines().stream().filter(k -> k.getId().equals(expertiseLineId)).findFirst().orElseThrow(() -> new ActionNotPermittedException(exceptionService.convertMessage(PLEASE_PROVIDE_THE_VALID_LINE_ID)));
        expertise.getExpertiseLines().sort(Comparator.comparing(ExpertiseLine::getStartDate));
        if (expertise.isPublished() && isExpertiseLineChanged(currentExpertiseLine, expertiseDTO.getOrganizationServiceIds(), expertiseDTO) && currentExpertiseLine.getStartDate().isBefore(expertiseDTO.getStartDate())) {
            if (expertiseDTO.getStartDate().isBefore(getLocalDate()) && (currentExpertiseLine.getEndDate() == null || currentExpertiseLine.getEndDate().isAfter(getLocalDate()))) {
                exceptionService.actionNotPermittedException(PLEASE_SELECT_PUBLISHED_DATE_LESS_AFTER_CURRENT_LINE_START_DATE);
            }
            if (isNotNull(expertise.getEndDate()) && !expertiseDTO.getStartDate().isBefore(expertise.getEndDate())) {
                exceptionService.actionNotPermittedException(PLEASE_SELECT_PUBLISHED_DATE_BEFORE_EXPERTISE_END_DATE);
            }
            ExpertiseLine expertiseLine = createExpertiseLine(expertiseDTO);
            currentExpertiseLine.setEndDate(expertiseDTO.getStartDate().minusDays(1L));
            if (expertise.getExpertiseLines().size() - 1 > expertise.getExpertiseLines().indexOf(currentExpertiseLine)) {
                expertiseLine.setEndDate(expertise.getExpertiseLines().get(expertise.getExpertiseLines().indexOf(currentExpertiseLine) + 1).getStartDate().minusDays(1));
            }
            expertise.getExpertiseLines().add(expertiseLine);
            expertiseDTO.getSeniorityLevels().forEach(k -> k.setId(null));
            addSeniorityLevelsInExpertise(expertiseLine, expertiseDTO, expertise);
            expertiseGraphRepository.save(expertise);
            employmentService.triggerEmploymentLine(expertiseId, expertiseLine);
        } else {
            if(seniorityLevelChanged(expertiseDTO)){
                //expertiseGraphRepository.removeSeniorityLevel(expertiseLineId);
                addSeniorityLevelsInExpertise(currentExpertiseLine, expertiseDTO, expertise);
            }
            updateExistingLine(expertiseDTO, expertise, currentExpertiseLine);
        }
        return updatedExpertiseData(expertise);
    }

    public void updateExistingLine(ExpertiseDTO expertiseDTO, Expertise expertise, ExpertiseLine currentExpertiseLine) {
        setBasicDetails(expertiseDTO, expertise.getCountry(), expertise);
        initializeExpertiseLine(currentExpertiseLine, expertiseDTO);
        expertiseGraphRepository.save(expertise);
    }

    public List<ExpertiseQueryResult> getAllExpertise(long countryId) {
        List<ExpertiseQueryResult> expertiseQueryResults = expertiseGraphRepository.getAllExpertise(countryId, new boolean[]{true});
        List<Long> allExpertiseIds = expertiseQueryResults.stream().map(ExpertiseQueryResult::getId).collect(Collectors.toList());
        List<ExpertiseLineQueryResult> expertiseLineQueryResults = expertiseGraphRepository.findAllExpertiseLines(allExpertiseIds);
        Map<Long, List<ExpertiseLineQueryResult>> expertiseLineQueryResultMap = expertiseLineQueryResults.stream().collect(Collectors.groupingBy(ExpertiseLineQueryResult::getExpertiseId));
        expertiseQueryResults.forEach(expertiseQueryResult -> expertiseQueryResult.setExpertiseLines(expertiseLineQueryResultMap.get(expertiseQueryResult.getId())));
        return expertiseQueryResults;
    }

    private boolean validateSeniorityLevels(List<SeniorityLevel> seniorityLevels) {
        Collections.sort(seniorityLevels);
        if (isCollectionNotEmpty(seniorityLevels) && seniorityLevels.get(0).getTo() != null && seniorityLevels.get(0).getTo() <= seniorityLevels.get(0).getFrom()) {
            exceptionService.actionNotPermittedException(PLEASE_ENTER_VALID_SENIORITY_LEVELS);
        }
        if (seniorityLevels.size() > 1) {
            for (int i = 0; i < seniorityLevels.size() - 1; i++) {
                if (seniorityLevels.get(i).getTo() != null && seniorityLevels.get(i).getTo() <= seniorityLevels.get(i).getFrom() || !seniorityLevels.get(i).getTo().equals(seniorityLevels.get(i + 1).getFrom())) {
                    exceptionService.actionNotPermittedException(PLEASE_ENTER_VALID_SENIORITY_LEVELS);
                }
            }
        }
        return true;

    }


    public boolean deleteExpertise(Long expertiseId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId, 2);
        if (!Optional.ofNullable(expertise).isPresent() || expertise.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EXPERTISE_ID_NOTFOUND, expertiseId);

        }
        if (expertise.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_EXPERTISE_CANNOTREMOVED);
        }
        expertise.setDeleted(true);
        if (Optional.ofNullable(expertise.getExpertiseLines().get(0).getSeniorityLevel()).isPresent() && !expertise.getExpertiseLines().get(0).getSeniorityLevel().isEmpty()) {
            for (SeniorityLevel seniorityLevel : expertise.getExpertiseLines().get(0).getSeniorityLevel())
                seniorityLevel.setDeleted(true);
        }
        expertiseGraphRepository.save(expertise);
        return true;
    }


    public UnionServiceWrapper getUnionsAndService(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        UnionServiceWrapper unionServiceWrapper = new UnionServiceWrapper();
        unionServiceWrapper.setServices(organizationServiceService.getAllOrganizationService(countryId));
        unionServiceWrapper.setUnions(unitGraphRepository.findAllUnionsByCountryId(countryId));
        unionServiceWrapper.setOrganizationLevels(countryGraphRepository.getLevelsByCountry(countryId));
        unionServiceWrapper.setSectors(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(sectorGraphRepository.findAllSectorsByCountryAndDeletedFalse(countryId), SectorDTO.class));
        return unionServiceWrapper;
    }


    private void validateExpertiseBeforePublishing(Expertise expertise) {
        if (expertise.isPublished()) {
            exceptionService.actionNotPermittedException(MESSAGE_EXPERTISE_ALREADYPUBLISHED);
        } else if (!Optional.ofNullable(expertise.getStartDate()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_STARTDATEMILLIS_NULL);
        } else if (!Optional.ofNullable(expertise.getBreakPaymentSetting()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_BREAKPAYMENTTYPE_NULL);
        } else if (!Optional.ofNullable(expertise.getUnion()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_UNION_NULL);
        } else if (!Optional.ofNullable(expertise.getExpertiseLines().get(0).getOrganizationServices()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_SERVICE_ABSENT);
        } else if (!Optional.ofNullable(expertise.getOrganizationLevel()).isPresent()) {
            exceptionService.invalidRequestException(MESSAGE_ORGANIZATIONLEVEL_NULL);
        } else if (!Optional.ofNullable(expertise.getSector()).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_SECTOR_ABSENT);
        } else if (isCollectionEmpty(expertise.getExpertiseLines().get(0).getSeniorityLevel())) {
            exceptionService.actionNotPermittedException(MESSAGE_SENIORITY_LEVEL_ABSENT);
        }
    }


    public ExpertiseQueryResult getExpertiseById(Long expertiseId) {
        ExpertiseQueryResult expertise = expertiseGraphRepository.getExpertiseById(expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EXPERTISE_ID_NOTFOUND, expertiseId);
        }
        List<ExpertiseLineQueryResult> expertiseLineQueryResults = expertiseGraphRepository.findAllExpertiseLines(Arrays.asList(expertiseId));
        expertise.setExpertiseLines(expertiseLineQueryResults);
        return expertise;
    }


    public List<ExpertiseQueryResult> getUnpublishedExpertise(Long countryId) {
        List<ExpertiseQueryResult> expertiseQueryResults = expertiseGraphRepository.getAllExpertise(countryId, new boolean[]{true, false});
        List<Long> allExpertiseIds = expertiseQueryResults.stream().map(ExpertiseQueryResult::getId).collect(Collectors.toList());
        List<ExpertiseLineQueryResult> expertiseLineQueryResults = expertiseGraphRepository.findAllExpertiseLines(allExpertiseIds);
        Map<Long, List<ExpertiseLineQueryResult>> expertiseLineQueryResultMap = expertiseLineQueryResults.stream().collect(Collectors.groupingBy(ExpertiseLineQueryResult::getExpertiseId));
        expertiseQueryResults.forEach(expertiseQueryResult -> expertiseQueryResult.setExpertiseLines(expertiseLineQueryResultMap.get(expertiseQueryResult.getId())));
        return expertiseQueryResults;
    }


    public List<com.kairos.persistence.model.user.expertise.response.ExpertiseDTO> getExpertiseByOrganizationSubType(Long countryId, Long organizationSubTypeId) {
        return expertiseGraphRepository.getExpertiseByOrganizationSubType(countryId, organizationSubTypeId);
    }

    public Boolean addPlannedTimeInExpertise(Long expertiseId, ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        if (isNull(expertise)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EXPERTISE_ID_NOTFOUND, expertiseEmploymentTypeDTO.getExpertiseId());
        }
        linkEmploymentTypeWithExpertise(expertiseEmploymentTypeDTO, expertise);
        return true;
    }

    public List<ExpertisePlannedTimeQueryResult> getPlannedTimeInExpertise(Long expertiseId) {
        Optional<Expertise> expertise = expertiseGraphRepository.findById(expertiseId);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_EXPERTISE_ID_NOTFOUND, expertiseId);
        }
        return expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(expertiseId);
    }

    public Boolean updatePlannedTimeInExpertise(Long expertiseId, ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO) {
        Expertise expertise = expertiseGraphRepository.findById(expertiseId).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_EXPERTISE_ID_NOTFOUND, expertiseId)));
        expertiseEmploymentTypeRelationshipGraphRepository.removeAllPreviousEmploymentType(expertiseId);
        linkEmploymentTypeWithExpertise(expertiseEmploymentTypeDTO, expertise);
        return true;
    }

    private void linkEmploymentTypeWithExpertise(ExpertiseEmploymentTypeDTO expertiseEmploymentTypeDTO, Expertise expertise) {
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
        List<PresenceTypeDTO> presenceTypes = ObjectMapperUtils.copyPropertiesOfCollectionByMapper
                (genericRestClient.publish(null, countryId, false, IntegrationOperation.GET, "/plannedTimeType", countryDetail), PresenceTypeDTO.class);

        countryDetail.put("employmentTypes", employmentTypes);
        countryDetail.put("presenceTypes", presenceTypes);
        return countryDetail;
    }

    public List<ExpertiseTagDTO> getExpertiseForOrgCTA(long unitId) {
        Long countryId = countryService.getCountryIdByUnitId(unitId);
        return expertiseGraphRepository.getAllExpertiseWithTagsByCountry(countryId);
    }


    public ProtectedDaysOffSettingDTO addOrUpdateProtectedDaysOffSetting(Long expertiseId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO) {
        Expertise expertise = expertiseGraphRepository.findById(expertiseId).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_DATANOTFOUND, EXPERTISE, expertiseId)));
        CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult = countryGraphRepository.findByCalendarHolidayId(protectedDaysOffSettingDTO.getHolidayId());
        if (isNull(countryHolidayCalendarQueryResult)) {
            exceptionService.dataNotMatchedException(MESSAGE_DATANOTFOUND, DAY, DAY_TYPE, protectedDaysOffSettingDTO.getHolidayId());
        }
        protectedDaysOffSettingDTO.setDayTypeId(countryHolidayCalendarQueryResult.getDayType().getId());
        protectedDaysOffSettingDTO.setPublicHolidayDate(countryHolidayCalendarQueryResult.getHolidayDate());
        expertise.getProtectedDaysOffSettings().add(ObjectMapperUtils.copyPropertiesByMapper(protectedDaysOffSettingDTO, ProtectedDaysOffSetting.class));
        expertiseGraphRepository.save(expertise);
        ProtectedDaysOffSetting protectedDaysOffSettings = expertise.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.getHolidayId().equals(protectedDaysOffSettingDTO.getHolidayId())).findAny().orElse(new ProtectedDaysOffSetting());
        protectedDaysOffSettingDTO.setId(protectedDaysOffSettings.getId());
        return protectedDaysOffSettingDTO;
    }


    public List<ProtectedDaysOffSettingDTO> getProtectedDaysOffSetting(Long expertiseId){
        Expertise expertise = expertiseGraphRepository.findById(expertiseId).orElseThrow(()->new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_DATANOTFOUND, EXPERTISE, expertiseId)));
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertise.getProtectedDaysOffSettings(),ProtectedDaysOffSettingDTO.class);
    }


    private void createDefaultSettings(Long targetExpertiseId, Long sourceExpertiseId) {
        List<ExpertiseEmploymentTypeRelationship> expertiseEmploymentList = new ArrayList<>();
        ExpertisePlannedTimeQueryResult expertiseEmploymentTypeRelationships = expertiseEmploymentTypeRelationshipGraphRepository.getPlannedTimeConfigurationByExpertise(sourceExpertiseId);
        if (Optional.ofNullable(expertiseEmploymentTypeRelationships).isPresent()) {
            expertiseEmploymentTypeRelationships.getEmploymentTypes().forEach(employmentType -> {
                ExpertiseEmploymentTypeRelationship expertiseEmploymentTypeRelationship = new ExpertiseEmploymentTypeRelationship(expertiseGraphRepository.findOne(targetExpertiseId),
                        employmentType, expertiseEmploymentTypeRelationships.getIncludedPlannedTime(), expertiseEmploymentTypeRelationships.getExcludedPlannedTime());
                expertiseEmploymentList.add(expertiseEmploymentTypeRelationship);
            });
            expertiseEmploymentTypeRelationshipGraphRepository.saveAll(expertiseEmploymentList);
        }
        functionalPaymentGraphRepository.linkFunctionalPaymentInExpertise(sourceExpertiseId, targetExpertiseId);


    }

    public SeniorAndChildCareDaysDTO getSeniorAndChildCareDays(Long expertiseId) {
        Expertise expertise = expertiseGraphRepository.findOne(expertiseId);
        List<CareDaysDTO> childCareDays = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertise.getChildCareDays(), CareDaysDTO.class);
        List<CareDaysDTO> seniorDays = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertise.getSeniorDays(), CareDaysDTO.class);
        return new SeniorAndChildCareDaysDTO(seniorDays, childCareDays);
    }

    public Map<Long,SeniorAndChildCareDaysDTO> getSeniorAndChildCareDaysMapByExpertiseIds(List<Long> expertiseId) {
        Map<Long,SeniorAndChildCareDaysDTO> seniorAndChildCareDaysDTOMap=new HashMap<>();
        List<Expertise> expertises = expertiseGraphRepository.findAllById(expertiseId);
        for (Expertise expertise : expertises) {
            List<CareDaysDTO> childCareDays = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertise.getChildCareDays(), CareDaysDTO.class);
            List<CareDaysDTO> seniorDays = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertise.getSeniorDays(), CareDaysDTO.class);
            seniorAndChildCareDaysDTOMap.put(expertise.getId(), new SeniorAndChildCareDaysDTO(seniorDays, childCareDays));
        }
        return seniorAndChildCareDaysDTOMap;
    }


    private Organization getUnion(Long unionId, String unionName, Country country) {
        Organization union;
        if (Optional.ofNullable(unionId).isPresent()) {
            union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unionId);
            if (!Optional.ofNullable(union).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, UNION, unionId);
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
        Unit unit = unitGraphRepository.findOne(unitId);
        if (!Optional.ofNullable(unit).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_ORGANIZATION_ID_NOTFOUND, unitId);
        }
        Long countryId = countryService.getCountryIdByUnitId(unitId);
        return getPlannedTimeAndEmploymentType(countryId);
    }

    //register a job for unassign expertise from activity and this method call when set enddate of publish expertise
    public boolean registerJobForUnassingExpertiesFromActivity(List<SchedulerPanelDTO> schedulerPanelDTOS) {
        if (isCollectionNotEmpty(schedulerPanelDTOS)) {
            LOGGER.info("create job for add planning period");
            // using -1 for unitId becounse this is not unit base job
            schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, -1L, true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
            });
            LOGGER.info("successfully job registered of add planning period");
        }
        return isCollectionNotEmpty(schedulerPanelDTOS);
    }

    private boolean isExpertiseLineChanged(ExpertiseLine expertiseLine, List<Long> organizationServiceIds, ExpertiseDTO expertiseDTO) {
        return expertiseLine.getFullTimeWeeklyMinutes() != expertiseDTO.getFullTimeWeeklyMinutes() ||
                expertiseLine.getNumberOfWorkingDaysInWeek() != expertiseDTO.getNumberOfWorkingDaysInWeek() ||
                expertiseLine.getOrganizationServices().size() > organizationServiceIds.size() ||
                seniorityLevelChanged(expertiseDTO);
    }


    private ExpertiseLine createExpertiseLine(ExpertiseDTO expertiseDTO) {
        List<OrganizationService> organizationServices = organizationServiceRepository.findAllOrganizationServicesByIds(expertiseDTO.getOrganizationServiceIds());
        return new ExpertiseLine.ExpertiseLineBuilder().setStartDate(expertiseDTO.getStartDate()).setEndDate(expertiseDTO.getEndDate())
                .setNumberOfWorkingDaysInWeek(expertiseDTO.getNumberOfWorkingDaysInWeek()).setFullTimeWeeklyMinutes(expertiseDTO.getFullTimeWeeklyMinutes()).setOrganizationServices(organizationServices).createLine();
    }

    private void initializeExpertiseLine(ExpertiseLine expertiseLine, ExpertiseDTO expertiseDTO) {
        organizationServiceRepository.addServices(expertiseLine.getId(), expertiseDTO.getOrganizationServiceIds());
        expertiseLine.setFullTimeWeeklyMinutes(expertiseDTO.getFullTimeWeeklyMinutes());
        expertiseLine.setNumberOfWorkingDaysInWeek(expertiseDTO.getNumberOfWorkingDaysInWeek());
    }

    public boolean publishExpertise(Long expertiseId) {
        List<SchedulerPanelDTO> schedulerPanelDTOS = new ArrayList<>();
        Expertise expertise = expertiseGraphRepository.findById(expertiseId, 2).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_EXPERTISE_ID_NOTFOUND, expertiseId)));
        validateExpertiseBeforePublishing(expertise);
        List<Long> seniorityLevelId = new ArrayList<>();
        for (SeniorityLevel seniorityLevel : expertise.getExpertiseLines().get(0).getSeniorityLevel()) {
            seniorityLevel.setPublished(true);
            seniorityLevelId.add(seniorityLevel.getId());
        }
        boolean payGradesExistsForSeniorityLevels = seniorityLevelGraphRepository.checkPayGradesInSeniorityLevel(seniorityLevelId);
        if (!payGradesExistsForSeniorityLevels) {
            exceptionService.actionNotPermittedException(MESSAGE_SENIORITYLEVEL_PAYGRADE_MISSING);
        }
        expertise.setPublished(true);
        expertiseGraphRepository.save(expertise);
        if (isNotNull(expertise.getEndDate()) && expertise.getEndDate().isBefore(getLocalDate())) {
            schedulerPanelDTOS.add(new SchedulerPanelDTO(JobType.FUNCTIONAL, JobSubType.UNASSIGN_EXPERTISE_FROM_ACTIVITY, true, getEndOfDayFromLocalDate(expertise.getEndDate()), BigInteger.valueOf(expertiseId), AppConstants.TIMEZONE_UTC));
        }

        // create job for unassign experties from activity
        try {
            registerJobForUnassingExpertiesFromActivity(schedulerPanelDTOS);
        } catch (Exception e) {
            LOGGER.info("Exception occured in scheduling job for unassign experties from activity");
        }
        return true;
    }

    private void addSeniorityLevelsInExpertise(ExpertiseLine expertiseLine, ExpertiseDTO expertiseDTO, Expertise expertise) {
        Set<Long> payGradeIds = expertiseDTO.getSeniorityLevels().stream().map(SeniorityLevelDTO::getPayGradeId).collect(Collectors.toSet());
        List<PayGrade> payGrades = payGradeGraphRepository.getAllPayGradesById(payGradeIds);
        Map<Long, PayGrade> payGradeMap = payGrades.stream().collect(Collectors.toMap(PayGrade::getId, v -> v));
        List<SeniorityLevel> seniorityLevels = new ArrayList<>();
        expertiseDTO.getSeniorityLevels().forEach(seniorityLevelDTO -> {
            SeniorityLevel seniorityLevel = new SeniorityLevel(seniorityLevelDTO.getId(), seniorityLevelDTO.getFrom(), seniorityLevelDTO.getTo(), payGradeMap.get(seniorityLevelDTO.getPayGradeId()), seniorityLevelDTO.getPensionPercentage(), seniorityLevelDTO.getFreeChoicePercentage(),
                    seniorityLevelDTO.getFreeChoiceToPension(), false);
            seniorityLevels.add(seniorityLevel);
        });
        expertiseLine.setSeniorityLevel(seniorityLevels);
        //expertiseGraphRepository.save(expertise, 2);
    }

    public boolean linkProtectedDaysOffSetting(List<CountryHolidayCalendarQueryResult> countryHolidayCalendarQueryResults, List<Expertise> expertises) {
        if (ObjectUtils.isCollectionEmpty(expertises)) {
            expertises = expertiseGraphRepository.getAllExpertiseByCountry(UserContext.getUserDetails().getCountryId());
        }
        if (ObjectUtils.isCollectionEmpty(countryHolidayCalendarQueryResults)) {
            countryHolidayCalendarQueryResults = countryGraphRepository.findAllCalendarHoliday();
        }
        List<ProtectedDaysOffSetting> protectedDaysOffSettings = new ArrayList<>();
        for (CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult : countryHolidayCalendarQueryResults) {
            if (!countryHolidayCalendarQueryResult.getDayType().isAllowTimeSettings() && countryHolidayCalendarQueryResult.getDayType().isHolidayType())
                protectedDaysOffSettings.add(new ProtectedDaysOffSetting(countryHolidayCalendarQueryResult.getId(), countryHolidayCalendarQueryResult.getHolidayDate(), true, countryHolidayCalendarQueryResult.getDayType().getId()));
        }
        for (Expertise expertise : expertises) {
            expertise.getProtectedDaysOffSettings().addAll(protectedDaysOffSettings);
        }
        expertiseGraphRepository.saveAll(expertises);
        return true;
    }

    public ExpertiseQueryResult copyExpertise(Long expertiseId, ExpertiseDTO expertiseDTO) {
        Expertise expertise = expertiseGraphRepository.findById(expertiseId, 2).orElseThrow(() -> new DataNotFoundByIdException(exceptionService.convertMessage("Data not found")));
        ExpertiseLine expertiseLine = (expertise.getExpertiseLines().get(expertise.getExpertiseLines().size() - 1));
        expertiseDTO.setUnion(ObjectMapperUtils.copyPropertiesByMapper(expertise.getUnion(), UnionIDNameDTO.class));
        Map<Long, Long> seniorityLevelAndPayGradeIdMap = expertiseLine.getSeniorityLevel().stream().collect(Collectors.toMap(UserBaseEntity::getId, v -> v.getPayGrade().getId()));
        expertiseDTO.setSeniorityLevels(ObjectMapperUtils.copyPropertiesOfCollectionByMapper(expertiseLine.getSeniorityLevel(), SeniorityLevelDTO.class));
        expertiseDTO.getSeniorityLevels().forEach(s -> {
            s.setPayGradeId(seniorityLevelAndPayGradeIdMap.get(s.getId()));
            s.setId(null);
        });
        expertiseDTO.setOrganizationLevelId(isNull(expertise.getOrganizationLevel()) ? null : expertise.getOrganizationLevel().getId());
        expertiseDTO.setOrganizationServiceIds(isCollectionEmpty(expertiseLine.getOrganizationServices()) ? null : expertiseLine.getOrganizationServices().stream().map(UserBaseEntity::getId).collect(Collectors.toList()));
        expertiseDTO.setSector(ObjectMapperUtils.copyPropertiesByMapper(expertise.getSector(), SectorDTO.class));
        expertiseDTO.setBreakPaymentSetting(expertise.getBreakPaymentSetting());
        expertiseDTO.setNumberOfWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek());
        expertiseDTO.setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes());
        ExpertiseQueryResult createdExpertise = saveExpertise(expertise.getCountry().getId(), expertiseDTO);
        createDefaultSettings(expertiseId, createdExpertise.getId());
        return createdExpertise;

    }

    private ExpertiseQueryResult updatedExpertiseData(Expertise expertise) {
        ExpertiseQueryResult expertiseQueryResult = ObjectMapperUtils.copyPropertiesByMapper(expertise, ExpertiseQueryResult.class);
        List<ExpertiseLineQueryResult> expertiseLineQueryResults = expertiseGraphRepository.findAllExpertiseLines(Collections.singletonList(expertiseQueryResult.getId()));
        expertiseQueryResult.setExpertiseLines(expertiseLineQueryResults);
        return expertiseQueryResult;
    }

    private boolean seniorityLevelChanged(ExpertiseDTO expertiseDTO) {
        List<SeniorityLevel> seniorityLevels = seniorityLevelGraphRepository.findAllByLineId(expertiseDTO.getExpertiseLineId());
        Collections.sort(seniorityLevels);
        if (expertiseDTO.getSeniorityLevels().size() != seniorityLevels.size()) {
            return true;
        }
        for (int i = 0; i < seniorityLevels.size(); i++) {
            SeniorityLevel seniorityLevel = seniorityLevels.get(i);
            SeniorityLevelDTO seniorityLevelDTO = expertiseDTO.getSeniorityLevels().get(i);
            if (!isEquals(seniorityLevel.getFrom(), seniorityLevelDTO.getFrom()) || !isEquals(seniorityLevel.getTo(), seniorityLevelDTO.getTo()) || !seniorityLevel.getPayGrade().getId().equals(seniorityLevelDTO.getPayGradeId())) {
                return true;
            }
        }
        return false;
    }

    public Expertise findById(Long id,int depth){
       return expertiseGraphRepository.findById(id,depth).orElseThrow(()->new DataNotFoundByIdException(exceptionService.convertMessage(MESSAGE_DATANOTFOUND, EXPERTISE, id)));
    }

}
