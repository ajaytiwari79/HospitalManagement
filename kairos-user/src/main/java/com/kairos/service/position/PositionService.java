package com.kairos.service.position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.wta.WTAWithRuleTemplateDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.WTARuleTemplateQueryResponse;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.*;
import com.kairos.persistence.model.user.client.ClientMinimumDTO;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.position.Position;
import com.kairos.persistence.model.user.position.PositionName;
import com.kairos.persistence.model.user.position.PositionQueryResult;
import com.kairos.persistence.model.user.staff.Staff;
import com.kairos.persistence.model.user.staff.UnitEmployment;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.cta.CollectiveTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.client.ClientGraphRepository;
import com.kairos.persistence.repository.user.country.EmploymentTypeGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.position.PositionGraphRepository;
import com.kairos.persistence.repository.user.position.PositionNameGraphRepository;
import com.kairos.persistence.repository.user.staff.StaffGraphRepository;
import com.kairos.persistence.repository.user.staff.UnitEmploymentGraphRepository;
import com.kairos.response.dto.web.PositionDTO;
import com.kairos.response.dto.web.PositionWrapper;
import com.kairos.service.UserBaseService;
import com.kairos.service.agreement.wta.WTAService;
import com.kairos.service.organization.OrganizationService;
import com.kairos.service.staff.StaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.AppConstants.*;

/**
 * Created by pawanmandhan on 26/7/17.
 */


@Transactional
@Service
public class PositionService extends UserBaseService {

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @Inject
    private StaffGraphRepository staffGraphRepository;
    @Inject
    private PositionGraphRepository positionGraphRepository;
    @Inject
    private PositionNameGraphRepository positionNameGraphRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private UnitEmploymentGraphRepository unitEmploymentGraphRepository;
    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject
    private CollectiveTimeAgreementGraphRepository costTimeAgreementGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private StaffService staffService;
    @Inject
    private EmploymentTypeGraphRepository employmentTypeGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private PositionNameService positionNameService;
    @Inject
    private WTAService wtaService;
    @Inject
    private ClientGraphRepository clientGraphRepository;

    public Position createPosition(Long id, long unitEmploymentId, PositionDTO positionDTO, String type) {
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);
        if (!Optional.ofNullable(unitEmployment).isPresent()) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }

        Organization organization = organizationService.getOrganizationDetail(id, type);
        if (!organization.isParentOrganization()) {
            organization = organizationService.getParentOfOrganization(organization.getId());

        }
        Position position = preparePosition(positionDTO, organization, id);
        List<Position> positions = unitEmployment.getPositions();

        positions.add(position);
        unitEmployment.setPositions(positions);
        save(unitEmployment);
        position.setStaff(null);
        return position;
    }


    public PositionWrapper updatePosition(long positionId, PositionDTO positionDTO) {

        List<ClientMinimumDTO> clientMinimumDTO = clientGraphRepository.getCitizenListForThisContactPerson(positionDTO.getStaffId());
        if (clientMinimumDTO.size() > 0) {
            return new PositionWrapper(clientMinimumDTO);
        }

        Position oldPosition = positionGraphRepository.findOne(positionId);
        if (!Optional.ofNullable(oldPosition).isPresent()) {
            throw new DataNotFoundByIdException("Invalid positionId id " + positionId + " while updating the position");
        }
        preparePosition(oldPosition, positionDTO);
        save(oldPosition);
        return new PositionWrapper(oldPosition);

    }


    public boolean removePosition(long positionId) {

        Position position = positionGraphRepository.findOne(positionId);
        if (position == null) {
            return false;
        }
        position.setEnabled(false);
        save(position);
        if (positionGraphRepository.findOne(positionId).isEnabled()) {
            return false;
        }
        return true;
    }


    public Position getPosition(long positionId) {
        return positionGraphRepository.findOne(positionId);
    }
    /*
    * Created by vipul
    * 4-august-17
    * used to get all positions based on unitEmployment
    * */

    public List<PositionQueryResult> getAllPositions(long unitEmploymentId) {
        UnitEmployment unitEmployment = unitEmploymentGraphRepository.findOne(unitEmploymentId);

        if (unitEmployment == null) {
            throw new DataNotFoundByIdException("Invalid UnitEmployment id" + unitEmploymentId);
        }
        return positionGraphRepository.findAllPositions(unitEmploymentId);

    }

    private Position preparePosition(PositionDTO positionDTO, Organization organization, Long unitId) {
        Position position = new Position();

        //String name, String description, Expertise expertise, CostTimeAgreement cta, WorkingTimeAgreement wta
        WTAWithRuleTemplateDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(positionDTO.getExpertiseId());

        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getName()).isPresent()) {
            logger.info("Expertise Doesn't contains WTA.Please select different Expertise" + positionDTO.getExpertiseId());
            throw new DataNotFoundByIdException("Expertise Doesn't contains WTA.Please select different Expertise");
        }
        WorkingTimeAgreement wta = copyWTASettingAndRuleTemplateWithCategory(wtaWithRuleTemplateDTO);
        save(wta);
        position.setWta(wta);


        if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Expertize" + positionDTO.getExpertiseId());
        }
        position.setExpertise(wtaWithRuleTemplateDTO.getExpertise());

        EmploymentType employmentType = organizationGraphRepository.getEmploymentTypeByOrganizationAndEmploymentId(organization.getId(), positionDTO.getEmploymentTypeId(), false);
        if (employmentType == null) {
            throw new DataNotFoundByIdException("Employment Type does not exist in unit " + employmentType.getId() + " AND " + positionDTO.getEmploymentTypeId());
        }
        position.setEmploymentType(employmentType);

        PositionName positionName = positionNameService.getPositionNameByUnitIdAndId(organization.getId(), positionDTO.getPositionNameId());
        if (!Optional.ofNullable(positionName).isPresent()) {
            throw new DataNotFoundByIdException("position Name does not exist in unit " + positionDTO.getPositionNameId());
        }
        position.setPositionName(positionName);

        /*CostTimeAgreement cta = costTimeAgreementGraphRepository.findOne(positionDTO.getCtaId());
         if (cta == null) {
            throw new DataNotFoundByIdException("Invalid CTA");
        }
        position.setCta(cta);<String, Object>

        */

        Staff staff = staffGraphRepository.findOne(positionDTO.getStaffId());
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + positionDTO.getStaffId());
        }
        position.setStaff(staff);
        position.setStartDate(positionDTO.getStartDate());
        position.setEndDate(positionDTO.getEndDate());

        position.setTotalWeeklyHours(positionDTO.getTotalWeeklyHours());
        position.setAvgDailyWorkingHours(positionDTO.getAvgDailyWorkingHours());
        position.setHourlyWages(positionDTO.getHourlyWages());
        position.setSalary(positionDTO.getSalary());
        position.setWorkingDaysInWeek(positionDTO.getWorkingDaysInWeek());

        return position;
    }

    private WorkingTimeAgreement copyWTASettingAndRuleTemplateWithCategory(WTAWithRuleTemplateDTO wtaWithRuleTemplateDTO) {
        WorkingTimeAgreement wta = new WorkingTimeAgreement();
        wta.setName(wtaWithRuleTemplateDTO.getName());
        wta.setDescription(wtaWithRuleTemplateDTO.getDescription());
        wta.setEndDateMillis(wtaWithRuleTemplateDTO.getEndDateMillis());
        wta.setStartDateMillis(wtaWithRuleTemplateDTO.getStartDateMillis());
        wtaWithRuleTemplateDTO.getRuleTemplates();

        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<>();
        for (int i = 0; i < wtaWithRuleTemplateDTO.getRuleTemplates().size(); i++) {
            ObjectMapper objectMapper = new ObjectMapper();
            WTARuleTemplateQueryResponse response = objectMapper.convertValue(wtaWithRuleTemplateDTO.getRuleTemplates().get(i), WTARuleTemplateQueryResponse.class);
            wtaBaseRuleTemplates.add(copyRuleTemplateAndLinkWithThisWTA(response));
        }
        wta.setRuleTemplates(wtaBaseRuleTemplates);
        return wta;
    }

    private WTABaseRuleTemplate copyRuleTemplateAndLinkWithThisWTA(WTARuleTemplateQueryResponse wtaRuleTemplateQueryResponse) {
        WTABaseRuleTemplate wtaBaseRuleTemplates = null;
        switch (wtaRuleTemplateQueryResponse.getTemplateType()) {
            case TEMPLATE1:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                maximumShiftLengthWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumShiftLengthWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(wtaRuleTemplateQueryResponse.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.getCheckAgainstTimeRules());
                maximumShiftLengthWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumShiftLengthWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(maximumShiftLengthWTATemplate);

                break;

            case TEMPLATE2:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                minimumShiftLengthWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumShiftLengthWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(wtaRuleTemplateQueryResponse.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.getCheckAgainstTimeRules());
                minimumShiftLengthWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumShiftLengthWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(minimumShiftLengthWTATemplate);
                break;

            case TEMPLATE3:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                maximumConsecutiveWorkingDaysWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumConsecutiveWorkingDaysWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.getCheckAgainstTimeRules());
                maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumConsecutiveWorkingDaysWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(maximumConsecutiveWorkingDaysWTATemplate);

                break;

            case TEMPLATE4:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                minimumRestInConsecutiveDaysWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumRestInConsecutiveDaysWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(wtaRuleTemplateQueryResponse.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(wtaRuleTemplateQueryResponse.getDaysWorked());
                minimumRestInConsecutiveDaysWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumRestInConsecutiveDaysWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(minimumRestInConsecutiveDaysWTATemplate);
                break;

            case TEMPLATE5:
                MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                maximumNightShiftLengthWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumNightShiftLengthWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                maximumNightShiftLengthWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumNightShiftLengthWTATemplate.setTimeLimit(wtaRuleTemplateQueryResponse.getTimeLimit());
                maximumNightShiftLengthWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.getCheckAgainstTimeRules());
                maximumNightShiftLengthWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                wtaBaseRuleTemplates = save(maximumNightShiftLengthWTATemplate);
                break;

            case TEMPLATE6:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                minimumConsecutiveNightsWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumConsecutiveNightsWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                minimumConsecutiveNightsWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumConsecutiveNightsWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());

                wtaBaseRuleTemplates = save(minimumConsecutiveNightsWTATemplate);
                break;

            case TEMPLATE7:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                maximumConsecutiveWorkingNights.setName(wtaRuleTemplateQueryResponse.getName());
                maximumConsecutiveWorkingNights.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumConsecutiveWorkingNights.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumConsecutiveWorkingNights.setNightsWorked(wtaRuleTemplateQueryResponse.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(wtaRuleTemplateQueryResponse.getCheckAgainstTimeRules());
                maximumConsecutiveWorkingNights.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(maximumConsecutiveWorkingNights);
                break;
            case TEMPLATE8:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                minimumRestConsecutiveNightsWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumRestConsecutiveNightsWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumRestConsecutiveNightsWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(wtaRuleTemplateQueryResponse.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(wtaRuleTemplateQueryResponse.getMinimumRest());
                minimumRestConsecutiveNightsWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(minimumRestConsecutiveNightsWTATemplate);
                break;
            case TEMPLATE9:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = new MaximumNumberOfNightsWTATemplate();
                maximumNumberOfNightsWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumNumberOfNightsWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumNumberOfNightsWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(wtaRuleTemplateQueryResponse.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumNumberOfNightsWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());

                wtaBaseRuleTemplates = save(maximumNumberOfNightsWTATemplate);
                break;
            case TEMPLATE10:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = new MaximumDaysOffInPeriodWTATemplate();
                maximumDaysOffInPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumDaysOffInPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumDaysOffInPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                maximumDaysOffInPeriodWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(maximumDaysOffInPeriodWTATemplate);
                break;
            case TEMPLATE11:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate();
                maximumAverageScheduledTimeWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumAverageScheduledTimeWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(wtaRuleTemplateQueryResponse.getUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(wtaRuleTemplateQueryResponse.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(wtaRuleTemplateQueryResponse.getBalanceAdjustment());
                maximumAverageScheduledTimeWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumAverageScheduledTimeWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(maximumAverageScheduledTimeWTATemplate);
                break;
            case TEMPLATE12:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                maximumVetoPerPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumVetoPerPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumVetoPerPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(wtaRuleTemplateQueryResponse.getMaximumVetoPercentage());
                maximumVetoPerPeriodWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());

                wtaBaseRuleTemplates = save(maximumVetoPerPeriodWTATemplate);
                break;
            case TEMPLATE13:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
                numberOfWeekendShiftInPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                numberOfWeekendShiftInPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                numberOfWeekendShiftInPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(wtaRuleTemplateQueryResponse.getNumberShiftsPerPeriod());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(wtaRuleTemplateQueryResponse.getNumberOfWeeks());
                numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(wtaRuleTemplateQueryResponse.getFromDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setFromTime(wtaRuleTemplateQueryResponse.getFromTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToTime(wtaRuleTemplateQueryResponse.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(wtaRuleTemplateQueryResponse.getToDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setProportional(wtaRuleTemplateQueryResponse.getProportional());
                numberOfWeekendShiftInPeriodWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());

                wtaBaseRuleTemplates = save(numberOfWeekendShiftInPeriodWTATemplate);
                break;
            case TEMPLATE14:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                careDayCheckWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                careDayCheckWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                careDayCheckWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                careDayCheckWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                careDayCheckWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());

                wtaBaseRuleTemplates = save(careDayCheckWTATemplate);
                break;
            case TEMPLATE15:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                minimumDailyRestingTimeWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumDailyRestingTimeWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumDailyRestingTimeWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(wtaRuleTemplateQueryResponse.getContinuousDayRestHours());
                minimumDailyRestingTimeWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());
                wtaBaseRuleTemplates = save(minimumDailyRestingTimeWTATemplate);
                break;
            case TEMPLATE16:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                minimumDurationBetweenShiftWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumDurationBetweenShiftWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumDurationBetweenShiftWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(wtaRuleTemplateQueryResponse.getMinimumDurationBetweenShifts());
                minimumDurationBetweenShiftWTATemplate.setActive(wtaRuleTemplateQueryResponse.getActive());

                wtaBaseRuleTemplates = save(minimumDurationBetweenShiftWTATemplate);
                break;
            case TEMPLATE17:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                minimumWeeklyRestPeriodWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                minimumWeeklyRestPeriodWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                minimumWeeklyRestPeriodWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(wtaRuleTemplateQueryResponse.getContinuousWeekRest());
                wtaBaseRuleTemplates = save(minimumWeeklyRestPeriodWTATemplate);
                break;
            case TEMPLATE18:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                shortestAndAverageDailyRestWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                shortestAndAverageDailyRestWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                shortestAndAverageDailyRestWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                shortestAndAverageDailyRestWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(wtaRuleTemplateQueryResponse.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(wtaRuleTemplateQueryResponse.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(wtaRuleTemplateQueryResponse.getShiftAffiliation());
                wtaBaseRuleTemplates = save(shortestAndAverageDailyRestWTATemplate);
                break;
            case TEMPLATE19:
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new MaximumShiftsInIntervalWTATemplate();
                maximumShiftsInIntervalWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumShiftsInIntervalWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumShiftsInIntervalWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(wtaRuleTemplateQueryResponse.getBalanceType());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(wtaRuleTemplateQueryResponse.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(wtaRuleTemplateQueryResponse.getOnlyCompositeShifts());
                wtaBaseRuleTemplates = maximumShiftsInIntervalWTATemplate;
                break;
            case TEMPLATE20:
                MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = new MaximumSeniorDaysInYearWTATemplate();
                maximumSeniorDaysInYearWTATemplate.setName(wtaRuleTemplateQueryResponse.getName());
                maximumSeniorDaysInYearWTATemplate.setTemplateType(wtaRuleTemplateQueryResponse.getTemplateType());
                maximumSeniorDaysInYearWTATemplate.setDescription(wtaRuleTemplateQueryResponse.getDescription());
                maximumSeniorDaysInYearWTATemplate.setIntervalLength(wtaRuleTemplateQueryResponse.getIntervalLength());
                maximumSeniorDaysInYearWTATemplate.setIntervalUnit(wtaRuleTemplateQueryResponse.getIntervalUnit());
                maximumSeniorDaysInYearWTATemplate.setValidationStartDateMillis(wtaRuleTemplateQueryResponse.getValidationStartDateMillis());
                maximumSeniorDaysInYearWTATemplate.setDaysLimit(wtaRuleTemplateQueryResponse.getDaysLimit());
                maximumSeniorDaysInYearWTATemplate.setActivityCode(wtaRuleTemplateQueryResponse.getActivityCode());

                wtaBaseRuleTemplates = maximumSeniorDaysInYearWTATemplate;
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");


        }
        RuleTemplateCategory ruleTemplateCategory = new RuleTemplateCategory(wtaRuleTemplateQueryResponse.getRuleTemplateCategory().getName(),
                wtaRuleTemplateQueryResponse.getRuleTemplateCategory().getDescription(), false);
        List<WTABaseRuleTemplate> wtaBaseRuleTemplateList = new ArrayList<>();
        wtaBaseRuleTemplateList.add(wtaBaseRuleTemplates);
        ruleTemplateCategory.setWtaBaseRuleTemplates(wtaBaseRuleTemplateList);
        save(ruleTemplateCategory);
        return wtaBaseRuleTemplateList.get(0);
    }

    private void preparePosition(Position oldPosition, PositionDTO positionDTO) {
        if (!oldPosition.getExpertise().getId().equals(positionDTO.getExpertiseId())) {
            WTAWithRuleTemplateDTO wtaWithRuleTemplateDTO = workingTimeAgreementGraphRepository.getWTAByExpertiseAndCountry(positionDTO.getExpertiseId());

            if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getExpertise()).isPresent()) {
                throw new DataNotFoundByIdException("Invalid Expertize" + positionDTO.getExpertiseId());
            }
            if (!Optional.ofNullable(wtaWithRuleTemplateDTO.getName()).isPresent()) {
                logger.info("Expertise Doesn't contains WTA.Please select different Expertise" + positionDTO.getExpertiseId());
                throw new DataNotFoundByIdException("Expertise Doesn't contains WTA.Please select different Expertise");
            } else {
                WorkingTimeAgreement wta = copyWTASettingAndRuleTemplateWithCategory(wtaWithRuleTemplateDTO);
                WorkingTimeAgreement oldWta = oldPosition.getWta();
                oldPosition.setWta(wta);
                wta.setWta(oldWta);
                save(wta);
                workingTimeAgreementGraphRepository.breakRelationFromOldWTA(oldPosition.getId(), oldWta.getId());
            }
            oldPosition.setExpertise(wtaWithRuleTemplateDTO.getExpertise());
        }


        if (!oldPosition.getPositionName().getId().equals(positionDTO.getPositionNameId())) {
            PositionName positionName = positionNameGraphRepository.findOne(positionDTO.getPositionNameId());
            if (!Optional.ofNullable(positionName).isPresent()) {
                throw new DataNotFoundByIdException("PositionName Cannot be null" + positionDTO.getPositionNameId());
            }
            oldPosition.setPositionName(positionName);
        }

        if (!oldPosition.getEmploymentType().getId().equals(positionDTO.getEmploymentTypeId())) {
            EmploymentType employmentType = employmentTypeGraphRepository.findOne(positionDTO.getEmploymentTypeId());
            if (!Optional.ofNullable(employmentType).isPresent()) {
                throw new DataNotFoundByIdException("employmentType Cannot be null" + positionDTO.getEmploymentTypeId());
            }
            oldPosition.setEmploymentType(employmentType);
        }


        oldPosition.setStartDate(positionDTO.getStartDate());
        oldPosition.setEndDate(positionDTO.getEndDate());
        oldPosition.setWorkingDaysInWeek(positionDTO.getWorkingDaysInWeek());
        oldPosition.setTotalWeeklyHours(positionDTO.getTotalWeeklyHours());
        oldPosition.setAvgDailyWorkingHours(positionDTO.getAvgDailyWorkingHours());
        oldPosition.setHourlyWages(positionDTO.getHourlyWages());
        oldPosition.setSalary(positionDTO.getSalary());

    }

    /*
     * @auth vipul
     * used to get all positions of organization n buy organization and staff Id
     * */
    public List<PositionQueryResult> getAllPositionByStaff(long id, long unitEmploymentId, long staffId, String
            type) {

        Long unitId = organizationService.getOrganization(id, type);

        Staff staff = staffGraphRepository.findOne(staffId);
        if (!Optional.ofNullable(staff).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Staff Id" + staffId);
        }

        return positionGraphRepository.getAllPositionByStaff(unitId, unitEmploymentId, staffId);
    }

}
