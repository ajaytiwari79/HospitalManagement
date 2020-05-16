package com.kairos.service.employment;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTATableSettingWrapper;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.user.country.experties.ExpertiseDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.user.employment.Employment;
import com.kairos.persistence.model.user.employment.query_result.CtaWtaQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.expertise.response.SeniorityLevelQueryResult;
import com.kairos.persistence.repository.organization.OrganizationBaseRepository;
import com.kairos.persistence.repository.organization.UnitGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.employment.EmploymentGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.country.CountryService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.expertise.ExpertiseService;
import com.kairos.service.integration.ActivityIntegrationService;
import com.kairos.service.organization.OrganizationService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.constants.ApiConstants.GET_VERSION_CTA;
import static com.kairos.constants.ApiConstants.GET_VERSION_WTA;
import static com.kairos.constants.UserMessagesConstants.*;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Service
@Transactional
public class EmploymentCTAWTAService {
    @Inject
    private EmploymentService employmentService;
    @Inject
    private EmploymentGraphRepository employmentGraphRepository;
    @Inject
    private OrganizationBaseRepository organizationBaseRepository;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private UnitGraphRepository unitGraphRepository;
    @Inject
    private OrganizationService organizationService;
    @Inject
    private UserGraphRepository userGraphRepository;
    @Inject
    private SeniorityLevelGraphRepository seniorityLevelGraphRepository;
    @Inject
    private FunctionGraphRepository functionGraphRepository;
    @Inject
    private WorkingTimeAgreementRestClient workingTimeAgreementRestClient;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject private ActivityIntegrationService activityIntegrationService;
    @Inject
    private ExpertiseService expertiseService;
    @Inject private SeniorityLevelService seniorityLevelService;

    public CtaWtaQueryResult getCtaAndWtaWithExpertiseDetailByExpertiseId(Long unitId, Long expertiseId, Long staffId,LocalDate selectedDate,Long employmentId){
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = activityIntegrationService.getCTAWTAByExpertiseAndDate(expertiseId,unitId,selectedDate,employmentId);
        Expertise currentExpertise = expertiseGraphRepository.findById(expertiseId,2).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_EXPERTISE_ID_NOTFOUND,expertiseId)));
        if(selectedDate.isBefore(currentExpertise.getStartDate())){
            exceptionService.actionNotPermittedException(EMPLOYMENT_START_DATE_CANNOT_BE_BEFORE_EXPERTISE_START_DATE);
        }
        ExpertiseLine expertiseLine=currentExpertise.getCurrentlyActiveLine(selectedDate);
        SeniorityLevel appliedSeniorityLevel = seniorityLevelService.getSeniorityLevelByStaffAndExpertise(staffId, expertiseLine,currentExpertise.getId());
        SeniorityLevelQueryResult seniorityLevel = null;
        if (appliedSeniorityLevel != null) {
            seniorityLevel = seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId());
            List<FunctionDTO> functionDTOs = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevel(currentExpertise.getId(), selectedDate.toString(), appliedSeniorityLevel.getId(), unitId);
            seniorityLevel.setFunctions(functionDTOs);
        }
        ExpertiseDTO expertiseDTO=ObjectMapperUtils.copyPropertiesByMapper(currentExpertise,ExpertiseDTO.class);
        expertiseDTO.setFullTimeWeeklyMinutes(expertiseLine.getFullTimeWeeklyMinutes());
        expertiseDTO.setNumberOfWorkingDaysInWeek(expertiseLine.getNumberOfWorkingDaysInWeek());

        return new CtaWtaQueryResult(ctawtaAndAccumulatedTimebankWrapper.getCta(),ctawtaAndAccumulatedTimebankWrapper.getWta(),expertiseDTO,seniorityLevel,currentExpertise.getUnion());
    }

    //TODO this must be moved to activity
    public EmploymentQueryResult updateEmploymentWTA(Long unitId, Long employmentId, BigInteger wtaId, WTADTO updateDTO,Boolean saveAsDraft){
        Employment employment = employmentGraphRepository.findOne(employmentId);
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_INVALIDEMPLOYMENTID, employmentId);

        }
        if (employment.getEndDate() != null && updateDTO.getEndDate() != null && updateDTO.getEndDate().isBefore(employment.getEndDate())) {
            exceptionService.actionNotPermittedException(END_DATE_FROM_END_DATE);
        }
        if (employment.getEndDate() != null && updateDTO.getStartDate().isAfter(employment.getEndDate())) {
            exceptionService.actionNotPermittedException(START_DATE_FROM_END_DATE);
        }
        if(!activityIntegrationService.isStaffNightWorker(unitId,employment.getStaff().getId())) {
            List<WTABaseRuleTemplateDTO> wtaBaseRuleTemplateDTOS=updateDTO.getRuleTemplates().stream().filter(wtaBaseRuleTemplateDTO -> WTATemplateType.DAYS_OFF_AFTER_A_SERIES.equals(wtaBaseRuleTemplateDTO.getWtaTemplateType()) && !wtaBaseRuleTemplateDTO.isDisabled()).collect(Collectors.toList());
            if(isCollectionNotEmpty(wtaBaseRuleTemplateDTOS)){
                exceptionService.actionNotPermittedException(MESSAGE_STAFF_NOT_NIGHT_WORKER);
            }
        }
        updateDTO.setId(wtaId);
        updateDTO.setEmploymentEndDate(employment.getEndDate());
        WTAResponseDTO wtaResponseDTO = activityIntegrationService.updateWTAOfEmployment(unitId,updateDTO, employment.isPublished(),saveAsDraft);
        return employmentService.getBasicDetails(employment, wtaResponseDTO, employment.getEmploymentLines().get(0));
    }

    public CTATableSettingWrapper getAllCTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<EmploymentQueryResult> employmentQueryResults = employmentGraphRepository.getAllEmploymentsBasicDetailsAndWTAByUser(user.getId());
        List<Long> employmentIds = employmentQueryResults.stream().map(EmploymentQueryResult::getId).collect(Collectors.toList());
        List<NameValuePair> requestParam = Collections.singletonList(new BasicNameValuePair("employmentIds", employmentIds.toString().replace("[", "").replace("]", "")));
        CTATableSettingWrapper ctaTableSettingWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_CTA, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTATableSettingWrapper>>() {
        });
        Map<Long, EmploymentQueryResult> employmentQueryResultMap = employmentQueryResults.stream().collect(Collectors.toMap(EmploymentQueryResult::getId, v -> v));
        ctaTableSettingWrapper.getAgreements().forEach(currentCTA -> {
            if (employmentQueryResultMap.containsKey(currentCTA.getEmploymentId())) {
                EmploymentQueryResult currentActiveEmployment = employmentQueryResultMap.get(currentCTA.getEmploymentId());
                currentCTA.setUnitInfo(currentActiveEmployment.getUnitInfo());
                currentCTA.setEmploymentId(currentActiveEmployment.getId());
            }
        });
        return ctaTableSettingWrapper;
    }
    public WTATableSettingWrapper getAllWTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<EmploymentQueryResult> employmentQueryResults = employmentGraphRepository.getAllEmploymentsBasicDetailsAndWTAByUser(user.getId());
        List<Long> employmentIds = employmentQueryResults.stream().map(EmploymentQueryResult::getId).collect(Collectors.toList());

        List<NameValuePair> param = Collections.singletonList(new BasicNameValuePair("employmentIds", employmentIds.toString().replace("[", "").replace("]", "")));
        WTATableSettingWrapper wtaWithTableSettings = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_WTA, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>>() {
        });
        Map<Long, EmploymentQueryResult> employmentQueryResultMap = employmentQueryResults.stream().filter(u -> u.getHistory() != null && u.getHistory().equals(false)).collect(Collectors.toMap(EmploymentQueryResult::getId, v -> v));
        wtaWithTableSettings.getAgreements().forEach(currentWTA -> {
            EmploymentQueryResult employmentQueryResult = employmentQueryResultMap.get(currentWTA.getEmploymentId());
            if (employmentQueryResult != null) {
                currentWTA.setUnitInfo(employmentQueryResult.getUnitInfo());
                currentWTA.setEmploymentId(employmentQueryResult.getId());
            }
        });
        return wtaWithTableSettings;
    }





}
