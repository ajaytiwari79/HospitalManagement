package com.kairos.service.unit_position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTAWTAWrapper;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.auth.User;

import com.kairos.persistence.model.client.query_results.ClientMinimumDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.staff.StaffExperienceInExpertiseDTO;
import com.kairos.persistence.model.staff.TimeCareEmploymentDTO;
import com.kairos.persistence.model.staff.employment.Employment;
import com.kairos.persistence.model.staff.employment.EmploymentQueryResult;
import com.kairos.persistence.model.staff.employment.EmploymentReasonCodeQueryResult;
import com.kairos.persistence.model.staff.employment.EmploymentUnitPositionDTO;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.user.unit_position.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.country.FunctionGraphRepository;
import com.kairos.persistence.repository.user.country.ReasonCodeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseEmploymentTypeRelationshipGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.pay_table.PayGradeGraphRepository;
import com.kairos.persistence.repository.user.positionCode.PositionCodeGraphRepository;
import com.kairos.persistence.repository.user.staff.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffExpertiseRelationShipGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitPermissionGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionEmploymentTypeRelationShipGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionFunctionRelationshipRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.rest_client.TimeBankRestClient;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.integration.PlannerSyncService;
import com.kairos.service.kafka.UserToSchedulerQueueService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.position_code.PositionCodeService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.service.staff.StaffService;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;
import com.kairos.dto.user.staff.unit_position.UnitPositionDTO;
import com.kairos.utils.DateUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.wrapper.PositionWrapper;
import com.kairos.dto.activity.cta.CTATableSettingWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.commons.utils.DateUtils.ONLY_DATE;

/**
 * Created by pawanmandhan on 26/7/17.
 */

@Transactional
@Service

public class UnitPositionService {
    private final Logger logger = LoggerFactory.getLogger(UnitPositionService.class);
    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private UnitPositionGraphRepository unitPositionGraphRepository;
    @Inject
    private PositionCodeGraphRepository positionCodeGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private UnitPermissionGraphRepository unitPermissionGraphRepository;
    //@Inject
    //private CollectiveTimeAgreementGraphRepository costTimeAgreementGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private PositionCodeService positionCodeService;
    @Inject
    private ClientGraphRepository clientGraphRepository;
    @Inject
    private TimeBankRestClient timeBankRestClient;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private UnitPositionEmploymentTypeRelationShipGraphRepository unitPositionEmploymentTypeRelationShipGraphRepository;
    @Inject
    private ReasonCodeGraphRepository reasonCodeGraphRepository;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    @Inject
    private PayGradeGraphRepository payGradeGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private StaffExpertiseRelationShipGraphRepository staffExpertiseRelationShipGraphRepository;
    @Inject
    private EmploymentService employmentService;
    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private WorkingTimeAgreementRestClient workingTimeAgreementRestClient;
    @Inject
    private PlannerSyncService plannerSyncService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UnitPositionFunctionRelationshipRepository unitPositionFunctionRelationshipRepository;
    @Inject
    ExpertiseEmploymentTypeRelationshipGraphRepository expertiseEmploymentTypeRelationshipGraphRepository;
    @Inject
    private ActivityIntegrationService activityIntegrationService;
    @Inject
    private UserToSchedulerQueueService userToSchedulerQueueService;
    @Inject
    private GenericRestClient genericRestClient;


    public PositionWrapper createUnitPosition(Long id, String type, UnitPositionDTO unitPositionDTO, Boolean createFromTimeCare, Boolean saveAsDraft) {
        Organization organization = organizationService.getOrganizationDetail(id, type);
        Organization parentOrganization;

        PositionCode positionCode = null;
        if (!organization.isParentOrganization()) {
            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(parentOrganization.getId(), unitPositionDTO.getPositionCodeId());
        } else {
            parentOrganization = organization;
            positionCode = positionCodeGraphRepository.getPositionCodeByUnitIdAndId(organization.getId(), unitPositionDTO.getPositionCodeId());
        }
        Employment employment = employmentGraphRepository.findEmploymentByStaff(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound", unitPositionDTO.getStaffId());
        }
        if (employment.getStartDateMillis() != null) {
            if (unitPositionDTO.getStartLocalDate().isBefore(DateUtil.getDateFromEpoch(employment.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");
            }
        }
        if (!Optional.ofNullable(positionCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.name.notexist", unitPositionDTO.getPositionCodeId());
        }
        if (!saveAsDraft) {
            List<UnitPosition> oldUnitPositions = unitPositionGraphRepository.getStaffUnitPositionsByExpertise(organization.getId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId());
            validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
        }
        UnitPosition unitPosition = new UnitPosition();

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(parentOrganization.getId(), unitPositionDTO.getEmploymentTypeId(), false);
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", unitPositionDTO.getEmploymentTypeId());
        }
        preparePosition(unitPosition, unitPositionDTO, createFromTimeCare);

        unitPosition.setPositionCode(positionCode);
        if (!saveAsDraft) {
            unitPosition.setPublished(true);
        }
        unitPosition.setUnit(organization);
        unitPositionGraphRepository.save(unitPosition);
        CTAWTAWrapper ctawtaWrapper = workingTimeAgreementRestClient.assignWTAToUnitPosition(unitPosition.getId(),unitPositionDTO.getWtaId(),unitPositionDTO.getCtaId());
        if (ctawtaWrapper.getWta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.wta.id");
        }
        if (ctawtaWrapper.getCta().isEmpty()) {
            exceptionService.dataNotFoundByIdException("message.cta.id");
        }
        Employment employment1 = employmentService.updateEmploymentEndDate(organization, unitPositionDTO.getStaffId(), unitPositionDTO.getEndLocalDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupIdOnEmploymentEnd());
        Long reasonCodeId = Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment1.getReasonCode().getId() : null;

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition, employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);

        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, relationShip, parentOrganization.getId(), parentOrganization.getName(), ctawtaWrapper.getWta().get(0));
        unitPositionQueryResult.setCostTimeAgreement(ctawtaWrapper.getCta().get(0));
        PositionWrapper positionWrapper = new PositionWrapper(unitPositionQueryResult, new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd()));
        return positionWrapper;
    }


    public boolean validateUnitPositionWithExpertise(List<UnitPosition> unitPositions, UnitPositionDTO unitPositionDTO) {

        LocalDate unitPositionStartDate = unitPositionDTO.getStartLocalDate();
        LocalDate unitPositionEndDate = unitPositionDTO.getEndLocalDate();
        unitPositions.forEach(unitPosition -> {
            // if null date is set
            if (unitPosition.getEndDateMillis() != null) {
                if (unitPositionStartDate.isBefore(DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis())) && unitPositionStartDate.isAfter(DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()))) {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()));
                }
                if (unitPositionEndDate != null) {
                    Interval previousInterval = new Interval(unitPosition.getStartDateMillis(), unitPosition.getEndDateMillis());
                    Interval interval = new Interval(DateUtil.getDateFromEpoch(unitPositionStartDate), DateUtil.getDateFromEpoch(unitPositionEndDate));
                    logger.info(" Interval of CURRENT UEP " + previousInterval + " Interval of going to create  " + interval);
                    if (previousInterval.overlaps(interval))
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                } else {
                    if (unitPositionStartDate.isBefore(DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, DateUtil.getDateFromEpoch(unitPosition.getEndDateMillis()));
                    }
                }
            } else {
                // unitEmploymentEnd date is null
                if (unitPositionEndDate != null) {
                    if (unitPositionEndDate.isAfter(DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist.withvalue", unitPositionEndDate, DateUtil.getDateFromEpoch(unitPosition.getStartDateMillis()));
                    }
                } else {
                    exceptionService.actionNotPermittedException("message.unitemployment.positioncode.alreadyexist");
                }
            }
        });
        return true;
    }

    private void createUnitPositionObject(UnitPosition oldUnitPosition, UnitPosition unitPosition, UnitPositionDTO unitPositionDTO) {
        BeanUtils.copyProperties(oldUnitPosition, unitPosition);
        unitPosition.setId(null);
        unitPosition.setEditable(true);
        unitPosition.setHistory(false);
        unitPosition.setParentUnitPosition(oldUnitPosition);
        oldUnitPosition.setHistory(true);
        oldUnitPosition.setEditable(false);
        Set<Long> olderFunctionsAddedInUnitPosition = oldUnitPosition.getFunctions() != null ? oldUnitPosition.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()) : Collections.emptySet();
        //TODO Vipul update equals method
        if (!olderFunctionsAddedInUnitPosition.equals(unitPositionDTO.getFunctionIds())) {
            List<Function> functions = new ArrayList<>();
            if (!unitPositionDTO.getFunctionIds().isEmpty()) {
                functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
                if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
                    exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
                }
            }
            unitPosition.setFunctions(functions);
        }
        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getEndLocalDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");
            }
        }
        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.enddate");
            }
            unitPosition.setLastWorkingDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getLastWorkingLocalDate()));
        }
        if (unitPositionDTO.getStartLocalDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");
        }
        unitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));
        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {
            unitPosition.setEndDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()));
            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException("message.region.enddate");
            }
            if (oldUnitPosition.getReasonCode() == null || !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId())) {
                Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
                if (!Optional.ofNullable(reasonCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", unitPositionDTO.getReasonCodeId());
                }
                unitPosition.setReasonCode(reasonCode.get());
            }
        }
        unitPosition.setTotalWeeklyMinutes((unitPositionDTO.getTotalWeeklyHours() * 60));
        unitPosition.setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours());
        unitPosition.setHourlyWages(unitPositionDTO.getHourlyWages());
        unitPosition.setSalary(unitPositionDTO.getSalary());
        unitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));
    }


    private boolean calculativeValueChanged(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO, UnitPositionEmploymentTypeRelationShip oldUnitPositionEmploymentTypeRelationShip) {
        if (oldUnitPosition.getAvgDailyWorkingHours() != unitPositionDTO.getAvgDailyWorkingHours() ||
                (oldUnitPosition.getReasonCode() != null && !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId())) ||
                (oldUnitPosition.getFunctions() != null && !oldUnitPosition.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()).equals(unitPositionDTO.getFunctionIds()))) {
            return true;
        }
        if (!oldUnitPositionEmploymentTypeRelationShip.getEmploymentType().getId().equals(unitPositionDTO.getEmploymentTypeId()) || !oldUnitPositionEmploymentTypeRelationShip.getEmploymentTypeCategory().equals(unitPositionDTO.getEmploymentTypeCategory())) {
            return true;
        }
        return false;
    }

    private void linkUnitPositionWithEmploymentType(UnitPosition unitPosition, UnitPositionDTO unitPositionDTO) {
        EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitPositionDTO.getEmploymentTypeId());
        if (!Optional.ofNullable(employmentType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.position.employmenttype.notexist", unitPositionDTO.getEmploymentTypeId());
        }

        UnitPositionEmploymentTypeRelationShip relationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition, employmentType, unitPositionDTO.getEmploymentTypeCategory());
        unitPositionEmploymentTypeRelationShipGraphRepository.save(relationShip);
    }

    public PositionWrapper updateUnitPosition(long unitPositionId, UnitPositionDTO unitPositionDTO, Long unitId, String type, Boolean saveAsDraft) {

        Organization organization = organizationService.getOrganizationDetail(unitId, type);
        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(unitPositionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        UnitPosition oldUnitPosition = unitPositionGraphRepository.findOne(unitPositionId);
         if (!Optional.ofNullable(oldUnitPosition).isPresent()) {

            exceptionService.dataNotFoundByIdException("message.positionid.notfound", unitPositionId);

        }
        UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip = unitPositionGraphRepository.findEmploymentTypeByUnitPositionId(unitPositionId);
        EmploymentQueryResult employmentQueryResult;
        UnitPositionQueryResult unitPositionQueryResult;
        Boolean calculativeValueChanged = calculativeValueChanged(oldUnitPosition, unitPositionDTO, unitPositionEmploymentTypeRelationShip);
        /**
         *  Old unit position is published and calculative values is changes and both options save and  published is selected
         *  Old unit position is published so need to create a new unit position
         **/
        if (oldUnitPosition.isPublished() && calculativeValueChanged && !saveAsDraft) {
            List<UnitPosition> oldUnitPositions
                    = unitPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitPositionDTO.getUnitId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId(), unitPositionId);

            validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);

            UnitPosition unitPosition = new UnitPosition();
            createUnitPositionObject(oldUnitPosition, unitPosition, unitPositionDTO);
            oldUnitPosition.setEndDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate().minusDays(1L)));
            unitPositionGraphRepository.save(unitPosition);
            linkUnitPositionWithEmploymentType(unitPosition, unitPositionDTO);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, unitPositionEmploymentTypeRelationShip, organization.getId(), organization.getName(), null);
        }
        // calculative value is not changed but still user still wants to save as draft.
        else if (oldUnitPosition.isPublished() && !calculativeValueChanged && saveAsDraft) {
            UnitPosition unitPosition = new UnitPosition();
            createUnitPositionObject(oldUnitPosition, unitPosition, unitPositionDTO);
            unitPositionGraphRepository.save(unitPosition);
            linkUnitPositionWithEmploymentType(unitPosition, unitPositionDTO);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, unitPositionEmploymentTypeRelationShip, organization.getId(), organization.getName(), null);
        }
        // calculative value is not changed but still user is publishing it it means olny end date is updated.
        else if (oldUnitPosition.isPublished() && !calculativeValueChanged && !saveAsDraft) {
            oldUnitPosition.setEndDateMillis(unitPositionDTO.getEndLocalDate() != null ? DateUtils.getLongFromLocalDate(unitPositionDTO.getEndLocalDate()) : null);
            oldUnitPosition.setLastWorkingDateMillis(unitPositionDTO.getLastWorkingLocalDate() != null ? DateUtils.getLongFromLocalDate(unitPositionDTO.getLastWorkingLocalDate()) : null);
            unitPositionGraphRepository.save(oldUnitPosition);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, oldUnitPosition, unitPositionEmploymentTypeRelationShip, organization.getId(), organization.getName(), null);
        } //calculative value is changed user is saving this as draft.
        else if (oldUnitPosition.isPublished() && calculativeValueChanged && saveAsDraft) {
            //CREAte new UP
            UnitPosition unitPosition = new UnitPosition();
            createUnitPositionObject(oldUnitPosition, unitPosition, unitPositionDTO);
            unitPositionGraphRepository.save(unitPosition);
            linkUnitPositionWithEmploymentType(unitPosition, unitPositionDTO);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, unitPosition, unitPositionEmploymentTypeRelationShip, organization.getId(), organization.getName(), null);
        } else {
            // update in current copy
            preparePosition(oldUnitPosition, unitPositionDTO, unitPositionEmploymentTypeRelationShip);
            if (!saveAsDraft) {
                List<UnitPosition> oldUnitPositions
                        = unitPositionGraphRepository.getAllUEPByExpertiseExcludingCurrent(unitPositionDTO.getUnitId(), unitPositionDTO.getStaffId(), unitPositionDTO.getExpertiseId(), unitPositionId);

                validateUnitPositionWithExpertise(oldUnitPositions, unitPositionDTO);
                oldUnitPosition.setPublished(true);
            }
            unitPositionGraphRepository.save(oldUnitPosition);
            unitPositionQueryResult = getBasicDetails(unitPositionDTO, oldUnitPosition, unitPositionEmploymentTypeRelationShip, organization.getId(), organization.getName(), null);

        }

        Employment employment = employmentService.updateEmploymentEndDate(oldUnitPosition.getUnit(), unitPositionDTO.getStaffId(),
                unitPositionDTO.getEndLocalDate() != null ? DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()) : null, unitPositionDTO.getReasonCodeId(), unitPositionDTO.getAccessGroupIdOnEmploymentEnd());
        Long reasonCodeId = Optional.ofNullable(employment.getReasonCode()).isPresent() ? employment.getReasonCode().getId() : null;
        employmentQueryResult = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd());
        // Deleting All shifts after employment end date
        if(unitPositionDTO.getEndLocalDate()!=null){
            activityIntegrationService.deleteShiftsAfterEmploymentEndDate(unitId,unitPositionDTO.getEndLocalDate(),unitPositionDTO.getStaffId());
        }
        //TODO might remove -- FOR FE compactibility
        WTAResponseDTO wtaResponseDTO = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_WTA_BY_UNITPOSITION, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTAResponseDTO>>() {},unitPositionId);
        unitPositionQueryResult.setWorkingTimeAgreement(wtaResponseDTO);
        //plannerSyncService.publishUnitPosition(unitId, oldUnitPosition, unitPositionEmploymentTypeRelationShip.getEmploymentType(), IntegrationOperation.UPDATE);
        return new PositionWrapper(unitPositionQueryResult, employmentQueryResult);

    }

    public EmploymentQueryResult removePosition(long positionId, Long unitId) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.id.notexist", positionId);

        }
        unitPosition.setDeleted(true);
        unitPositionGraphRepository.save(unitPosition);

        Organization unit = organizationGraphRepository.findOne(unitId, 0);
        Long staffId = unitPositionGraphRepository.getStaffIdFromUnitPosition(positionId);
        Employment employment = employmentService.updateEmploymentEndDate(unit, staffId);
        //plannerSyncService.publishUnitPosition(unitId, unitPosition, null, IntegrationOperation.DELETE);
        return new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis());
    }



    public UnitPositionQueryResult getUnitPosition(Long unitPositionId){
       UnitPosition unitPosition =  unitPositionGraphRepository.findOne(unitPositionId,0);
        UnitPositionQueryResult unitPositionQueryResult = new UnitPositionQueryResult();
        unitPositionQueryResult.setPublished(unitPosition.isPublished());
        unitPositionQueryResult.setId(unitPosition.getId());
       return unitPositionQueryResult;
    }
    private UnitPosition preparePosition(UnitPosition unitPosition, UnitPositionDTO unitPositionDTO, Boolean createFromTimeCare) {
        if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent()) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());
            }
            unitPosition.setUnion(union);
        }
        Optional<Organization> unitPositionInOrganization = organizationGraphRepository.findById(unitPositionDTO.getUnitId(), 0);
        if (!unitPositionInOrganization.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.organization.notfound");

        }
        unitPosition.setUnit(unitPositionInOrganization.get());
        Optional<Expertise> expertise = expertiseGraphRepository.findById(unitPositionDTO.getExpertiseId(), 1);
        if (!expertise.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.expertise.id.notFound", unitPositionDTO.getExpertiseId());

        }
        unitPosition.setExpertise(expertise.get());
        unitPosition.setWorkingDaysInWeek(expertise.get().getNumberOfWorkingDaysInWeek());


        Staff staff = staffGraphRepository.findOne(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", unitPositionDTO.getStaffId());

        }
        unitPosition.setStaff(staff);

        // UEP can be created for past dates from time care
        if (!createFromTimeCare && unitPositionDTO.getStartLocalDate().isBefore(LocalDate.now())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");

        }
        unitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));
        unitPosition.setWorkingDaysInWeek(expertise.get().getNumberOfWorkingDaysInWeek());

        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getEndLocalDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");

            }
            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException("message.region.enddate");

            }

            Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
            if (!Optional.ofNullable(reasonCode).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", unitPositionDTO.getReasonCodeId());

            }
            unitPosition.setReasonCode(reasonCode.get());
            unitPosition.setEndDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()));
        }

        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");

            }
            unitPosition.setLastWorkingDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getLastWorkingLocalDate()));
        }

        SeniorityLevel seniorityLevel = getSeniorityLevelByStaffAndExpertise(staff.getId(), expertise.get());
        if (!Optional.ofNullable(seniorityLevel).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.seniorityLevel.id.notfound", unitPositionDTO.getReasonCodeId());

        }
        unitPosition.setSeniorityLevel(seniorityLevel);
        List<Function> functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
        if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
            exceptionService.actionNotPermittedException("message.unitposition.functions.unable");

        }
        unitPosition.setFunctions(functions);
        unitPosition.setFullTimeWeeklyMinutes(expertise.get().getFullTimeWeeklyMinutes());
        unitPosition.setTotalWeeklyMinutes(unitPositionDTO.getTotalWeeklyMinutes() + (unitPositionDTO.getTotalWeeklyHours() * 60));
        unitPosition.setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours());
        unitPosition.setHourlyWages(unitPositionDTO.getHourlyWages());
        unitPosition.setSalary(unitPositionDTO.getSalary());
        unitPosition.setEditable(true);
        unitPosition.setHistory(false);

        //unitPosition.setWorkingDaysInWeek(UnitPositionDTO.getWorkingDaysInWeek());
        if (createFromTimeCare) {
            unitPosition.setTimeCareExternalId(unitPositionDTO.getTimeCareExternalId());
        }

        return unitPosition;
    }

    private void prepareUnion(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO) {

        // If already selected but now no value so we are removing
        if (!Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            oldUnitPosition.setUnion(null);

        }

// If already not present now its present    Previous its absent
        else if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && !Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
            if (!Optional.ofNullable(union).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());

            }
            oldUnitPosition.setUnion(union);
        }

// If already present and still present but a different

        else if (Optional.ofNullable(unitPositionDTO.getUnionId()).isPresent() && Optional.ofNullable(oldUnitPosition.getUnion()).isPresent()) {
            if (!unitPositionDTO.getUnionId().equals(oldUnitPosition.getUnion().getId())) {
                Organization union = organizationGraphRepository.findByIdAndUnionTrueAndIsEnableTrue(unitPositionDTO.getUnionId());
                if (!Optional.ofNullable(union).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.unitposition.union.notexist", unitPositionDTO.getUnionId());

                }
                oldUnitPosition.setUnion(union);
            }
        }

    }

    private void preparePosition(UnitPosition oldUnitPosition, UnitPositionDTO unitPositionDTO, UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip) {

        prepareUnion(oldUnitPosition, unitPositionDTO);
        if (unitPositionEmploymentTypeRelationShip != null) {
            // user has changed need to remove previous and add new
            if (unitPositionEmploymentTypeRelationShip.getEmploymentType().getId() != unitPositionDTO.getEmploymentTypeId()) {
                EmploymentType employmentType = employmentTypeGraphRepository.findOne(unitPositionDTO.getEmploymentTypeId());
                if (!Optional.ofNullable(employmentType).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.employmentType.notnull", unitPositionDTO.getEmploymentTypeId());
                }
                unitPositionEmploymentTypeRelationShipGraphRepository.delete(unitPositionEmploymentTypeRelationShip);
                unitPositionEmploymentTypeRelationShip = new UnitPositionEmploymentTypeRelationShip(oldUnitPosition, employmentType, unitPositionDTO.getEmploymentTypeCategory());
            }
            // user has changed the type
            else if (!unitPositionDTO.getEmploymentTypeCategory().equals(unitPositionEmploymentTypeRelationShip.getEmploymentTypeCategory())) {
                unitPositionEmploymentTypeRelationShip.setEmploymentTypeCategory(unitPositionDTO.getEmploymentTypeCategory());
            }
            unitPositionEmploymentTypeRelationShipGraphRepository.save(unitPositionEmploymentTypeRelationShip);

        }
        if (!oldUnitPosition.getExpertise().getId().equals(unitPositionDTO.getExpertiseId())) {
            exceptionService.actionNotPermittedException("message.unitposition.expertise.notchanged", unitPositionDTO.getExpertiseId());
        }
        if (!oldUnitPosition.getPositionCode().getId().equals(unitPositionDTO.getPositionCodeId())) {
            exceptionService.actionNotPermittedException("message.unitposition.positioncode.notchanged", unitPositionDTO.getPositionCodeId());
        }

        Set<Long> olderFunctionsAddedInUnitPosition = oldUnitPosition.getFunctions() != null ? oldUnitPosition.getFunctions().stream().map(Function::getId).collect(Collectors.toSet()) : Collections.emptySet();
        if (!olderFunctionsAddedInUnitPosition.equals(unitPositionDTO.getFunctionIds())) {
            List<Function> functions = new ArrayList<>();
            if (!unitPositionDTO.getFunctionIds().isEmpty()) {
                functions = functionGraphRepository.findAllFunctionsById(unitPositionDTO.getFunctionIds());
                if (functions.size() != unitPositionDTO.getFunctionIds().size()) {
                    exceptionService.actionNotPermittedException("message.unitposition.functions.unable");
                }
            }
            oldUnitPosition.setFunctions(functions);
        }
        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getEndLocalDate())) {
                exceptionService.actionNotPermittedException("message.startdate.notlessthan.enddate");
            }
        }
        if (Optional.ofNullable(unitPositionDTO.getLastWorkingLocalDate()).isPresent()) {
            if (unitPositionDTO.getStartLocalDate().isAfter(unitPositionDTO.getLastWorkingLocalDate())) {
                exceptionService.actionNotPermittedException("message.lastdate.notlessthan.enddate");

            }
            oldUnitPosition.setLastWorkingDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getLastWorkingLocalDate()));
        }
        Employment employment = employmentGraphRepository.findEmploymentByStaff(unitPositionDTO.getStaffId());
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.employment.notFound", unitPositionDTO.getStaffId());

        }
        if (employment.getStartDateMillis() != null) {
            if (unitPositionDTO.getStartLocalDate().isBefore(DateUtil.getDateFromEpoch(employment.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException("message.staff.data.employmentdate.lessthan");

            }
        }

        oldUnitPosition.setStartDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getStartLocalDate()));

        if (Optional.ofNullable(unitPositionDTO.getEndLocalDate()).isPresent()) {

            oldUnitPosition.setEndDateMillis(DateUtil.getDateFromEpoch(unitPositionDTO.getEndLocalDate()));

            if (!Optional.ofNullable(unitPositionDTO.getReasonCodeId()).isPresent()) {
                exceptionService.actionNotPermittedException("message.region.enddate");

            }

            if (oldUnitPosition.getReasonCode() == null || !oldUnitPosition.getReasonCode().getId().equals(unitPositionDTO.getReasonCodeId())) {
                Optional<ReasonCode> reasonCode = reasonCodeGraphRepository.findById(unitPositionDTO.getReasonCodeId(), 0);
                if (!Optional.ofNullable(reasonCode).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", unitPositionDTO.getReasonCodeId());

                }
                oldUnitPosition.setReasonCode(reasonCode.get());
            }
        }
        //oldUnitPosition.setWorkingDaysInWeek(UnitPositionDTO.getWorkingDaysInWeek());
        oldUnitPosition.setTotalWeeklyMinutes((unitPositionDTO.getTotalWeeklyHours() * 60));
        oldUnitPosition.setAvgDailyWorkingHours(unitPositionDTO.getAvgDailyWorkingHours());
        oldUnitPosition.setHourlyWages(unitPositionDTO.getHourlyWages());
        oldUnitPosition.setSalary(unitPositionDTO.getSalary());


    }

    /*
     * @author vipul
     * used to get all positions of organization n by organization and staff Id
     * */
    public EmploymentUnitPositionDTO getUnitPositionsOfStaff(long unitId, long staffId, boolean allOrganization) {
        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitposition.staff.notfound", staffId);

        }

        User user = userGraphRepository.getUserByStaffId(staffId);

        EmploymentReasonCodeQueryResult employmentReasonCode = employmentGraphRepository.findEmploymentreasonCodeByStaff(staffId);
        Employment employment = employmentReasonCode.getEmployment();

        Long reasonCodeId = Optional.ofNullable(employmentReasonCode.getReasonCode()).isPresent() ? employmentReasonCode.getReasonCode().getId() : null;
        EmploymentQueryResult employmentQueryResult = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), reasonCodeId, employment.getAccessGroupIdOnEmploymentEnd(), employment.getMainEmploymentStartDate(), employment.getMainEmploymentEndDate(), employment.isMainEmployment());
        List<UnitPositionQueryResult> unitPositionQueryResults = (allOrganization) ? unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()) : unitPositionGraphRepository.getAllUnitPositionsForCurrentOrganization(staffId);
        List<Long> unitPositionIds = unitPositionQueryResults.stream().map(u -> u.getId()).collect(Collectors.toList());
        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("upIds",unitPositionIds.toString().replace("[", "").replace("]", "")));
        CTAWTAWrapper ctawtaWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_CTA_WTA_BY_UPIDS, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTAWTAWrapper>>() {});
        Map<Long, WTAResponseDTO> wtaResponseDTOMap = ctawtaWrapper.getWta().stream().collect(Collectors.toMap(w -> w.getUnitPositionId(), w -> w));
        Map<Long, CTAResponseDTO> ctaResponseDTOMap = ctawtaWrapper.getCta().stream().collect(Collectors.toMap(c -> c.getUnitPositionId(), c -> c));
        unitPositionQueryResults.forEach(u -> {
            u.setWorkingTimeAgreement(wtaResponseDTOMap.get(u.getId()));
            u.setCostTimeAgreement(ctaResponseDTOMap.get(u.getId()));
        });
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentQueryResult, unitPositionQueryResults);
        return employmentUnitPositionDTO;

        // TODO  Organization organization = organizationService.getOrganizationDetail(id, type);
//        Organization parentOrganization;
//        UnitPermission unitPermission;
//        if (!organization.isParentOrganization()) {
//            parentOrganization = organizationService.getParentOfOrganization(organization.getId());
//            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(parentOrganization.getId(), organization.getId(), staffId);
//        } else {
//            unitPermission = unitPermissionGraphRepository.checkUnitPermissionOfStaff(organization.getId(), staffId);
//        }
//        if (!Optional.ofNullable(unitPermission).isPresent()) {
//            logger.info("Unable to get Unit employment of this staff ,{} in organization,{}", staffId, organization.getId());
//            throw new DataNotFoundByIdException("unable to get unit employment  of staff");
//        }
        //TODO  return unitPositionGraphRepository.getAllUnitPositionsByStaff(organization.getId(), staffId);
    }

    public PositionCtaWtaQueryResult getCtaAndWtaWithExpertiseDetailByExpertiseId(Long unitId, Long expertiseId, Long staffId) {
        PositionCtaWtaQueryResult positionCtaWtaQueryResult = genericRestClient.publishRequest(null,unitId,true,IntegrationOperation.GET,GET_CTA_WTA_BY_EXPERTISE,null,new ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCtaWtaQueryResult>>() {},expertiseId);
        Optional<Expertise> currentExpertise = expertiseGraphRepository.findById(expertiseId);
        SeniorityLevel appliedSeniorityLevel = getSeniorityLevelByStaffAndExpertise(staffId, currentExpertise.get());
        positionCtaWtaQueryResult.setExpertise(currentExpertise.get().retrieveBasicDetails());
        //SeniorityLevelQueryResult seniorityLevel = (appliedSeniorityLevel != null) ? seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId()) : null;
        //positionCtaWtaQueryResult.setApplicableSeniorityLevel(seniorityLevel);
        positionCtaWtaQueryResult.setUnion(currentExpertise.get().getUnion());

        SeniorityLevelQueryResult seniorityLevel = null;
        if (appliedSeniorityLevel != null) {
            seniorityLevel = seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId());

            List<FunctionDTO> functionDTOs = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevel(currentExpertise.get().getId(), appliedSeniorityLevel.getId());
            seniorityLevel.setFunctions(functionDTOs);
        }
        positionCtaWtaQueryResult.setApplicableSeniorityLevel(seniorityLevel);


        return positionCtaWtaQueryResult;
    }

    //TODO this must be moved to activity
    public UnitPositionQueryResult updateUnitPositionWTA(Long unitId, Long unitPositionId, BigInteger wtaId, WTADTO updateDTO) {
        UnitPosition unitPosition = unitPositionGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(unitPosition).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.InvalidEmploymentPostionId", unitPositionId);

        }
        updateDTO.setId(wtaId);
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.updateWTAOfUnitPosition(updateDTO, unitPosition.isPublished());
        unitPositionGraphRepository.save(unitPosition);
        UnitPositionQueryResult unitPositionQueryResult = getBasicDetails(unitPosition, wtaResponseDTO);
        return unitPositionQueryResult;
    }

    private UnitPositionQueryResult getBasicDetails(UnitPositionDTO unitPositionDTO, UnitPosition unitPosition, UnitPositionEmploymentTypeRelationShip relationShip,
                                                    Long parentOrganizationId, String parentOrganizationName, WTAResponseDTO wtaResponseDTO) {


        logger.info(unitPosition.toString());
        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(),
                unitPosition.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(),
                unitPosition.getTotalWeeklyMinutes(),
                unitPosition.getAvgDailyWorkingHours(),
                unitPosition.getHourlyWages(),
                unitPosition.getId(),
                unitPosition.getSalary(),
                unitPosition.getPositionCode(),
                unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(),
                null, wtaResponseDTO);
        result.setUnitId(unitPosition.getUnit().getId());
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setParentUnitId(parentOrganizationId);
        result.setEditable(unitPosition.isEditable());
        result.setHistory(unitPosition.isHistory());
        result.setPublished(unitPosition.isPublished());


        // TODO Setting for compatibility
        Map<String, Object> unitInfo = new HashMap<>();
        unitInfo.put("id", parentOrganizationId);
        unitInfo.put("name", parentOrganizationName);
        result.setUnitInfo(unitInfo);
        Map<String, Object> employmentTypes = new HashMap();
        employmentTypes.put("name", relationShip.getEmploymentType().getName());
        employmentTypes.put("id", relationShip.getEmploymentType().getId());
        employmentTypes.put("employmentTypeCategory", relationShip.getEmploymentTypeCategory());
        result.setEmploymentType(employmentTypes);


        Map<String, Object> seniorityLevel;
        ObjectMapper objectMapper = new ObjectMapper();
        seniorityLevel = objectMapper.convertValue(unitPosition.getSeniorityLevel(), Map.class);
        seniorityLevel.put("functions", unitPositionDTO.getFunctionIds());
        seniorityLevel.put("payGrade", unitPosition.getSeniorityLevel().getPayGrade());
        result.setSeniorityLevel(seniorityLevel);
        return result;
    }

    public UnitPositionQueryResult getBasicDetails(UnitPosition unitPosition, WTAResponseDTO wtaResponseDTO) {
        UnitPositionQueryResult unitPositionQueryResult = unitPositionGraphRepository.getUnitIdAndParentUnitIdByUnitPositionId(unitPosition.getId());
        UnitPositionQueryResult result = new UnitPositionQueryResult(unitPosition.getExpertise().retrieveBasicDetails(), unitPosition.getStartDateMillis(), unitPosition.getWorkingDaysInWeek(),
                unitPosition.getEndDateMillis(), unitPosition.getTotalWeeklyMinutes(), unitPosition.getAvgDailyWorkingHours(), unitPosition.getHourlyWages(),
                unitPosition.getId(), unitPosition.getSalary(), unitPosition.getPositionCode(), unitPosition.getUnion(),
                unitPosition.getLastWorkingDateMillis(), null, null/*unitPosition.getWorkingTimeAgreement()*/);
        result.setReasonCodeId(unitPosition.getReasonCode() != null ? unitPosition.getReasonCode().getId() : null);
        result.setUnitId(unitPositionQueryResult.getUnitId());
        result.setEditable(unitPosition.isEditable());
        result.setHistory(unitPosition.isHistory());
        result.setPublished(unitPosition.isPublished());
        result.setWorkingTimeAgreement(wtaResponseDTO);

        result.setParentUnitId(unitPositionQueryResult.getParentUnitId());


        return result;
    }


    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionCTA(Long unitPositionId, Long unitId) {

        StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        Long countryId = organizationService.getCountryIdOfOrganization(unitId);
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionWithCtaDetailsDTO = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        unitPositionWithCtaDetailsDTO.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
//        unitPositionWithCtaDetailsDTO.setStaffId(unitPosition.getStaff().getId());
        unitPositionWithCtaDetailsDTO.setId(unitPosition.getId());
        unitPositionWithCtaDetailsDTO.setCountryId(countryId);
        unitPositionWithCtaDetailsDTO.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionWithCtaDetailsDTO.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionWithCtaDetailsDTO.setStartDate(new Date(unitPosition.getStartDateMillis()));
        unitPositionWithCtaDetailsDTO.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionWithCtaDetailsDTO.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionWithCtaDetailsDTO.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionWithCtaDetailsDTO.setEndDate(new Date(unitPosition.getEndDateMillis()));
        }
        Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
        unitPositionWithCtaDetailsDTO.setUnitTimeZone(organization.get().getTimeZone());
        com.kairos.dto.activity.shift.EmploymentType employmentType = new com.kairos.dto.activity.shift.EmploymentType();
        ObjectMapperUtils.copyProperties(unitPosition.getEmploymentType(), employmentType);
        unitPositionWithCtaDetailsDTO.setEmploymentType(employmentType);
        return unitPositionWithCtaDetailsDTO;
    }


    public void convertUnitPositionObject(UnitPositionQueryResult unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails){

        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));

        unitPositionDetails.setId(unitPosition.getId());

        unitPositionDetails.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionDetails.setStartDateMillis(unitPosition.getStartDateMillis());
        unitPositionDetails.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionDetails.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        unitPositionDetails.setCostTimeAgreementId(unitPosition.getCostTimeAgreementId());
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionDetails.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionDetails.setEndDateMillis(unitPosition.getEndDateMillis());
        }
    }
    private void convertUnitPositionObject(StaffUnitPositionDetails unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails){

        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));

        unitPositionDetails.setId(unitPosition.getId());

        unitPositionDetails.setTotalWeeklyMinutes(unitPosition.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(unitPosition.getWorkingDaysInWeek());
        unitPositionDetails.setStartDateMillis(unitPosition.getStartDateMillis());
        unitPositionDetails.setWorkingTimeAgreementId(unitPosition.getWorkingTimeAgreementId());
        unitPositionDetails.setUnitPositionStartDate(DateUtils.asLocalDate(new Date(unitPosition.getStartDateMillis())));
        unitPositionDetails.setCostTimeAgreementId(unitPosition.getCostTimeAgreementId());
        if (unitPosition.getEndDateMillis() != null) {
            unitPositionDetails.setUnitPositionEndDate(DateUtils.asLocalDate(new Date(unitPosition.getEndDateMillis())));
            unitPositionDetails.setEndDateMillis(unitPosition.getEndDateMillis());
        }
    }

    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionDetails(Long unitPositionId, Organization organization, Long countryId) {

        StaffUnitPositionDetails unitPosition = unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        convertUnitPositionObject(unitPosition,unitPositionDetails);
        unitPositionDetails.setCountryId(countryId);
        ExpertisePlannedTimeQueryResult expertisePlannedTimeQueryResult = expertiseEmploymentTypeRelationshipGraphRepository.findPlannedTimeByExpertise(unitPositionDetails.getExpertise().getId(), unitPositionDetails.getEmploymentType().getId());
        if (Optional.ofNullable(expertisePlannedTimeQueryResult).isPresent()) {
            unitPositionDetails.setExcludedPlannedTime(expertisePlannedTimeQueryResult.getExcludedPlannedTime());
            unitPositionDetails.setIncludedPlannedTime(expertisePlannedTimeQueryResult.getIncludedPlannedTime());

        }
        unitPositionDetails.setUnitTimeZone(organization.getTimeZone());
        return unitPositionDetails;
    }


    public UnitPositionDTO convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(TimeCareEmploymentDTO timeCareEmploymentDTO, Long expertiseId, Long staffId, Long employmentTypeId, Long positionCodeId, BigInteger wtaId, BigInteger ctaId, Long unitId) {
        LocalDate startDate=DateUtils.getLocalDateFromString(timeCareEmploymentDTO.getStartDate());
        LocalDate endDate=null;
        if (!timeCareEmploymentDTO.getEndDate().equals("0001-01-01T00:00:00")) {
            endDate = DateUtils.getLocalDateFromString(timeCareEmploymentDTO.getEndDate());
        }
        UnitPositionDTO unitPositionDTO = new UnitPositionDTO(positionCodeId, expertiseId, startDate, endDate, Integer.parseInt(timeCareEmploymentDTO.getWeeklyHours()), employmentTypeId, staffId, wtaId, ctaId, unitId, new Long(timeCareEmploymentDTO.getId()));
        return unitPositionDTO;
    }

    public boolean addEmploymentToUnitByExternalId(List<TimeCareEmploymentDTO> timeCareEmploymentDTOs, String unitExternalId, Long expertiseId) {
        Organization organization = organizationGraphRepository.findByExternalId(unitExternalId);
        if (organization == null) {
            exceptionService.dataNotFoundByIdException("message.unitposition.organization.externalid", unitExternalId);

        }
        Organization parentOrganization = organizationService.fetchParentOrganization(organization.getId());

        Long countryId = organizationService.getCountryIdOfOrganization(parentOrganization.getId());
        EmploymentType employmentType = employmentTypeGraphRepository.getOneEmploymentTypeByCountryId(countryId, false);

        Expertise expertise = null;
        if (expertiseId == null) {
            expertise = expertiseGraphRepository.getOneDefaultExpertiseByCountry(countryId);
        } else {
            expertise = expertiseGraphRepository.getExpertiesOfCountry(countryId, expertiseId);
        }

        if (expertise == null) {
            exceptionService.dataNotFoundByIdException("message.unitposition.expertise.notfound", expertiseId);

        }




        CTAWTAWrapper ctawtaWrapper = workingTimeAgreementRestClient.getWTAByExpertise(expertise.getId());
        if (CollectionUtils.isNotEmpty(ctawtaWrapper.getCta()) || CollectionUtils.isNotEmpty(ctawtaWrapper.getWta())) {
            exceptionService.dataNotFoundByIdException("message.organization.cta.notfound", organization.getId());

        }
        if (CollectionUtils.isNotEmpty(ctawtaWrapper.getWta())) {
            exceptionService.dataNotFoundByIdException("message.wta.notFound", organization.getId());

        }
        PositionCode positionCode = positionCodeGraphRepository.getOneDefaultPositionCodeByUnitId(parentOrganization.getId());
        if (positionCode == null) {
            exceptionService.dataNotFoundByIdException("message.positioncode.organization.notexist", parentOrganization.getId());

        }

        for (TimeCareEmploymentDTO timeCareEmploymentDTO : timeCareEmploymentDTOs) {
            Staff staff = staffGraphRepository.findByExternalId(timeCareEmploymentDTO.getPersonID());
            if (staff == null) {
                exceptionService.dataNotFoundByIdException("message.staff.externalid.notexist", timeCareEmploymentDTO.getPersonID());

            }
            UnitPositionDTO unitEmploymentPosition = convertTimeCareEmploymentDTOIntoUnitEmploymentDTO(timeCareEmploymentDTO, expertise.getId(), staff.getId(), employmentType.getId(), positionCode.getId(), ctawtaWrapper.getWta().get(0).getId(), ctawtaWrapper.getCta().get(0).getId(), organization.getId());
            createUnitPosition(organization.getId(), "Organization", unitEmploymentPosition, true, true);
        }
        return true;
    }

    public boolean importAllEmploymentsFromTimeCare(List<TimeCareEmploymentDTO> timeCareEmploymentsDTOs, Long expertiseId) {

        // To prepare list of organization's external Id
        Set<String> listOfWorkPlaceIds = new HashSet<>();
        for (TimeCareEmploymentDTO timeCareStaffDTO : timeCareEmploymentsDTOs) {
            listOfWorkPlaceIds.add(timeCareStaffDTO.getWorkPlaceID());
        }

        for (String workPlaceId : listOfWorkPlaceIds) {
            List<TimeCareEmploymentDTO> timeCareEmploymentsByWorkPlace = timeCareEmploymentsDTOs.stream().filter(timeCareEmploymentDTO -> timeCareEmploymentDTO.getWorkPlaceID().equals(workPlaceId)).
                    collect(Collectors.toList());
            addEmploymentToUnitByExternalId(timeCareEmploymentsByWorkPlace, workPlaceId, expertiseId);
        }

        return true;
    }



    public SeniorityLevel getSeniorityLevelByStaffAndExpertise(Long staffId, Expertise currentExpertise) {

        StaffExperienceInExpertiseDTO staffSelectedExpertise = staffExpertiseRelationShipGraphRepository.getExpertiseWithExperienceByStaffIdAndExpertiseId(staffId, currentExpertise.getId());

        if (!Optional.ofNullable(staffSelectedExpertise).isPresent() || !Optional.ofNullable(currentExpertise).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.staff.expertise.notassigned");

            }

        Integer experienceInMonth = (int) ChronoUnit.MONTHS.between(DateUtil.asLocalDate(staffSelectedExpertise.getExpertiseStartDate()), LocalDate.now());
        logger.info("user has current experience in months :{}", experienceInMonth);
        SeniorityLevel appliedSeniorityLevel = null;
        for (SeniorityLevel seniorityLevel : currentExpertise.getSeniorityLevel()) {
            if (seniorityLevel.getTo() == null) {
                // more than  is set if
                if (experienceInMonth >= seniorityLevel.getFrom() * 12) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            } else {
                // to and from is present
                logger.info("user has current experience in months :{} ,{},{},{}", seniorityLevel.getFrom(), experienceInMonth, seniorityLevel.getTo(), experienceInMonth);

                if (seniorityLevel.getFrom() * 12 <= experienceInMonth && seniorityLevel.getTo() * 12 > experienceInMonth) {
                    appliedSeniorityLevel = seniorityLevel;
                    break;
                }
            }
        }

        return appliedSeniorityLevel;
    }

    public EmploymentUnitPositionDTO updateUnitPositionEndDateFromEmployment(Long staffId, String employmentEndDate, Long unitId, Long reasonCodeId, Long accessGroupId) {

        Organization unit = organizationGraphRepository.findOne(unitId);
        Long endDateMillis = DateUtil.getIsoDateInLong(employmentEndDate);
        Long unitPositionStartDateMillisMax = unitPositionGraphRepository.getMaxUnitPositionStartDate(staffId);
        if (Optional.ofNullable(unitPositionStartDateMillisMax).isPresent() && endDateMillis < unitPositionStartDateMillisMax) {
            exceptionService.actionNotPermittedException("message.employmentdata.greaterthan.unitpositiondate", unitPositionStartDateMillisMax);

        }
        List<UnitPosition> unitPositions = unitPositionGraphRepository.getUnitPositionsFromEmploymentEndDate(staffId, endDateMillis);
        ReasonCode reasonCode = reasonCodeGraphRepository.findById(reasonCodeId, 0).get();
        if (!Optional.of(reasonCode).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.reasonCode.id.notFound", reasonCodeId);

        }

        for (UnitPosition unitPosition : unitPositions) {
            unitPosition.setEndDateMillis(endDateMillis);
            if (!Optional.ofNullable(unitPosition.getReasonCode()).isPresent()) {
                unitPosition.setReasonCode(reasonCode);
            }
        }

        Employment employment = employmentGraphRepository.findEmploymentByStaff(staffId);
        if (employment.getMainEmploymentEndDate() != null) {
            Long mainEmploymentEndDate = DateUtil.getDateFromEpoch(employment.getMainEmploymentEndDate());
            if (endDateMillis > mainEmploymentEndDate) {
                exceptionService.invalidRequestException("message.employmentdata.lessthan.mainEmploymentEndDate");
            }
        }

        userToSchedulerQueueService.pushToJobQueueOnEmploymentEnd(endDateMillis, employment.getEndDateMillis(), unit.getId(), employment.getId(),
                unit.getTimeZone());

        employment.setEndDateMillis(endDateMillis);
        employmentGraphRepository.deleteEmploymentReasonCodeRelation(staffId);

        employment.setReasonCode(reasonCode);
        employment.setAccessGroupIdOnEmploymentEnd(accessGroupId);
        unitPositionGraphRepository.saveAll(unitPositions);
        employmentGraphRepository.save(employment);
        User user = userGraphRepository.getUserByStaffId(staffId);
        EmploymentQueryResult employmentUpdated = new EmploymentQueryResult(employment.getId(), employment.getStartDateMillis(), employment.getEndDateMillis(), employment.getReasonCode().getId(), employment.getAccessGroupIdOnEmploymentEnd());
        EmploymentUnitPositionDTO employmentUnitPositionDTO = new EmploymentUnitPositionDTO(employmentUpdated, unitPositionGraphRepository.getAllUnitPositionsByUser(user.getId()));
        return employmentUnitPositionDTO;

    }

    public Long getUnitPositionIdByStaffAndExpertise(Long unitId, Long staffId, Long dateInMillis, Long expertiseId) {
        return unitPositionGraphRepository.getUnitPositionIdByStaffAndExpertise(unitId, staffId, expertiseId, dateInMillis);
    }

    public Map<Long, Long> getUnitPositionExpertiseMap(Long unitId) {
        List<Map<Long, Long>> listOfMap = unitPositionGraphRepository.getMapOfUnitPositionAndExpertiseId(unitId);
        Map<Long, Long> mapOfUnitPositionAndExpertise = new HashMap<>(listOfMap.size());
        listOfMap.forEach(mapOfExpertise -> {
            mapOfUnitPositionAndExpertise.putAll(mapOfExpertise);
        });
        return mapOfUnitPositionAndExpertise;
    }

    public Boolean applyFunction(Long unitPositionId, Map<String, Object> payload) throws ParseException {

        String dateAsString = new ArrayList<>(payload.keySet()).get(0);

        Map<String, Object> functionMap = (Map<String, Object>) payload.get(dateAsString);
        Long functionId = new Long((Integer) functionMap.get("id"));

        Date date = DateUtils.convertToOnlyDate(dateAsString, ONLY_DATE);
        Boolean unitPositionFunctionRelationship = unitPositionFunctionRelationshipRepository.getUnitPositionFunctionRelationshipByUnitPositionAndFunction(unitPositionId, functionId, date.getTime());

        if (unitPositionFunctionRelationship == null) {
            unitPositionFunctionRelationshipRepository.createUnitPositionFunctionRelationship(unitPositionId, functionId, Collections.singletonList(date.getTime()));
        } else if (unitPositionFunctionRelationship) {
            exceptionService.actionNotPermittedException("message.unitposition.function.alreadyApplied", dateAsString);
        }
        return true;
    }

    public Boolean removeFunction(Long unitPositionId, Date appliedDate) {
        unitPositionFunctionRelationshipRepository.removeDateFromUnitPositionFunctionRelationship(unitPositionId, appliedDate.getTime());
        return true;
    }

    public List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> getStaffsUnitPosition(Long unitId, Long expertiseId, List<Long> staffId) {
        List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> staffData =
                staffGraphRepository.getStaffInfoByUnitIdAndStaffId(unitId, expertiseId, staffId);
        return staffData;
    }

    public List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, Long expertiseId, List<Long> staffId) {
        List<com.kairos.persistence.model.user.unit_position.StaffUnitPositionDetails> staffData =
                staffGraphRepository.getStaffIdAndUnitPositionId(unitId, expertiseId, staffId, System.currentTimeMillis());
        return staffData;
    }

    public WTATableSettingWrapper getAllWTAOfStaff(Long unitId,Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> unitpositionIds = unitPositionQueryResults.stream().map(u->u.getId()).collect(Collectors.toList());

        List<NameValuePair> param = Arrays.asList(new BasicNameValuePair("upIds",unitpositionIds.toString().replace("[", "").replace("]", "")));
        WTATableSettingWrapper wtaWithTableSettings = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_WTA, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>>() {});
        Map<Long,UnitPositionQueryResult> unitPositionQueryResultMap = unitPositionQueryResults.stream().filter(u->u.getHistory().equals(false)).collect(Collectors.toMap(k->k.getId(),v->v));
        wtaWithTableSettings.getAgreements().forEach(currentWTA -> {
            UnitPositionQueryResult unitPositionQueryResult = unitPositionQueryResultMap.get(currentWTA.getUnitPositionId());
            if(unitPositionQueryResult!=null){
                currentWTA.setUnitInfo(unitPositionQueryResult.getUnitInfo());
                currentWTA.setUnitPositionId(unitPositionQueryResult.getId());
                currentWTA.setPositionCode(ObjectMapperUtils.copyPropertiesByMapper(unitPositionQueryResult.getPositionCode(), PositionCodeDTO.class));
            }
        });
        return wtaWithTableSettings;
    }

    public CTATableSettingWrapper getAllCTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> upIds = unitPositionQueryResults.stream().map(u -> u.getId()).collect(Collectors.toList());
        List<NameValuePair> requestParam = Arrays.asList(new BasicNameValuePair("upIds", upIds.toString().replace("[", "").replace("]", "")));
        CTATableSettingWrapper ctaTableSettingWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_CTA, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTATableSettingWrapper>>() {});
        ctaTableSettingWrapper.getAgreements().forEach(currentCTA -> {
            UnitPositionQueryResult currentActiveUnitPosition = unitPositionQueryResults.stream().filter(currentUnitPosition -> currentUnitPosition.getId().equals(currentCTA.getUnitPositionId())
                    && currentUnitPosition.getHistory().equals(false)).findFirst().get();
            currentCTA.setUnitInfo(currentActiveUnitPosition.getUnitInfo());
            currentCTA.setUnitPositionId(currentActiveUnitPosition.getId());
            currentCTA.setPositionCode(ObjectMapperUtils.copyPropertiesByMapper(currentActiveUnitPosition.getPositionCode(), PositionCodeDTO.class));
        });
        return ctaTableSettingWrapper;
    }


    public void updateSeniorityLevelOnJobTrigger() {

        List<UnitPositionSeniorityLevelQueryResult> unitPositionSeniorityLevelQueryResults = unitPositionGraphRepository.findUnitPositionSeniorityLeveltoUpdate();
        List<Long> unitPositionIds = unitPositionSeniorityLevelQueryResults.stream().map(unitPositionQueryResult->unitPositionQueryResult.getUnitPosition().getId()).
                collect(Collectors.toList());

        List<UnitPositionCompleteQueryResult> unitPositionsComplete = unitPositionGraphRepository.findUnitPositionCompleteObject(unitPositionIds);
        Map<Long,UnitPosition> unitPositionMap = new HashMap<Long,UnitPosition>();
        for(UnitPositionCompleteQueryResult unitPositionCompleteQueryResult:unitPositionsComplete) {
            UnitPosition unitPositionComplete = unitPositionCompleteQueryResult.getUnitPosition();
            unitPositionComplete.setExpertise(unitPositionCompleteQueryResult.getExpertise());
            unitPositionComplete.setFunctions(unitPositionCompleteQueryResult.getFunctions());
            unitPositionComplete.setReasonCode(unitPositionCompleteQueryResult.getReasonCode());
            unitPositionComplete.setStaff(unitPositionCompleteQueryResult.getStaff());
            unitPositionComplete.setPositionCode(unitPositionCompleteQueryResult.getPositionCode());
            unitPositionComplete.setUnit(unitPositionCompleteQueryResult.getUnit());
            unitPositionComplete.setUnion(unitPositionCompleteQueryResult.getUnionOrg());
            unitPositionMap.put(unitPositionComplete.getId(),unitPositionComplete);
        }

        List<UnitPositionEmploymentTypeRelationShip> unitPositionEmploymentTypeRelationShips = new ArrayList<>();
        List<UnitPosition> unitPositions = new ArrayList<>();
        for(UnitPositionSeniorityLevelQueryResult unitPositionSeniorityLevelQueryResult :unitPositionSeniorityLevelQueryResults) {
            UnitPosition unitPosition = new UnitPosition();
            UnitPositionEmploymentTypeRelationShip unitPositionEmploymentTypeRelationShip;
            UnitPosition oldUnitPosition = unitPositionMap.get(unitPositionSeniorityLevelQueryResult.getUnitPosition().getId());
            ObjectMapperUtils.copyProperties(oldUnitPosition,unitPosition);
            oldUnitPosition.setEndDateMillis(DateUtils.getOneDayBeforeMillis());
            oldUnitPosition.setHistory(true);
            oldUnitPosition.setEditable(false);
            unitPosition.setStartDateMillis(DateUtils.getCurrentDayStartMillis());
            unitPosition.setSeniorityLevel(unitPositionSeniorityLevelQueryResult.getSeniorityLevel());
            unitPosition.setParentUnitPosition(oldUnitPosition);
            unitPositionEmploymentTypeRelationShip = new UnitPositionEmploymentTypeRelationShip(unitPosition,unitPositionSeniorityLevelQueryResult.getEmploymentType(),
                    unitPositionSeniorityLevelQueryResult.getUnitPositionEmploymentTypeRelationShip().getEmploymentTypeCategory() );
            unitPositionEmploymentTypeRelationShips.add( unitPositionEmploymentTypeRelationShip);
            unitPosition.setId(null);
            unitPositions.add(unitPosition);
        }

        unitPositionGraphRepository.saveAll(unitPositions);
        unitPositionEmploymentTypeRelationShipGraphRepository.saveAll(unitPositionEmploymentTypeRelationShips);


    }
}
