package com.kairos.service.agreement.wta;

import com.kairos.constants.RuleTemplates;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.RuleTemplateCategoryDTO;
import com.kairos.persistence.model.user.agreement.wta.WTADTO;
import com.kairos.persistence.model.user.agreement.wta.WTAResponseDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.PhaseTemplateValue;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.*;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.RuleTemplates.getByTemplateType;
import static com.kairos.persistence.model.user.agreement.cta.RuleTemplateCategoryType.WTA;

/**
 * Created by vipul on 19/12/17.
 */

//@Transactional
@Service
public class WTAOrganizationService extends UserBaseService {

    @Inject
    private WorkingTimeAgreementGraphRepository workingTimeAgreementGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    @Inject
    private CountryService countryService;
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaRuleTemplateGraphRepository;

    private final Logger logger = LoggerFactory.getLogger(WTAOrganizationService.class);

    public List<WTAResponseDTO> getAllWTAByOrganization(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        List<WTAResponseDTO> workingTimeAgreements = workingTimeAgreementGraphRepository.getWtaByOrganization(unitId);
        return workingTimeAgreements;
    }


    public WorkingTimeAgreement updateWtaOfOrganization(Long unitId, Long wtaId, WTADTO updateDTO) {
        Organization organization = organizationGraphRepository.findOne(unitId, 1);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }

        WorkingTimeAgreement oldWta = workingTimeAgreementGraphRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }


        WorkingTimeAgreement newWta = new WorkingTimeAgreement();

        boolean isWTAAlreadyExists = workingTimeAgreementGraphRepository.checkUniqueWTANameInOrganization("(?i)" + updateDTO.getName(), unitId, wtaId);
        if (isWTAAlreadyExists) {
            logger.info("Duplicate WTA name in organization :", wtaId);
            throw new DuplicateDataException("Duplicate WTA name in organization " + updateDTO.getName());
        }
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed at unit level :", wtaId);
            throw new ActionNotPermittedException("Expertise can't be changed");
        }

        //Copying Properties
        BeanUtils.copyProperties(oldWta, newWta);
        newWta.setId(null);
        newWta.setDeleted(true);
        newWta.setStartDateMillis(oldWta.getStartDateMillis());
        newWta.setEndDateMillis(updateDTO.getStartDateMillis());
        newWta.setCountryParentWTA(null);
        newWta.getRuleTemplates().forEach(ruleTemplate -> {
            if (Optional.ofNullable(ruleTemplate.getPhaseTemplateValues()).isPresent()) {
                ruleTemplate.getPhaseTemplateValues().forEach(phaseTemplateValue -> {
                });
            }
        });

        ruleTemplateCategoryGraphRepository.detachPreviousRuleTemplates(oldWta.getId());
        save(newWta);
        if (Optional.ofNullable(oldWta.getParentWTA()).isPresent()) {
            workingTimeAgreementGraphRepository.removeOldParentWTAMapping(oldWta.getParentWTA().getId());
        }

        oldWta.setName(updateDTO.getName());
        oldWta.setDescription(updateDTO.getDescription());
        if (updateDTO.getStartDateMillis() < System.currentTimeMillis()) {
            throw new ActionNotPermittedException("Start date cant be less than current Date " + oldWta.getId());
        }
        oldWta.setStartDateMillis(updateDTO.getStartDateMillis());
        oldWta.setEndDateMillis(updateDTO.getEndDateMillis());
        oldWta.setExpertise(oldWta.getExpertise());
        oldWta.setParentWTA(newWta);
        oldWta.setDisabled(false);

        List<WTABaseRuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {

            ruleTemplates = copyRuleTemplates(oldWta.getRuleTemplates(), updateDTO.getRuleTemplates());
            oldWta.setRuleTemplates(ruleTemplates);
        }
        //oldWta.setOrganization(organization);
        //organization.addWorkingTimeAgreements(newWta);


        save(oldWta);
        //Preparing Response for frontend
        //workingTimeAgreementGraphRepository.removeOldWorkingTimeAgreement(oldWta.getId(), organization.getId(), updateDTO.getStartDateMillis());
        oldWta.setParentWTA(newWta.getParentWTA());

        oldWta.getExpertise().setCountry(null);
        return oldWta;
    }


    private RuleTemplateCategory getCategory(List<WTABaseRuleTemplate> ruleTemplates, Long id, String ruleTemplateCategoryName) {
        RuleTemplateCategory ruleTemplateCategory = null;
        if (ruleTemplates != null) {
            for (RuleTemplate ruletemplate : ruleTemplates) {
                if (ruletemplate.getId().equals(id) && ruleTemplateCategoryName.equalsIgnoreCase(ruletemplate.getRuleTemplateCategory().getName())) { // if category is not changed so Assign from previous
                    ruleTemplateCategory = ruletemplate.getRuleTemplateCategory();
                    break;
                }
            }
        }
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(ruleTemplateCategoryName, WTA);
            // if rule template is still null so this category does not exist.
            if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
                logger.info("category does not exist in when updating wta's rule template  :", ruleTemplateCategoryName);
                throw new DataNotFoundByIdException("category : " + ruleTemplateCategoryName + " does not exist in when updating wta's rule template  :" + id);
            }
        }
        return ruleTemplateCategory;
    }

    public List<PhaseTemplateValue> copyPhaseTemplateValue(List<PhaseTemplateValue> phaseTemplateValues) {
        if(phaseTemplateValues==null){
            return null;
        }

        List<PhaseTemplateValue> phases = new ArrayList<>(4);
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            PhaseTemplateValue newPhaseTemplateValue = new PhaseTemplateValue();
            newPhaseTemplateValue.setDisabled(phaseTemplateValue.isDisabled());
            newPhaseTemplateValue.setManagementValue(phaseTemplateValue.getManagementValue());
            newPhaseTemplateValue.setOptional(phaseTemplateValue.isOptional());
            newPhaseTemplateValue.setStaffValue(phaseTemplateValue.getStaffValue());
            newPhaseTemplateValue.setOptionalFrequency(phaseTemplateValue.getOptionalFrequency());
            newPhaseTemplateValue.setPhaseId(phaseTemplateValue.getPhaseId());
            newPhaseTemplateValue.setSequence(phaseTemplateValue.getSequence());
            newPhaseTemplateValue.setDeleted(false);
            newPhaseTemplateValue.setPhaseName(phaseTemplateValue.getPhaseName());
            phases.add(newPhaseTemplateValue);
        }
        return phases;
    }

    protected List<WTABaseRuleTemplate> copyRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates, List<RuleTemplateCategoryDTO> ruleTemplatesNewObjects) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>(20);
        for (RuleTemplateCategoryDTO ruleTemplate : ruleTemplatesNewObjects) {
            RuleTemplateCategory ruleTemplateCategory = null;
            ruleTemplateCategory = getCategory(ruleTemplates, ruleTemplate.getId(), ruleTemplate.getRuleTemplateCategory().getName());
            List<PhaseTemplateValue> phaseTemplateValues = copyPhaseTemplateValue(ruleTemplate.getPhaseTemplateValues());
            RuleTemplates ruleTemplateType = getByTemplateType(ruleTemplate.getTemplateType());
            switch (ruleTemplateType) {
                case MAXIMUM_SHIFT_LENGTH:
                    MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                    maximumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumShiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    maximumShiftLengthWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());

                    wtaBaseRuleTemplates.add(maximumShiftLengthWTATemplate);
                    break;

                case MINIMUM_SHIFT_LENGTH:
                    MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                    minimumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    minimumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    minimumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    minimumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumShiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumShiftLengthWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(minimumShiftLengthWTATemplate);

                    break;

                case MAXIMUM_NUMBER_OF_CONSECUTIVE_DAYS:
                    MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                    maximumConsecutiveWorkingDaysWTATemplate.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingDaysWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    maximumConsecutiveWorkingDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumConsecutiveWorkingDaysWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(maximumConsecutiveWorkingDaysWTATemplate);
                    break;

                case MINIMUM_REST_AFTER_CONSECUTIVE_DAYS_WORKED:
                    MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                    minimumRestInConsecutiveDaysWTATemplate.setName(ruleTemplate.getName());
                    minimumRestInConsecutiveDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(ruleTemplate.getDaysWorked());
                    minimumRestInConsecutiveDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestInConsecutiveDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestInConsecutiveDaysWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumRestInConsecutiveDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumRestInConsecutiveDaysWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());

                    wtaBaseRuleTemplates.add(minimumRestInConsecutiveDaysWTATemplate);
                    break;

                case MAXIMUM_NIGHT_SHIFTS_LENGTH:
                    MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                    maximumNightShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumNightShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumNightShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumNightShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumNightShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumNightShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumNightShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumNightShiftLengthWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    maximumNightShiftLengthWTATemplate.setPhaseTemplateValues(phaseTemplateValues);

                    wtaBaseRuleTemplates.add(maximumNightShiftLengthWTATemplate);
                    break;

                case MINIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS:
                    MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                    minimumConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumConsecutiveNightsWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    minimumConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumConsecutiveNightsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumConsecutiveNightsWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());

                    wtaBaseRuleTemplates.add(minimumConsecutiveNightsWTATemplate);
                    break;

                case MAXIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS:
                    MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                    maximumConsecutiveWorkingNights.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingNights.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingNights.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingNights.setNightsWorked(ruleTemplate.getNightsWorked());
                    maximumConsecutiveWorkingNights.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingNights.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingNights.setDisabled(ruleTemplate.getDisabled());
                    maximumConsecutiveWorkingNights.setPhaseTemplateValues(phaseTemplateValues);
                    maximumConsecutiveWorkingNights.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(maximumConsecutiveWorkingNights);
                    break;
                case MINIMUM_REST_AFTER_CONSECUTIVE_NIGHTS_WORKED:
                    MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                    minimumRestConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumRestConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestConsecutiveNightsWTATemplate.setNightsWorked(ruleTemplate.getNightsWorked());
                    minimumRestConsecutiveNightsWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumRestConsecutiveNightsWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumRestConsecutiveNightsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumRestConsecutiveNightsWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(minimumRestConsecutiveNightsWTATemplate);
                    break;
                case MAXIMUM_NUMBER_OF_WORK_NIGHTS:
                    MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = new MaximumNumberOfNightsWTATemplate();
                    maximumNumberOfNightsWTATemplate.setName(ruleTemplate.getName());
                    maximumNumberOfNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumNumberOfNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumNumberOfNightsWTATemplate.setNightsWorked(ruleTemplate.getNightsWorked());
                    maximumNumberOfNightsWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumNumberOfNightsWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumNumberOfNightsWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumNumberOfNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumNumberOfNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumNumberOfNightsWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    maximumNumberOfNightsWTATemplate.setPhaseTemplateValues(phaseTemplateValues);

                    wtaBaseRuleTemplates.add(maximumNumberOfNightsWTATemplate);
                    break;
                case MINIMUM_NUMBER_OF_DAYS_OFF_PER_PERIOD:
                    MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = new MaximumDaysOffInPeriodWTATemplate();
                    maximumDaysOffInPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumDaysOffInPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumDaysOffInPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumDaysOffInPeriodWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumDaysOffInPeriodWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumDaysOffInPeriodWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumDaysOffInPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumDaysOffInPeriodWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumDaysOffInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    maximumDaysOffInPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumDaysOffInPeriodWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(maximumDaysOffInPeriodWTATemplate);
                    break;
                case MAXIMUM_AVERAGE_SCHEDULED_TIME_PER_WEEK_WITHIN_AN_INTERVAL:
                    MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate();
                    maximumAverageScheduledTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumAverageScheduledTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(ruleTemplate.getUseShiftTimes());
                    maximumAverageScheduledTimeWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumAverageScheduledTimeWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(ruleTemplate.getMaximumAvgTime());
                    maximumAverageScheduledTimeWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(ruleTemplate.getBalanceAdjustment());
                    maximumAverageScheduledTimeWTATemplate.setName(ruleTemplate.getName());
                    maximumAverageScheduledTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumAverageScheduledTimeWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumAverageScheduledTimeWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    maximumAverageScheduledTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);


                    wtaBaseRuleTemplates.add(maximumAverageScheduledTimeWTATemplate);
                    break;
                case MAXIMUM_VETO_PER_PERIOD:
                    MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                    maximumVetoPerPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumVetoPerPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumVetoPerPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(ruleTemplate.getMaximumVetoPercentage());
                    maximumVetoPerPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumVetoPerPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumVetoPerPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    maximumVetoPerPeriodWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(maximumVetoPerPeriodWTATemplate);
                    break;
                case NUMBER_OF_WEEKEND_SHIFTS_IN_A_PERIOD_COMPARED_TO_AVERAGE:
                    NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
                    numberOfWeekendShiftInPeriodWTATemplate.setName(ruleTemplate.getName());
                    numberOfWeekendShiftInPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    numberOfWeekendShiftInPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(ruleTemplate.getNumberShiftsPerPeriod());
                    numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(ruleTemplate.getNumberOfWeeks());
                    numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(ruleTemplate.getFromDayOfWeek());
                    numberOfWeekendShiftInPeriodWTATemplate.setFromTime(ruleTemplate.getFromTime());
                    numberOfWeekendShiftInPeriodWTATemplate.setToTime(ruleTemplate.getToTime());
                    numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(ruleTemplate.getToDayOfWeek());
                    numberOfWeekendShiftInPeriodWTATemplate.setProportional(ruleTemplate.getProportional());
                    numberOfWeekendShiftInPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    numberOfWeekendShiftInPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    numberOfWeekendShiftInPeriodWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    numberOfWeekendShiftInPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    wtaBaseRuleTemplates.add(numberOfWeekendShiftInPeriodWTATemplate);
                    break;
                case CARE_DAYS_CHECK:
                    CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                    careDayCheckWTATemplate.setName(ruleTemplate.getName());
                    careDayCheckWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    careDayCheckWTATemplate.setDescription(ruleTemplate.getDescription());
                    careDayCheckWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    careDayCheckWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    careDayCheckWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    careDayCheckWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    careDayCheckWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    careDayCheckWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    careDayCheckWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    careDayCheckWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(careDayCheckWTATemplate);
                    break;
                case MINIMUM_DAILY_RESTING_TIME:
                    MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                    minimumDailyRestingTimeWTATemplate.setName(ruleTemplate.getName());
                    minimumDailyRestingTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDailyRestingTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(ruleTemplate.getContinuousDayRestHours());
                    minimumDailyRestingTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDailyRestingTimeWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDailyRestingTimeWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumDailyRestingTimeWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(minimumDailyRestingTimeWTATemplate);
                    break;
                case MINIMUM_DURATION_BETWEEN_SHIFTS:
                    MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                    minimumDurationBetweenShiftWTATemplate.setName(ruleTemplate.getName());
                    minimumDurationBetweenShiftWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDurationBetweenShiftWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDurationBetweenShiftWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(ruleTemplate.getMinimumDurationBetweenShifts());
                    minimumDurationBetweenShiftWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDurationBetweenShiftWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDurationBetweenShiftWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumDurationBetweenShiftWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(minimumDurationBetweenShiftWTATemplate);
                    break;
                case MINIMUM_WEEKLY_REST_PERIOD_FIXED_WEEKS:
                    MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                    minimumWeeklyRestPeriodWTATemplate.setName(ruleTemplate.getName());
                    minimumWeeklyRestPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumWeeklyRestPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(ruleTemplate.getContinuousWeekRest());
                    minimumWeeklyRestPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumWeeklyRestPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumWeeklyRestPeriodWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    minimumWeeklyRestPeriodWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(minimumWeeklyRestPeriodWTATemplate);
                    break;
                case SHORTEST_AND_AVERAGE_DAILY_REST_FIXED_TIMES:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                    shortestAndAverageDailyRestWTATemplate.setName(ruleTemplate.getName());
                    shortestAndAverageDailyRestWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    shortestAndAverageDailyRestWTATemplate.setDescription(ruleTemplate.getDescription());
                    shortestAndAverageDailyRestWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    shortestAndAverageDailyRestWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    shortestAndAverageDailyRestWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(ruleTemplate.getContinuousDayRestHours());
                    shortestAndAverageDailyRestWTATemplate.setAverageRest(ruleTemplate.getAverageRest());
                    shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(ruleTemplate.getShiftAffiliation());
                    shortestAndAverageDailyRestWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    shortestAndAverageDailyRestWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    shortestAndAverageDailyRestWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(shortestAndAverageDailyRestWTATemplate);
                    break;
                case MAXIMUM_NUMBER_OF_SHIFTS_PER_INTERVAL:
                    MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new MaximumShiftsInIntervalWTATemplate();
                    maximumShiftsInIntervalWTATemplate.setName(ruleTemplate.getName());
                    maximumShiftsInIntervalWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumShiftsInIntervalWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumShiftsInIntervalWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumShiftsInIntervalWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumShiftsInIntervalWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumShiftsInIntervalWTATemplate.setShiftsLimit(ruleTemplate.getShiftsLimit());
                    maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(ruleTemplate.getOnlyCompositeShifts());
                    maximumShiftsInIntervalWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftsInIntervalWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    maximumShiftsInIntervalWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    wtaBaseRuleTemplates.add(maximumShiftsInIntervalWTATemplate);
                    break;
                case MAXIMUM_SENIOR_DAYS_PER_YEAR:
                    MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = new MaximumSeniorDaysInYearWTATemplate();
                    maximumSeniorDaysInYearWTATemplate.setName(ruleTemplate.getName());
                    maximumSeniorDaysInYearWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumSeniorDaysInYearWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumSeniorDaysInYearWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumSeniorDaysInYearWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumSeniorDaysInYearWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumSeniorDaysInYearWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumSeniorDaysInYearWTATemplate.setActivityCode(ruleTemplate.getActivityCode());
                    maximumSeniorDaysInYearWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumSeniorDaysInYearWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumSeniorDaysInYearWTATemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    maximumSeniorDaysInYearWTATemplate.setPhaseTemplateValues(phaseTemplateValues);
                    wtaBaseRuleTemplates.add(maximumSeniorDaysInYearWTATemplate);
                    break;
                case MAXIMUM_TIME_BANK:
                    MaximumTimeBank maximumTimeBank = new MaximumTimeBank();
                    maximumTimeBank.setName(ruleTemplate.getName());
                    maximumTimeBank.setTemplateType(ruleTemplate.getTemplateType());
                    maximumTimeBank.setDescription(ruleTemplate.getDescription());
                    maximumTimeBank.setFrequency(ruleTemplate.getFrequency());
                    maximumTimeBank.setYellowZone(ruleTemplate.getYellowZone());
                    maximumTimeBank.setForbid(ruleTemplate.isForbid());
                    maximumTimeBank.setAllowExtraActivity(ruleTemplate.isAllowExtraActivity());
                    maximumTimeBank.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumTimeBank.setDisabled(ruleTemplate.getDisabled());
                    maximumTimeBank.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    maximumTimeBank.setPhaseTemplateValues(phaseTemplateValues);
                    wtaBaseRuleTemplates.add(maximumTimeBank);
                    break;
                case MINIMUM_TIME_BANK:
                    MinimumTimeBank minimumTimeBank = new MinimumTimeBank();
                    minimumTimeBank.setName(ruleTemplate.getName());
                    minimumTimeBank.setTemplateType(ruleTemplate.getTemplateType());
                    minimumTimeBank.setDescription(ruleTemplate.getDescription());
                    minimumTimeBank.setFrequency(ruleTemplate.getFrequency());
                    minimumTimeBank.setYellowZone(ruleTemplate.getYellowZone());
                    minimumTimeBank.setForbid(ruleTemplate.isForbid());
                    minimumTimeBank.setAllowExtraActivity(ruleTemplate.isAllowExtraActivity());
                    minimumTimeBank.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumTimeBank.setDisabled(ruleTemplate.getDisabled());
                    minimumTimeBank.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    minimumTimeBank.setPhaseTemplateValues(phaseTemplateValues);
                    wtaBaseRuleTemplates.add(minimumTimeBank);
                    break;
                case BREAKS_IN_SHIFT:
                    BreaksInShift breaksInShift=new BreaksInShift();
                    breaksInShift.setName(ruleTemplate.getName());
                    breaksInShift.setTemplateType(ruleTemplate.getTemplateType());
                    breaksInShift.setDescription(ruleTemplate.getDescription());
                    breaksInShift.setRuleTemplateCategory(ruleTemplateCategory);
                    breaksInShift.setDisabled(ruleTemplate.getDisabled());
                    breaksInShift.setRecommendedValue(ruleTemplate.getRecommendedValue());
                    breaksInShift.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    breaksInShift.setBreakTemplateValues(ruleTemplate.getBreakTemplateValues());
                    wtaBaseRuleTemplates.add(breaksInShift);
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }

        }
        return wtaBaseRuleTemplates;
    }


}