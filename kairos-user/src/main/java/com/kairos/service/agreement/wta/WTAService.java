package com.kairos.service.agreement.wta;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.agreement.wta.WTAWithCountryAndOrganizationTypeDTO;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreementQueryResult;
import com.kairos.persistence.model.user.agreement.wta.templates.*;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseDTO;
import com.kairos.persistence.model.user.expertise.ExpertiseIdListDTO;
import com.kairos.persistence.repository.organization.OrganizationTypeGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.RuleTemplateCategoryGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WTABaseRuleTemplateGraphRepository;
import com.kairos.persistence.repository.user.agreement.wta.WorkingTimeAgreementGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.region.RegionGraphRepository;
import com.kairos.response.dto.web.WtaDTO;
import com.kairos.service.UserBaseService;
import com.kairos.service.expertise.ExpertiseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstants.*;


/**
 * Created by pawanmandhan on 2/8/17.
 */


@Transactional
@Service
public class WTAService extends UserBaseService {
    @Inject
    private WorkingTimeAgreementGraphRepository wtaRepository;
    @Inject
    private OrganizationTypeGraphRepository organizationTypeRepository;
    @Inject
    private CountryGraphRepository countryRepository;
    @Inject
    private ExpertiseGraphRepository expertiseRepository;
    @Inject
    private RuleTemplateCategoryGraphRepository ruleTemplateCategoryRepository;
    @Inject
    private RegionGraphRepository regionRepository;
    @Inject
    private WTABaseRuleTemplateGraphRepository wtaBaseRuleTemplateGraphRepository;
    @Inject
    ExpertiseService expertiseService;
    @Inject
    private WtaRuleTemplateService wtaRuleTemplateService;
    @Inject
    private RuleTemplateCategoryService ruleTemplateCategoryService;

    public HashMap createWta(long countryId, WtaDTO wtaDTO) {
        Country country = countryRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid Country id " + countryId);
        }
        checkUniquenessOfData(countryId, wtaDTO);
        WorkingTimeAgreement wta = prepareWta(countryId, wtaDTO);
        wta.setCountry(country);
        save(wta);
        HashMap hs = new HashMap();
        hs.put("id", wta.getId());
        hs.put("name", wta.getName());
        return hs;
    }

    private void checkUniquenessOfData(long countryId, WtaDTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfData(wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }

    private WorkingTimeAgreement prepareWta(long countryId, WtaDTO wtaDTO) {

        WorkingTimeAgreement wta = new WorkingTimeAgreement();

        wta.setDescription(wtaDTO.getDescription());
        wta.setName(wtaDTO.getName());

        Expertise expertise = expertiseRepository.findOne(((long) wtaDTO.getExpertiseId()));
        if (!Optional.ofNullable(expertise).isPresent()) {
            throw new DataNotFoundByIdException("Invalid expertiseId " + wtaDTO.getExpertiseId());
        }
        wta.setExpertise(expertise);
        OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
        if (!Optional.ofNullable(organizationType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        wta.setOrganizationType(organizationType);

        OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());
        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }
        wta.setOrganizationSubType(organizationSubType);
        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        // wtaBaseRuleTemplates = setRuleTemplates(countryId, wta, wtaDTO);
        copyRuleTemplates(countryId, wtaDTO, wtaBaseRuleTemplates);

        wta.setRuleTemplates(wtaBaseRuleTemplates);


        if (wtaDTO.getStartDateMillis() == 0) {
            wta.setStartDateMillis(new Date().getTime());
        } else wta.setStartDateMillis(wtaDTO.getStartDateMillis());
        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (wtaDTO.getStartDateMillis() > wtaDTO.getEndDateMillis()) {
                throw new InvalidRequestException("End Date must not be greater than start date");

            }
            wta.setEndDateMillis(wtaDTO.getEndDateMillis());
        }

        return wta;
    }

    private void copyRuleTemplates(long countryId, WtaDTO wtaDTO, List<WTABaseRuleTemplate> wtaBaseRuleTemplates) {
        if (wtaDTO.getRuleTemplates() != null || wtaDTO.getRuleTemplates().isEmpty()) {
            for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
                WTARuleTemplateQueryResponse wtaBaseRuleTemplate = wtaRuleTemplateService.getRuleTemplateById(ruleTemplateId);
                WTABaseRuleTemplate wtaBaseRuleTemplateCopy = new WTABaseRuleTemplate();
                if (!Optional.ofNullable(wtaBaseRuleTemplate).isPresent()) {
                    throw new DataNotFoundByIdException("Invalid RuleTemplate Id " + ruleTemplateId);

                }

                wtaBaseRuleTemplateCopy = createCopyOfPrevious(wtaBaseRuleTemplate);
                ruleTemplateCategoryService.setRuleTemplatecategoryWithRuleTemplate(wtaBaseRuleTemplate.getRuleTemplateCategory().getId(), wtaBaseRuleTemplateCopy.getId());
                wtaBaseRuleTemplates.add(wtaBaseRuleTemplateCopy);
            }
        }
    }


    private WTABaseRuleTemplate getTemplateByType(Long countryId, String templateType) {
        return countryRepository.getTemplateByType(countryId, templateType);
    }

    private void checkUniquenessOfDataExcludingCurrent(long countryId, long wtaId, WtaDTO wtaDTO) {
        WorkingTimeAgreement wta =
                wtaRepository.checkUniquenessOfDataExcludingCurrent(wtaDTO.getOrganizationSubType(), wtaDTO.getOrganizationType(), wtaDTO.getExpertiseId(), countryId, wtaId);
        if (Optional.ofNullable(wta).isPresent()) {
            throw new InvalidRequestException("WTA combination of exp,org,level,region already exist.");

        }
        return;
    }

    public boolean updateWta(long countryId, long wtaId, WtaDTO wta) {


        WorkingTimeAgreement oldWta = wtaRepository.findOne(wtaId);

        if (!Optional.ofNullable(oldWta).isPresent()) {
            return false;
        }
        checkUniquenessOfDataExcludingCurrent(countryId, wtaId, wta);

        prepareWta(countryId, oldWta, wta);

        save(oldWta);

        return true;
    }

    private void prepareWta(long countryId, WorkingTimeAgreement oldWta, WtaDTO wtaDTO) {
        oldWta.setName(wtaDTO.getName());
        oldWta.setDescription(wtaDTO.getDescription());

        if (oldWta.getExpertise().getId() != wtaDTO.getExpertiseId()) {
            Expertise expertise = expertiseRepository.findOne(wtaDTO.getExpertiseId());
            if (!Optional.ofNullable(expertise).isPresent()) {
                throw new DataNotFoundByIdException("Expertize not found by Id" + wtaDTO.getExpertiseId());
            }
            oldWta.setExpertise(expertise);
        }


        OrganizationType organizationType = organizationTypeRepository.findOne(wtaDTO.getOrganizationType());
        if (!Optional.ofNullable(organizationType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization type " + wtaDTO.getOrganizationType());
        }
        oldWta.setOrganizationType(organizationType);

        OrganizationType organizationSubType = organizationTypeRepository.findOne(wtaDTO.getOrganizationSubType());

        if (!Optional.ofNullable(organizationSubType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organization sub type " + wtaDTO.getOrganizationSubType());
        }
        oldWta.setOrganizationSubType(organizationSubType);

        List<WTABaseRuleTemplate> wtaBaseRuleTemplates = new ArrayList<WTABaseRuleTemplate>();

        // wtaBaseRuleTemplates = setRuleTemplates(countryId, wta, wtaDTO);
        if (wtaDTO.getRuleTemplates() != null || wtaDTO.getRuleTemplates().isEmpty()) {
            for (long ruleTemplateId : wtaDTO.getRuleTemplates()) {
                WTABaseRuleTemplate wtaBaseRuleTemplate = wtaBaseRuleTemplateGraphRepository.findOne(ruleTemplateId);
                if (!Optional.ofNullable(wtaBaseRuleTemplate).isPresent()) {
                    throw new DataNotFoundByIdException("Invalid RuleTemplate Id " + ruleTemplateId);

                }
                wtaBaseRuleTemplates.add(wtaBaseRuleTemplate);
            }
        }
        oldWta.setRuleTemplates(wtaBaseRuleTemplates);
        if (wtaDTO.getStartDateMillis() == 0) {
            oldWta.setStartDateMillis(new Date().getTime());
        } else oldWta.setStartDateMillis(wtaDTO.getStartDateMillis());

        if (wtaDTO.getEndDateMillis() != null && wtaDTO.getEndDateMillis() > 0) {
            if (wtaDTO.getStartDateMillis() > wtaDTO.getEndDateMillis()) {
                throw new InvalidRequestException("End Date must not be less than start date");
            }
            oldWta.setEndDateMillis(wtaDTO.getEndDateMillis());
        }
    }

    public WorkingTimeAgreement getWta(long wtaId) {
        return wtaRepository.getWta(wtaId);
    }

    public boolean removeWta(long wtaId) {
        WorkingTimeAgreement wta = wtaRepository.getWta(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("Invalid wtaId  " + wtaId);
        }
        wta.setEnabled(false);
        save(wta);

        return true;
    }

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationId(long organizationId) {
        return wtaRepository.getAllWTAByOrganizationId(organizationId);
    }

    public List<WTAWithCountryAndOrganizationTypeDTO> getAllWTAByCountryId(long countryId) {
        return wtaRepository.getAllWTAByCountryId(countryId);
    }

    public List<WorkingTimeAgreementQueryResult> getAllWTAByOrganizationSubType(long organizationSubTypeId) {
        return wtaRepository.getAllWTAByOrganizationSubType(organizationSubTypeId);
    }

    public List<Object> getAllWTAWithOrganization(long countryId) {
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithOrganization(countryId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<Object> getAllWTAWithWTAId(long countryId, long wtaId) {
        List<Map<String, Object>> map = wtaRepository.getAllWTAWithWTAId(countryId, wtaId);
        List<Object> objectList = new ArrayList<>();
        for (Map<String, Object> result : map) {
            objectList.add(result.get("result"));
        }
        return objectList;
    }

    public List<ExpertiseDTO> getAllAvailableExpertise(Long organizationSubTypeId, Long countryId) {
        ExpertiseIdListDTO map = wtaRepository.getAvailableAndFreeExpertise(countryId, organizationSubTypeId);
        List<Long> linkedExpertiseIds = map.getLinkedExpertise();
        List<Long> allExpertiseIds = map.getAllExpertiseIds();
        allExpertiseIds.removeAll(linkedExpertiseIds);
        List<ExpertiseDTO> expertiseDTOS = new ArrayList<ExpertiseDTO>();
        expertiseDTOS = expertiseService.getAllFreeExpertise(allExpertiseIds);
        return expertiseDTOS;

    }

    public boolean setWtaWithOrganizationType(long wtaId, long organizationSubTypeId, boolean checked) {
        OrganizationType orgType = organizationTypeRepository.findOne(organizationSubTypeId);
        if (!Optional.ofNullable(orgType).isPresent()) {
            throw new DataNotFoundByIdException("Invalid organisation Sub type Id " + organizationSubTypeId);
        }
        WorkingTimeAgreement wta = wtaRepository.findOne(wtaId);
        if (!Optional.ofNullable(wta).isPresent()) {
            throw new DataNotFoundByIdException("wta not found " + wtaId);
        }
        if (checked) {
            WorkingTimeAgreement newWtaObject = new WorkingTimeAgreement();
            WorkingTimeAgreement.copyProperties(wta, newWtaObject);
            newWtaObject.setId(null);
            newWtaObject.setOrganizationSubType(orgType);
            save(newWtaObject);
        } else {
            wta.setEnabled(false);

            save(wta);
        }
        return checked;

    }

    private WTABaseRuleTemplate createCopyOfPrevious(WTARuleTemplateQueryResponse wtaBaseRuleTemplate) {
        WTABaseRuleTemplate wtaBaseRuleTemplateCopy = null;
        switch (wtaBaseRuleTemplate.getTemplateType()) {
            case TEMPLATE1:
                MaximumShiftLengthWTATemplate maximumShiftLengthWTATemplate = new MaximumShiftLengthWTATemplate();
                maximumShiftLengthWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumShiftLengthWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumShiftLengthWTATemplate.setTimeLimit(wtaBaseRuleTemplate.getTimeLimit());
                maximumShiftLengthWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumShiftLengthWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(maximumShiftLengthWTATemplate);

                break;

            case TEMPLATE2:
                MinimumShiftLengthWTATemplate minimumShiftLengthWTATemplate = new MinimumShiftLengthWTATemplate();
                minimumShiftLengthWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumShiftLengthWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumShiftLengthWTATemplate.setTimeLimit(wtaBaseRuleTemplate.getTimeLimit());
                minimumShiftLengthWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                minimumShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                minimumShiftLengthWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(minimumShiftLengthWTATemplate);
                break;

            case TEMPLATE3:
                MaximumConsecutiveWorkingDaysWTATemplate maximumConsecutiveWorkingDaysWTATemplate = new MaximumConsecutiveWorkingDaysWTATemplate();
                maximumConsecutiveWorkingDaysWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumConsecutiveWorkingDaysWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumConsecutiveWorkingDaysWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                maximumConsecutiveWorkingDaysWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumConsecutiveWorkingDaysWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumConsecutiveWorkingDaysWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(maximumConsecutiveWorkingDaysWTATemplate);
                break;

            case TEMPLATE4:
                MinimumRestInConsecutiveDaysWTATemplate minimumRestInConsecutiveDaysWTATemplate = new MinimumRestInConsecutiveDaysWTATemplate();
                minimumRestInConsecutiveDaysWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumRestInConsecutiveDaysWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumRestInConsecutiveDaysWTATemplate.setMinimumRest(wtaBaseRuleTemplate.getMinimumRest());
                minimumRestInConsecutiveDaysWTATemplate.setDaysWorked(wtaBaseRuleTemplate.getDaysWorked());
                minimumRestInConsecutiveDaysWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(minimumRestInConsecutiveDaysWTATemplate);
                break;

            case TEMPLATE5:
                MaximumNightShiftLengthWTATemplate maximumNightShiftLengthWTATemplate = new MaximumNightShiftLengthWTATemplate();
                maximumNightShiftLengthWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumNightShiftLengthWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumNightShiftLengthWTATemplate.setTimeLimit(wtaBaseRuleTemplate.getTimeLimit());
                maximumNightShiftLengthWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumNightShiftLengthWTATemplate.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                maximumNightShiftLengthWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(maximumNightShiftLengthWTATemplate);
                break;

            case TEMPLATE6:
                MinimumConsecutiveNightsWTATemplate minimumConsecutiveNightsWTATemplate = new MinimumConsecutiveNightsWTATemplate();
                minimumConsecutiveNightsWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumConsecutiveNightsWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumConsecutiveNightsWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                minimumConsecutiveNightsWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                wtaBaseRuleTemplateCopy = save(minimumConsecutiveNightsWTATemplate);
                break;

            case TEMPLATE7:
                MaximumConsecutiveWorkingNightsWTATemplate maximumConsecutiveWorkingNights = new MaximumConsecutiveWorkingNightsWTATemplate();
                maximumConsecutiveWorkingNights.setName(wtaBaseRuleTemplate.getName());
                maximumConsecutiveWorkingNights.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumConsecutiveWorkingNights.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumConsecutiveWorkingNights.setNightsWorked(wtaBaseRuleTemplate.getNightsWorked());
                maximumConsecutiveWorkingNights.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumConsecutiveWorkingNights.setCheckAgainstTimeRules(wtaBaseRuleTemplate.getCheckAgainstTimeRules());
                wtaBaseRuleTemplateCopy = save(maximumConsecutiveWorkingNights);
                break;
            case TEMPLATE8:
                MinimumRestConsecutiveNightsWTATemplate minimumRestConsecutiveNightsWTATemplate = new MinimumRestConsecutiveNightsWTATemplate();
                minimumRestConsecutiveNightsWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumRestConsecutiveNightsWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumRestConsecutiveNightsWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumRestConsecutiveNightsWTATemplate.setNightsWorked(wtaBaseRuleTemplate.getNightsWorked());
                minimumRestConsecutiveNightsWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                minimumRestConsecutiveNightsWTATemplate.setMinimumRest(wtaBaseRuleTemplate.getMinimumRest());
                wtaBaseRuleTemplateCopy = save(minimumRestConsecutiveNightsWTATemplate);
                break;
            case TEMPLATE9:
                MaximumNumberOfNightsWTATemplate maximumNumberOfNightsWTATemplate = new MaximumNumberOfNightsWTATemplate();
                maximumNumberOfNightsWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumNumberOfNightsWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumNumberOfNightsWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumNumberOfNightsWTATemplate.setNightsWorked(wtaBaseRuleTemplate.getNightsWorked());
                maximumNumberOfNightsWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumNumberOfNightsWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumNumberOfNightsWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumNumberOfNightsWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                wtaBaseRuleTemplateCopy = save(maximumNumberOfNightsWTATemplate);
                break;
            case TEMPLATE10:
                MaximumDaysOffInPeriodWTATemplate maximumDaysOffInPeriodWTATemplate = new MaximumDaysOffInPeriodWTATemplate();
                maximumDaysOffInPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumDaysOffInPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumDaysOffInPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumDaysOffInPeriodWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumDaysOffInPeriodWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumDaysOffInPeriodWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumDaysOffInPeriodWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumDaysOffInPeriodWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                wtaBaseRuleTemplateCopy = save(maximumDaysOffInPeriodWTATemplate);
                break;
            case TEMPLATE11:
                MaximumAverageScheduledTimeWTATemplate maximumAverageScheduledTimeWTATemplate = new MaximumAverageScheduledTimeWTATemplate();
                maximumAverageScheduledTimeWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumAverageScheduledTimeWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumAverageScheduledTimeWTATemplate.setUseShiftTimes(wtaBaseRuleTemplate.getUseShiftTimes());
                maximumAverageScheduledTimeWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumAverageScheduledTimeWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumAverageScheduledTimeWTATemplate.setMaximumAvgTime(wtaBaseRuleTemplate.getMaximumAvgTime());
                maximumAverageScheduledTimeWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumAverageScheduledTimeWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumAverageScheduledTimeWTATemplate.setBalanceAdjustment(wtaBaseRuleTemplate.getBalanceAdjustment());
                maximumAverageScheduledTimeWTATemplate.setName(wtaBaseRuleTemplate.getName());
                wtaBaseRuleTemplateCopy = save(maximumAverageScheduledTimeWTATemplate);
                break;
            case TEMPLATE12:
                MaximumVetoPerPeriodWTATemplate maximumVetoPerPeriodWTATemplate = new MaximumVetoPerPeriodWTATemplate();
                maximumVetoPerPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumVetoPerPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumVetoPerPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumVetoPerPeriodWTATemplate.setMaximumVetoPercentage(wtaBaseRuleTemplate.getMaximumVetoPercentage());
                wtaBaseRuleTemplateCopy = save(maximumVetoPerPeriodWTATemplate);
                break;
            case TEMPLATE13:
                NumberOfWeekendShiftInPeriodWTATemplate numberOfWeekendShiftInPeriodWTATemplate = new NumberOfWeekendShiftInPeriodWTATemplate();
                numberOfWeekendShiftInPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                numberOfWeekendShiftInPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                numberOfWeekendShiftInPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberShiftsPerPeriod(wtaBaseRuleTemplate.getNumberShiftsPerPeriod());
                numberOfWeekendShiftInPeriodWTATemplate.setNumberOfWeeks(wtaBaseRuleTemplate.getNumberOfWeeks());
                numberOfWeekendShiftInPeriodWTATemplate.setFromDayOfWeek(wtaBaseRuleTemplate.getFromDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setFromTime(wtaBaseRuleTemplate.getFromTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToTime(wtaBaseRuleTemplate.getToTime());
                numberOfWeekendShiftInPeriodWTATemplate.setToDayOfWeek(wtaBaseRuleTemplate.getToDayOfWeek());
                numberOfWeekendShiftInPeriodWTATemplate.setProportional(wtaBaseRuleTemplate.getProportional());
                wtaBaseRuleTemplateCopy = save(numberOfWeekendShiftInPeriodWTATemplate);
                break;
            case TEMPLATE14:
                CareDayCheckWTATemplate careDayCheckWTATemplate = new CareDayCheckWTATemplate();
                careDayCheckWTATemplate.setName(wtaBaseRuleTemplate.getName());
                careDayCheckWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                careDayCheckWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                careDayCheckWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                careDayCheckWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                careDayCheckWTATemplate.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                careDayCheckWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                wtaBaseRuleTemplateCopy = save(careDayCheckWTATemplate);
                break;
            case TEMPLATE15:
                MinimumDailyRestingTimeWTATemplate minimumDailyRestingTimeWTATemplate = new MinimumDailyRestingTimeWTATemplate();
                minimumDailyRestingTimeWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumDailyRestingTimeWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumDailyRestingTimeWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumDailyRestingTimeWTATemplate.setContinuousDayRestHours(wtaBaseRuleTemplate.getContinuousDayRestHours());
                wtaBaseRuleTemplateCopy = save(minimumDailyRestingTimeWTATemplate);
                break;
            case TEMPLATE16:
                MinimumDurationBetweenShiftWTATemplate minimumDurationBetweenShiftWTATemplate = new MinimumDurationBetweenShiftWTATemplate();
                minimumDurationBetweenShiftWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumDurationBetweenShiftWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumDurationBetweenShiftWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumDurationBetweenShiftWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                minimumDurationBetweenShiftWTATemplate.setMinimumDurationBetweenShifts(wtaBaseRuleTemplate.getMinimumDurationBetweenShifts());
                wtaBaseRuleTemplateCopy = save(minimumDurationBetweenShiftWTATemplate);
                break;
            case TEMPLATE17:
                MinimumWeeklyRestPeriodWTATemplate minimumWeeklyRestPeriodWTATemplate = new MinimumWeeklyRestPeriodWTATemplate();
                minimumWeeklyRestPeriodWTATemplate.setName(wtaBaseRuleTemplate.getName());
                minimumWeeklyRestPeriodWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                minimumWeeklyRestPeriodWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                minimumWeeklyRestPeriodWTATemplate.setContinuousWeekRest(wtaBaseRuleTemplate.getContinuousWeekRest());
                wtaBaseRuleTemplateCopy = save(minimumWeeklyRestPeriodWTATemplate);
                break;
            case TEMPLATE18:
                ShortestAndAverageDailyRestWTATemplate shortestAndAverageDailyRestWTATemplate = new ShortestAndAverageDailyRestWTATemplate();
                shortestAndAverageDailyRestWTATemplate.setName(wtaBaseRuleTemplate.getName());
                shortestAndAverageDailyRestWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                shortestAndAverageDailyRestWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                shortestAndAverageDailyRestWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                shortestAndAverageDailyRestWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                shortestAndAverageDailyRestWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                shortestAndAverageDailyRestWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                shortestAndAverageDailyRestWTATemplate.setContinuousDayRestHours(wtaBaseRuleTemplate.getContinuousDayRestHours());
                shortestAndAverageDailyRestWTATemplate.setAverageRest(wtaBaseRuleTemplate.getAverageRest());
                shortestAndAverageDailyRestWTATemplate.setShiftAffiliation(wtaBaseRuleTemplate.getShiftAffiliation());
                wtaBaseRuleTemplateCopy = save(shortestAndAverageDailyRestWTATemplate);
                break;
            case TEMPLATE19:
                MaximumShiftsInIntervalWTATemplate maximumShiftsInIntervalWTATemplate = new MaximumShiftsInIntervalWTATemplate();
                maximumShiftsInIntervalWTATemplate.setName(wtaBaseRuleTemplate.getName());
                maximumShiftsInIntervalWTATemplate.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                maximumShiftsInIntervalWTATemplate.setDescription(wtaBaseRuleTemplate.getDescription());
                maximumShiftsInIntervalWTATemplate.setBalanceType(wtaBaseRuleTemplate.getBalanceType());
                maximumShiftsInIntervalWTATemplate.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                maximumShiftsInIntervalWTATemplate.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                maximumShiftsInIntervalWTATemplate.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                maximumShiftsInIntervalWTATemplate.setShiftsLimit(wtaBaseRuleTemplate.getShiftsLimit());
                maximumShiftsInIntervalWTATemplate.setOnlyCompositeShifts(wtaBaseRuleTemplate.getOnlyCompositeShifts());
                wtaBaseRuleTemplateCopy = save(maximumShiftsInIntervalWTATemplate);
                break;
            case TEMPLATE20:
                MaximumSeniorDaysInYearWTATemplate template20 = new MaximumSeniorDaysInYearWTATemplate();
                template20.setName(wtaBaseRuleTemplate.getName());
                template20.setTemplateType(wtaBaseRuleTemplate.getTemplateType());
                template20.setDescription(wtaBaseRuleTemplate.getDescription());
                template20.setIntervalLength(wtaBaseRuleTemplate.getIntervalLength());
                template20.setIntervalUnit(wtaBaseRuleTemplate.getIntervalUnit());
                template20.setValidationStartDateMillis(wtaBaseRuleTemplate.getValidationStartDateMillis());
                template20.setDaysLimit(wtaBaseRuleTemplate.getDaysLimit());
                template20.setActivityCode(wtaBaseRuleTemplate.getActivityCode());

                wtaBaseRuleTemplateCopy = save(template20);
                break;
            default:
                throw new DataNotFoundByIdException("Invalid TEMPLATE");


        }
        return wtaBaseRuleTemplateCopy;
    }
}