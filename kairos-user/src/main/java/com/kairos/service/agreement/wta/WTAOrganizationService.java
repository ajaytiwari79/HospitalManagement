package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.RuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.WTAOrganizationMappingDTO;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.model.user.agreement.wta.templates.template_types.*;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.response.dto.web.WTARuleTemplateDTO;
import com.kairos.response.dto.web.WTAUpdateDTO;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private WTAService wtaService;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryGraphRepository;
    private final Logger logger = LoggerFactory.getLogger(WTAOrganizationService.class);

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByOrganization(Long unitId) {
        Organization organization = organizationGraphRepository.findOne(unitId, 0);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        List<WTAWithCountryAndOrganizationTypeDTO> workingTimeAgreements = workingTimeAgreementGraphRepository.getWtaByOrganization(unitId);
        return workingTimeAgreements;
    }

    public HashMap<String, Object> getWTAForOrganizationMapping(Long unitId) {
        HashMap<String, Object> hs = new HashMap<>(2);

        Organization organization = organizationGraphRepository.findOne(unitId, 1);
        if (!Optional.ofNullable(organization).isPresent()) {
            throw new DataNotFoundByIdException("Invalid unit  " + unitId);
        }
        if (Optional.ofNullable(organization.getOrganizationSubTypes()).isPresent()) {
            List<Long> subTypes = organization.getOrganizationSubTypes().stream().map(type -> type.getId()).collect(Collectors.toList());
            List<WTAOrganizationMappingDTO> countryWTA = workingTimeAgreementGraphRepository.getAllWTAByOrganizationSubType(subTypes);
            hs.put("countryWTA", countryWTA);
        }
        List<WTAOrganizationMappingDTO> organizationWTA = workingTimeAgreementGraphRepository.getAllWTAByOrganizationId(unitId);
        hs.put("organizationWTA", organizationWTA);
        return hs;
    }

    public WorkingTimeAgreement linkWTAWithOrganization(Long workingTimeAgreementId, Long unitId, boolean checked) {
        WorkingTimeAgreement workingTimeAgreement;
        if (checked) {
            workingTimeAgreement = workingTimeAgreementGraphRepository.findOne(workingTimeAgreementId, 2);
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            if (!Optional.ofNullable(workingTimeAgreement).isPresent()) {
                throw new DataNotFoundByIdException("Invalid wtaId  " + workingTimeAgreementId);
            }
            WorkingTimeAgreement.copyProperties(workingTimeAgreement, newWtaObject, "id");
            List<RuleTemplate> ruleTemplates = new ArrayList<>();
            if (workingTimeAgreement.getRuleTemplates().size() > 0) {
                ruleTemplates = copyRuleTemplates(workingTimeAgreement.getRuleTemplates());
            }
            setExpertiseAndUnlinkBasicProperties(workingTimeAgreement, newWtaObject, unitId);
            newWtaObject.setRuleTemplates(ruleTemplates);
            save(newWtaObject);
            newWtaObject.setOrganization(null);
            newWtaObject.setParentWTA(workingTimeAgreement.basicDetails());
            return newWtaObject;
        } else {
            workingTimeAgreement = workingTimeAgreementGraphRepository.getChildWTAbyParentWTA(workingTimeAgreementId, unitId);
            workingTimeAgreement.setDeleted(true);
            save(workingTimeAgreement);
            return workingTimeAgreement;
        }
    }

    private List<RuleTemplate> copyRuleTemplates(List<RuleTemplate> ruleTemplates) {
        List<RuleTemplate> wtaBaseRuleTemplates = new ArrayList<RuleTemplate>(20);
        for (RuleTemplate ruleTemplate : ruleTemplates) {
            WTABaseRuleTemplate ruleTemplateCopy = new WTABaseRuleTemplate();
            BeanUtils.copyProperties(ruleTemplate, ruleTemplateCopy, "id");
            ruleTemplateCopy.setRuleTemplateCategory(null);
            RuleTemplateCategory rtcCopy = ruleTemplate.getRuleTemplateCategory();
            rtcCopy.setId(null);
            ruleTemplateCopy.setRuleTemplateCategory(rtcCopy);
            wtaBaseRuleTemplates.add(ruleTemplateCopy);
        }
        return wtaBaseRuleTemplates;
    }

    private void setExpertiseAndUnlinkBasicProperties(WorkingTimeAgreement oldWTA, WorkingTimeAgreement newWta, Long unitId) {
        Expertise e = new Expertise();
        e.setName(oldWTA.getExpertise().getName());
        e.setDescription(oldWTA.getExpertise().getDescription());
        e.setId(oldWTA.getExpertise().getId());
        newWta.setExpertise(e);
        newWta.setId(null);
        newWta.setCountry(null);
        newWta.setOrganizationType(null);
        newWta.setOrganizationSubType(null);
        newWta.setOrganization(organizationGraphRepository.findOne(unitId, 0));
        newWta.setParentWTA(oldWTA);
    }

    public WorkingTimeAgreement updateWtaOfOrganization(Long unitId, Long wtaId, WTAUpdateDTO updateDTO) {
        WorkingTimeAgreement oldWta = workingTimeAgreementGraphRepository.findOne(wtaId, 2);
        if (!Optional.ofNullable(oldWta).isPresent()) {
            logger.info("wta not found while updating at unit %d", wtaId);
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        // TODO may be again changed in future.

        boolean isWTAAlreadyExists = workingTimeAgreementGraphRepository.checkUniqueWTANameInOrganization("(?i)" + updateDTO.getName(), unitId, wtaId);
        if (isWTAAlreadyExists) {
            logger.info("Duplicate WTA name in organization :", wtaId);
            throw new DuplicateDataException("Duplicate WTA name in organization " + updateDTO.getName());
        }
        if (oldWta.getExpertise().getId() != updateDTO.getExpertiseId()) {
            logger.info("Expertise cant be changed at unit level :", wtaId);
            throw new ActionNotPermittedException("Expertise can't be changed");
        }

        if (oldWta.getRuleTemplates().size() != updateDTO.getRuleTemplates().size()) {
            throw new ActionNotPermittedException("Missing rule templates");
        }
        WorkingTimeAgreement newWta = new WorkingTimeAgreement();
        newWta.setId(null);
        newWta.setName(updateDTO.getName());
        newWta.setDescription(updateDTO.getDescription());
        newWta.setStartDateMillis(updateDTO.getStartDateMillis());
        newWta.setEndDateMillis(updateDTO.getEndDateMillis());
        newWta.setExpertise(oldWta.getExpertise());
        newWta.setOrganization(oldWta.getOrganization());
        newWta.setParentWTA(oldWta);
        oldWta.setOrganization(null);
        List<RuleTemplate> ruleTemplates = new ArrayList<>();
        if (updateDTO.getRuleTemplates().size() > 0) {
            ruleTemplates = copyRuleTemplatesWithNew(oldWta.getRuleTemplates(), updateDTO.getRuleTemplates(), "ORGANIZATION");
            newWta.setRuleTemplates(ruleTemplates);
        }
        //save(newWta);
        //
        newWta.setParentWTA(oldWta.basicDetails());
        newWta.setOrganization(null);
        newWta.getExpertise().setCountry(null);
        return newWta;
    }

    private RuleTemplateCategory getCategory(List<RuleTemplate> ruleTemplates, Long id, String ruleTemplateCategoryName) {
        RuleTemplateCategory ruleTemplateCategory = null;
        for (RuleTemplate ruletemplate : ruleTemplates) {
            if (ruletemplate.getId().equals(id)  && ruleTemplateCategoryName.equals(ruletemplate.getRuleTemplateCategory().getName())) { // if category is not changed so Assign from previous
                ruleTemplateCategory = ruletemplate.getRuleTemplateCategory();
                break;
            }

        }
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
            ruleTemplateCategory = ruleTemplateCategoryGraphRepository.findByName(ruleTemplateCategoryName, WTA);
            // if rule template is still null so this category does not exist and unit cant create a new category.
            if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {
                logger.info("category does not exist in when updating wta's rule template  :", ruleTemplateCategoryName);
                throw new DataNotFoundByIdException("category : " + ruleTemplateCategoryName + " does not exist in when updating wta's rule template  :" + id);
            }
        }

        return ruleTemplateCategory;
    }

    // TODO for country
    private RuleTemplateCategory getCategoryForCountry(List<RuleTemplate> ruleTemplates, Long id, String ruleTemplateCategoryName) {
        RuleTemplateCategory ruleTemplateCategory = null;
        for (RuleTemplate ruletemplate : ruleTemplates) {
            if (ruletemplate.getId() == id) {
                ruleTemplateCategory = ruletemplate.getRuleTemplateCategory();
                break;
            }

        }
        if (!Optional.ofNullable(ruleTemplateCategory).isPresent()) {

        }
        return ruleTemplateCategory;
    }

    private List<RuleTemplate> copyRuleTemplatesWithNew(List<RuleTemplate> ruleTemplates, List<WTARuleTemplateDTO> ruleTemplatesWithNew, String source) {
        List<RuleTemplate> wtaBaseRuleTemplates = new ArrayList<RuleTemplate>(20);

        for (WTARuleTemplateDTO ruleTemplate : ruleTemplatesWithNew) {
            RuleTemplateCategory ruleTemplateCategory = null;
            if (source.equals("ORGANIZATION")) {
                ruleTemplateCategory = getCategory(ruleTemplates, ruleTemplate.getId(), ruleTemplate.getCategory());
            } else {

            }
            switch (ruleTemplate.getTemplateType()) {
                case TEMPLATE1:
                    MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                    maximumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.isCheckAgainstTimeRules());
                    maximumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftLengthWTATemplate.setDisabled(ruleTemplate.isActive());
                    wtaBaseRuleTemplates.add(maximumShiftLengthWTATemplate);
                    break;

                case TEMPLATE2:
                    MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                    minimumShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    minimumShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    minimumShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.isCheckAgainstTimeRules());
                    minimumShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumShiftLengthWTATemplate.setDisabled(ruleTemplate.isActive());

                    wtaBaseRuleTemplates.add(minimumShiftLengthWTATemplate);
                    break;

                case TEMPLATE3:
                    MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                    maximumConsecutiveWorkingDaysWTATemplate.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingDaysWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(ruleTemplate.isCheckAgainstTimeRules());
                    maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingDaysWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingDaysWTATemplate.setDisabled(ruleTemplate.isActive());

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
                    minimumRestInConsecutiveDaysWTATemplate.setDisabled(ruleTemplate.isActive());

                    wtaBaseRuleTemplates.add(minimumRestInConsecutiveDaysWTATemplate);
                    break;

                case TEMPLATE5:
                    MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                    maximumNightShiftLengthWTATemplate.setName(ruleTemplate.getName());
                    maximumNightShiftLengthWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumNightShiftLengthWTATemplate.setTimeLimit(ruleTemplate.getTimeLimit());
                    maximumNightShiftLengthWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(ruleTemplate.isCheckAgainstTimeRules());
                    maximumNightShiftLengthWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumNightShiftLengthWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumNightShiftLengthWTATemplate.setDisabled(ruleTemplate.isActive());

                    wtaBaseRuleTemplates.add(maximumNightShiftLengthWTATemplate);
                    break;

                case TEMPLATE6:
                    MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                    minimumConsecutiveNightsWTATemplate.setName(ruleTemplate.getName());
                    minimumConsecutiveNightsWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumConsecutiveNightsWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    minimumConsecutiveNightsWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumConsecutiveNightsWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.isActive());

                    wtaBaseRuleTemplates.add(minimumConsecutiveNightsWTATemplate);
                    break;

                case TEMPLATE7:
                    MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                    maximumConsecutiveWorkingNights.setName(ruleTemplate.getName());
                    maximumConsecutiveWorkingNights.setDescription(ruleTemplate.getDescription());
                    maximumConsecutiveWorkingNights.setTemplateType(ruleTemplate.getTemplateType());
                    maximumConsecutiveWorkingNights.setNightsWorked(ruleTemplate.getNightsWorked());
                    maximumConsecutiveWorkingNights.setBalanceType(ruleTemplate.getBalanceType());
                    maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(ruleTemplate.isCheckAgainstTimeRules());
                    maximumConsecutiveWorkingNights.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumConsecutiveWorkingNights.setDisabled(ruleTemplate.isActive());

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
                    minimumRestConsecutiveNightsWTATemplate.setDisabled(ruleTemplate.isActive());

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
                    maximumNumberOfNightsWTATemplate.setDisabled(ruleTemplate.isActive());

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
                    maximumDaysOffInPeriodWTATemplate.setDisabled(ruleTemplate.isActive());
                    maximumDaysOffInPeriodWTATemplate.setDaysLimit(ruleTemplate.getDaysLimit());
                    wtaBaseRuleTemplates.add(maximumDaysOffInPeriodWTATemplate);
                    break;
                case TEMPLATE11:
                    MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate();
                    maximumAverageScheduledTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumAverageScheduledTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(ruleTemplate.isUseShiftTimes());
                    maximumAverageScheduledTimeWTATemplate.setIntervalLength(ruleTemplate.getIntervalLength());
                    maximumAverageScheduledTimeWTATemplate.setIntervalUnit(ruleTemplate.getIntervalUnit());
                    maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(ruleTemplate.getMaximumAvgTime());
                    maximumAverageScheduledTimeWTATemplate.setBalanceType(ruleTemplate.getBalanceType());
                    maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(ruleTemplate.getValidationStartDateMillis());
                    maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(ruleTemplate.isBalanceAdjustment());
                    maximumAverageScheduledTimeWTATemplate.setName(ruleTemplate.getName());
                    maximumAverageScheduledTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumAverageScheduledTimeWTATemplate.setDisabled(ruleTemplate.isActive());
                    wtaBaseRuleTemplates.add(maximumAverageScheduledTimeWTATemplate);
                    break;
                case TEMPLATE12:
                    MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                    maximumVetoPerPeriodWTATemplate.setName(ruleTemplate.getName());
                    maximumVetoPerPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    maximumVetoPerPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(ruleTemplate.getMaximumVetoPercentage());
                    maximumVetoPerPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumVetoPerPeriodWTATemplate.setDisabled(ruleTemplate.isActive());
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
                    numberOfWeekendShiftInPeriodWTATemplate.setDisabled(ruleTemplate.isActive());
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
                    careDayCheckWTATemplate.setDisabled(ruleTemplate.isActive());
                    wtaBaseRuleTemplates.add(careDayCheckWTATemplate);
                    break;
                case TEMPLATE15:
                    MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                    minimumDailyRestingTimeWTATemplate.setName(ruleTemplate.getName());
                    minimumDailyRestingTimeWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumDailyRestingTimeWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(ruleTemplate.getContinuousDayRestHours());
                    minimumDailyRestingTimeWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumDailyRestingTimeWTATemplate.setDisabled(ruleTemplate.isActive());
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
                    minimumDurationBetweenShiftWTATemplate.setDisabled(ruleTemplate.isActive());
                    wtaBaseRuleTemplates.add(minimumDurationBetweenShiftWTATemplate);
                    break;
                case TEMPLATE17:
                    MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                    minimumWeeklyRestPeriodWTATemplate.setName(ruleTemplate.getName());
                    minimumWeeklyRestPeriodWTATemplate.setTemplateType(ruleTemplate.getTemplateType());
                    minimumWeeklyRestPeriodWTATemplate.setDescription(ruleTemplate.getDescription());
                    minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(ruleTemplate.getContinuousWeekRest());
                    minimumWeeklyRestPeriodWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    minimumWeeklyRestPeriodWTATemplate.setDisabled(ruleTemplate.isActive());
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
                    shortestAndAverageDailyRestWTATemplate.setDisabled(ruleTemplate.isActive());
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
                    maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(ruleTemplate.isOnlyCompositeShifts());
                    maximumShiftsInIntervalWTATemplate.setRuleTemplateCategory(ruleTemplateCategory);
                    maximumShiftsInIntervalWTATemplate.setDisabled(ruleTemplate.isActive());
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
                    maximumSeniorDaysInYearWTATemplate.setDisabled(ruleTemplate.isActive());
                    wtaBaseRuleTemplates.add(maximumSeniorDaysInYearWTATemplate);
                    break;
                default:
                    throw new DataNotFoundByIdException("Invalid TEMPLATE");
            }

        }
        return wtaBaseRuleTemplates;
    }
}
