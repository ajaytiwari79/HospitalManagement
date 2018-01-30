package com.kairos.service.agreement.wta;

import com.kairos.config.security.CurrentUserDetails;
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
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.country.CountryService;
import com.kairos.util.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.constants.AppConstants.*;
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
                ruleTemplate.setId(null);

            if (Optional.ofNullable(ruleTemplate.getPhaseTemplateValues()).isPresent()) {
                ruleTemplate.getPhaseTemplateValues().forEach(PhaseTemplateValue->{
                    PhaseTemplateValue.setId(null);
                });
            }


        });


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

            ruleTemplates = copyRuleTemplate(oldWta.getRuleTemplates(), updateDTO.getRuleTemplates());
            oldWta.setRuleTemplates(ruleTemplates);
        }
        //oldWta.setOrganization(organization);
        //organization.addWorkingTimeAgreements(newWta);


        save(oldWta);
        //Preparing Response for frontend
        //workingTimeAgreementGraphRepository.removeOldWorkingTimeAgreement(oldWta.getId(), organization.getId(), updateDTO.getStartDateMillis());
        oldWta.setParentWTA(newWta);

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

    protected List<WTABaseRuleTemplate> copyRuleTemplates(List<WTABaseRuleTemplate> ruleTemplates, List<RuleTemplateCategoryDTO> ruleTemplatesNewObjects) {
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>(20);
        for (RuleTemplateCategoryDTO ruleTemplate : ruleTemplatesNewObjects) {
//            if(Optional.ofNullable(ruleTemplate.getPhaseTemplateValues()).isPresent()){
//                ruleTemplate.getPhaseTemplateValues().forEach(PhaseTemplateValue->{PhaseTemplateValue.setId(null);});
//            }

            RuleTemplateCategory ruleTemplateCategory = null;
            ruleTemplateCategory = getCategory(ruleTemplates, ruleTemplate.getId(), ruleTemplate.getRuleTemplateCategory().getName());

            switch (ruleTemplate.getTemplateType()) {
                case TEMPLATE1:
                    MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                    maximumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumShiftLengthWTATemplate);
                    break;

                case TEMPLATE2:
                    MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                    minimumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    minimumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    minimumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    minimumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumShiftLengthWTATemplate);
                    break;

                case TEMPLATE3:
                    MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                    maximumConsecutiveWorkingDaysWTATemplate.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingDaysWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    maximumConsecutiveWorkingDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());

                    wtaBaseRuleTemplates.add(maximumConsecutiveWorkingDaysWTATemplate);
                    break;

                case TEMPLATE4:
                    MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                    minimumRestInConsecutiveDaysWTATemplate.setName(ruleTemplate.getName());
                    minimumRestInConsecutiveDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(ruleTemplate.getDaysWorked());
                    minimumRestInConsecutiveDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestInConsecutiveDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestInConsecutiveDaysWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    minimumRestInConsecutiveDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());

                    wtaBaseRuleTemplates.add(minimumRestInConsecutiveDaysWTATemplate);
                    break;

                case TEMPLATE5:
                    MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                    maximumNightShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumNightShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumNightShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumNightShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumNightShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumNightShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumNightShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumNightShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(maximumNightShiftLengthWTATemplate);
                    break;

                case TEMPLATE6:
                    MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                    minimumConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumConsecutiveNightsWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    minimumConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumConsecutiveNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(minimumConsecutiveNightsWTATemplate);
                    break;

                case TEMPLATE7:
                    MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                    maximumConsecutiveWorkingNights.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingNights.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingNights.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingNights.setNightsWorked(ruleTemplate.getNightsWorked());
                    maximumConsecutiveWorkingNights.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingNights.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingNights.setDisabled(ruleTemplate.getDisabled());
                    maximumConsecutiveWorkingNights.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumConsecutiveWorkingNights);
                    break;
                case TEMPLATE8:
                    MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                    minimumRestConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumRestConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestConsecutiveNightsWTATemplate.setNightsWorked(ruleTemplate.getNightsWorked());
                    minimumRestConsecutiveNightsWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumRestConsecutiveNightsWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumRestConsecutiveNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumRestConsecutiveNightsWTATemplate);
                    break;
                case TEMPLATE9:
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
                    maximumNumberOfNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(maximumNumberOfNightsWTATemplate);
                    break;
                case TEMPLATE10:
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
                    maximumDaysOffInPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(maximumDaysOffInPeriodWTATemplate);
                    break;
                case TEMPLATE11:
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
                    maximumAverageScheduledTimeWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());


                    wtaBaseRuleTemplates.add(maximumAverageScheduledTimeWTATemplate);
                    break;
                case TEMPLATE12:
                    MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                    maximumVetoPerPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumVetoPerPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumVetoPerPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(ruleTemplate.getMaximumVetoPercentage());
                    maximumVetoPerPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumVetoPerPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumVetoPerPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumVetoPerPeriodWTATemplate);
                    break;
                case TEMPLATE13:
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
                    numberOfWeekendShiftInPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(numberOfWeekendShiftInPeriodWTATemplate);
                    break;
                case TEMPLATE14:
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
                    careDayCheckWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(careDayCheckWTATemplate);
                    break;
                case TEMPLATE15:
                    MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                    minimumDailyRestingTimeWTATemplate.setName(ruleTemplate.getName());
                    minimumDailyRestingTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDailyRestingTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(ruleTemplate.getContinuousDayRestHours());
                    minimumDailyRestingTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDailyRestingTimeWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDailyRestingTimeWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumDailyRestingTimeWTATemplate);
                    break;
                case TEMPLATE16:
                    MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                    minimumDurationBetweenShiftWTATemplate.setName(ruleTemplate.getName());
                    minimumDurationBetweenShiftWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDurationBetweenShiftWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDurationBetweenShiftWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(ruleTemplate.getMinimumDurationBetweenShifts());
                    minimumDurationBetweenShiftWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDurationBetweenShiftWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDurationBetweenShiftWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumDurationBetweenShiftWTATemplate);
                    break;
                case TEMPLATE17:
                    MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                    minimumWeeklyRestPeriodWTATemplate.setName(ruleTemplate.getName());
                    minimumWeeklyRestPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumWeeklyRestPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(ruleTemplate.getContinuousWeekRest());
                    minimumWeeklyRestPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumWeeklyRestPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumWeeklyRestPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumWeeklyRestPeriodWTATemplate);
                    break;
                case TEMPLATE18:
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
                    shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(shortestAndAverageDailyRestWTATemplate);
                    break;
                case TEMPLATE19:
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
                    maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumShiftsInIntervalWTATemplate);
                    break;
                case TEMPLATE20:
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
                    maximumSeniorDaysInYearWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumSeniorDaysInYearWTATemplate);
                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }

        }
        return wtaBaseRuleTemplates;
    }


    public List<WTABaseRuleTemplate> copyRuleTemplate(List<WTABaseRuleTemplate> ruleTemplates, List<RuleTemplateCategoryDTO> ruleTemplateDTOs) {


        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>(20);
        for (RuleTemplateCategoryDTO ruleTemplate : ruleTemplateDTOs) {
            RuleTemplateCategory ruleTemplateCategory = null;
            ruleTemplateCategory = getCategory(ruleTemplates, ruleTemplate.getId(), ruleTemplate.getRuleTemplateCategory().getName());

            if(!ruleTemplates.contains(ruleTemplate)){

               WTABaseRuleTemplate newAddedWTA= copyNewlyAddedRuleTemplates(ruleTemplateCategory,ruleTemplate);
                wtaBaseRuleTemplates.add(newAddedWTA);
                continue;
            }


            WTABaseRuleTemplate oldTemplate = wtaRuleTemplateGraphRepository.findOne(ruleTemplate.getId());
            if (!Optional.ofNullable(oldTemplate).isPresent()) {
                throw new DataNotFoundByIdException("Invalid TemplateType id " + ruleTemplate.getId());
            }

            switch (ruleTemplate.getTemplateType()) {
                case TEMPLATE1:
                    MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = (MaximumShiftLengthWTATemplate) oldTemplate;//oldTemplate;

                    maximumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumShiftLengthWTATemplate);


                    break;
                case TEMPLATE2:
                    MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = (MinimumShiftLengthWTATemplate) oldTemplate;
                    minimumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    minimumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    minimumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    minimumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumShiftLengthWTATemplate);
                    break;
                case TEMPLATE3:
                    MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = (MaximumConsecutiveWorkingDaysWTATemplate) oldTemplate;
                    maximumConsecutiveWorkingDaysWTATemplate.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingDaysWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    maximumConsecutiveWorkingDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());

                    wtaBaseRuleTemplates.add(maximumConsecutiveWorkingDaysWTATemplate);
                    break;
                case TEMPLATE4:
                    MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = (MinimumRestInConsecutiveDaysWTATemplate) oldTemplate;
                    minimumRestInConsecutiveDaysWTATemplate.setName(ruleTemplate.getName());
                    minimumRestInConsecutiveDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(ruleTemplate.getDaysWorked());
                    minimumRestInConsecutiveDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestInConsecutiveDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestInConsecutiveDaysWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    minimumRestInConsecutiveDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());

                    wtaBaseRuleTemplates.add(minimumRestInConsecutiveDaysWTATemplate);
                    break;

                case TEMPLATE5:
                    MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = (MaximumNightShiftLengthWTATemplate) oldTemplate;
                    maximumNightShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumNightShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumNightShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumNightShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumNightShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumNightShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumNightShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumNightShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(maximumNightShiftLengthWTATemplate);
                    break;

                case TEMPLATE6:
                    MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = (MinimumConsecutiveNightsWTATemplate) oldTemplate;
                    minimumConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumConsecutiveNightsWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    minimumConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumConsecutiveNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(minimumConsecutiveNightsWTATemplate);
                    break;

                case TEMPLATE7:
                    MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = (MaximumConsecutiveWorkingNightsWTATemplate) oldTemplate;//oldTemplate;
                    maximumConsecutiveWorkingNights.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingNights.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingNights.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingNights.setNightsWorked(ruleTemplate.getNightsWorked());
                    maximumConsecutiveWorkingNights.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingNights.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingNights.setDisabled(ruleTemplate.getDisabled());
                    maximumConsecutiveWorkingNights.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumConsecutiveWorkingNights);
                    break;
                case TEMPLATE8:
                    MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = (MinimumRestConsecutiveNightsWTATemplate) oldTemplate;
                    minimumRestConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumRestConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestConsecutiveNightsWTATemplate.setNightsWorked(ruleTemplate.getNightsWorked());
                    minimumRestConsecutiveNightsWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumRestConsecutiveNightsWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumRestConsecutiveNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumRestConsecutiveNightsWTATemplate);
                    break;
                case TEMPLATE9:
                    MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = (MaximumNumberOfNightsWTATemplate) oldTemplate;
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
                    maximumNumberOfNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(maximumNumberOfNightsWTATemplate);
                    break;
                case TEMPLATE10:
                    MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = (MaximumDaysOffInPeriodWTATemplate) oldTemplate;
                    maximumDaysOffInPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumDaysOffInPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumDaysOffInPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumDaysOffInPeriodWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumDaysOffInPeriodWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumDaysOffInPeriodWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumDaysOffInPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumDaysOffInPeriodWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumDaysOffInPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplates.add(maximumDaysOffInPeriodWTATemplate);
                    break;
                case TEMPLATE11:
                    MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = (MaximumAverageScheduledTimeWTATemplate) oldTemplate;
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
                    maximumAverageScheduledTimeWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());


                    wtaBaseRuleTemplates.add(maximumAverageScheduledTimeWTATemplate);
                    break;
                case TEMPLATE12:
                    MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = (MaximumVetoPerPeriodWTATemplate) oldTemplate;
                    maximumVetoPerPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumVetoPerPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumVetoPerPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(ruleTemplate.getMaximumVetoPercentage());
                    maximumVetoPerPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumVetoPerPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumVetoPerPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumVetoPerPeriodWTATemplate);
                    break;
                case TEMPLATE13:
                    NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = (NumberOfWeekendShiftInPeriodWTATemplate) oldTemplate;
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
                    numberOfWeekendShiftInPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(numberOfWeekendShiftInPeriodWTATemplate);
                    break;
                case TEMPLATE14:
                    CareDayCheckWTATemplate careDayCheckWTATemplate = (CareDayCheckWTATemplate) oldTemplate;
                    careDayCheckWTATemplate.setName(ruleTemplate.getName());
                    careDayCheckWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    careDayCheckWTATemplate.setDescription(ruleTemplate.getDescription());
                    careDayCheckWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    careDayCheckWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    careDayCheckWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    careDayCheckWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    careDayCheckWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    careDayCheckWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    careDayCheckWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(careDayCheckWTATemplate);
                    break;
                case TEMPLATE15:
                    MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = (MinimumDailyRestingTimeWTATemplate) oldTemplate;
                    minimumDailyRestingTimeWTATemplate.setName(ruleTemplate.getName());
                    minimumDailyRestingTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDailyRestingTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(ruleTemplate.getContinuousDayRestHours());
                    minimumDailyRestingTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDailyRestingTimeWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDailyRestingTimeWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumDailyRestingTimeWTATemplate);
                    break;
                case TEMPLATE16:
                    MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = (MinimumDurationBetweenShiftWTATemplate) oldTemplate;
                    minimumDurationBetweenShiftWTATemplate.setName(ruleTemplate.getName());
                    minimumDurationBetweenShiftWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDurationBetweenShiftWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDurationBetweenShiftWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(ruleTemplate.getMinimumDurationBetweenShifts());
                    minimumDurationBetweenShiftWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDurationBetweenShiftWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDurationBetweenShiftWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumDurationBetweenShiftWTATemplate);
                    break;
                case TEMPLATE17:
                    MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = (MinimumWeeklyRestPeriodWTATemplate) oldTemplate;
                    minimumWeeklyRestPeriodWTATemplate.setName(ruleTemplate.getName());
                    minimumWeeklyRestPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumWeeklyRestPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(ruleTemplate.getContinuousWeekRest());
                    minimumWeeklyRestPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumWeeklyRestPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumWeeklyRestPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(minimumWeeklyRestPeriodWTATemplate);
                    break;
                case TEMPLATE18:
                    ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = (ShortestAndAverageDailyRestWTATemplate) oldTemplate;
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
                    shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(shortestAndAverageDailyRestWTATemplate);
                    break;
                case TEMPLATE19:
                    MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = (MaximumShiftsInIntervalWTATemplate) oldTemplate;
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
                    maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumShiftsInIntervalWTATemplate);
                    break;
                case TEMPLATE20:
                    MaximumSeniorDaysInYearWTATemplate maximumSeniorDaysInYearWTATemplate = (MaximumSeniorDaysInYearWTATemplate) oldTemplate;
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
                    maximumSeniorDaysInYearWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplates.add(maximumSeniorDaysInYearWTATemplate);
                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }


//        oldTemplate.setRuleTemplateCategory(ruleTemplateCategory);
//        CurrentUserDetails currentUserDetails = UserContext.getUserDetails();
//        oldTemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
//
//        oldTemplate.setDisabled(ruleTemplate.getDisabled());
//        oldTemplate.setRuleTemplateCategory(ruleTemplateCategory);
//        oldTemplate.setRecommendedValue(ruleTemplate.getRecommendedValue());
//
//        oldTemplate.setLastUpdatedBy(currentUserDetails.getFirstName());
//
//        save(oldTemplate);

        }
        return wtaBaseRuleTemplates;


    }




















    protected WTABaseRuleTemplate copyNewlyAddedRuleTemplates(RuleTemplateCategory ruleTemplateCategory, RuleTemplateCategoryDTO ruleTemplate) {
        //List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>(20);

        WTABaseRuleTemplate wtaBaseRuleTemplate;

        switch (ruleTemplate.getTemplateType()) {
                case TEMPLATE1:
                    MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                    maximumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplate = maximumShiftLengthWTATemplate;
                    break;

                case TEMPLATE2:
                    MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                    minimumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    minimumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    minimumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    minimumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate =  minimumShiftLengthWTATemplate;
                    break;

                case TEMPLATE3:
                    MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                    maximumConsecutiveWorkingDaysWTATemplate.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingDaysWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    maximumConsecutiveWorkingDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());

                    wtaBaseRuleTemplate =maximumConsecutiveWorkingDaysWTATemplate;
                    break;

                case TEMPLATE4:
                    MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                    minimumRestInConsecutiveDaysWTATemplate.setName(ruleTemplate.getName());
                    minimumRestInConsecutiveDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(ruleTemplate.getDaysWorked());
                    minimumRestInConsecutiveDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestInConsecutiveDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestInConsecutiveDaysWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    minimumRestInConsecutiveDaysWTATemplate.setDisabled(ruleTemplate.getDisabled());

                    wtaBaseRuleTemplate = minimumRestInConsecutiveDaysWTATemplate;
                    break;

                case TEMPLATE5:
                    MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                    maximumNightShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumNightShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumNightShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumNightShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumNightShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumNightShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumNightShiftLengthWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumNightShiftLengthWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplate=maximumNightShiftLengthWTATemplate;
                    break;

                case TEMPLATE6:
                    MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                    minimumConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumConsecutiveNightsWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    minimumConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumConsecutiveNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplate=minimumConsecutiveNightsWTATemplate;
                    break;

                case TEMPLATE7:
                    MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                    maximumConsecutiveWorkingNights.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingNights.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingNights.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingNights.setNightsWorked(ruleTemplate.getNightsWorked());
                    maximumConsecutiveWorkingNights.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(ruleTemplate.getCheckAgainstTimeRules());
                    maximumConsecutiveWorkingNights.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingNights.setDisabled(ruleTemplate.getDisabled());
                    maximumConsecutiveWorkingNights.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=maximumConsecutiveWorkingNights;
                    break;
                case TEMPLATE8:
                    MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                    minimumRestConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumRestConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumRestConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumRestConsecutiveNightsWTATemplate.setNightsWorked(ruleTemplate.getNightsWorked());
                    minimumRestConsecutiveNightsWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumRestConsecutiveNightsWTATemplate.setMinimumRest(ruleTemplate.getMinimumRest());
                    minimumRestConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumRestConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumRestConsecutiveNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=minimumRestConsecutiveNightsWTATemplate;
                    break;
                case TEMPLATE9:
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
                    maximumNumberOfNightsWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplate=maximumNumberOfNightsWTATemplate;
                    break;
                case TEMPLATE10:
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
                    maximumDaysOffInPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());

                    wtaBaseRuleTemplate=maximumDaysOffInPeriodWTATemplate;
                    break;
                case TEMPLATE11:
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
                    maximumAverageScheduledTimeWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());


                    wtaBaseRuleTemplate=maximumAverageScheduledTimeWTATemplate;
                    break;
                case TEMPLATE12:
                    MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                    maximumVetoPerPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumVetoPerPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumVetoPerPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(ruleTemplate.getMaximumVetoPercentage());
                    maximumVetoPerPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumVetoPerPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    maximumVetoPerPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=maximumVetoPerPeriodWTATemplate;
                    break;
                case TEMPLATE13:
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
                    numberOfWeekendShiftInPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=numberOfWeekendShiftInPeriodWTATemplate;
                    break;
                case TEMPLATE14:
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
                    careDayCheckWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=careDayCheckWTATemplate;
                    break;
                case TEMPLATE15:
                    MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                    minimumDailyRestingTimeWTATemplate.setName(ruleTemplate.getName());
                    minimumDailyRestingTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDailyRestingTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(ruleTemplate.getContinuousDayRestHours());
                    minimumDailyRestingTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDailyRestingTimeWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDailyRestingTimeWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=minimumDailyRestingTimeWTATemplate;
                    break;
                case TEMPLATE16:
                    MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                    minimumDurationBetweenShiftWTATemplate.setName(ruleTemplate.getName());
                    minimumDurationBetweenShiftWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDurationBetweenShiftWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDurationBetweenShiftWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(ruleTemplate.getMinimumDurationBetweenShifts());
                    minimumDurationBetweenShiftWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDurationBetweenShiftWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumDurationBetweenShiftWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=minimumDurationBetweenShiftWTATemplate;
                    break;
                case TEMPLATE17:
                    MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                    minimumWeeklyRestPeriodWTATemplate.setName(ruleTemplate.getName());
                    minimumWeeklyRestPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumWeeklyRestPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(ruleTemplate.getContinuousWeekRest());
                    minimumWeeklyRestPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumWeeklyRestPeriodWTATemplate.setDisabled(ruleTemplate.getDisabled());
                    minimumWeeklyRestPeriodWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=minimumWeeklyRestPeriodWTATemplate;
                    break;
                case TEMPLATE18:
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
                    shortestAndAverageDailyRestWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=shortestAndAverageDailyRestWTATemplate;
                    break;
                case TEMPLATE19:
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
                    maximumShiftsInIntervalWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=maximumShiftsInIntervalWTATemplate;
                    break;
                case TEMPLATE20:
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
                    maximumSeniorDaysInYearWTATemplate.setPhaseTemplateValues(ruleTemplate.getPhaseTemplateValues());
                    wtaBaseRuleTemplate=maximumSeniorDaysInYearWTATemplate;
                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }


        return wtaBaseRuleTemplate;
    }

}