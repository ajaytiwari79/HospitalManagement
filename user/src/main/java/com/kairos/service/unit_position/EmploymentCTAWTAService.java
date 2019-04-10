package com.kairos.service.unit_position;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTATableSettingWrapper;
import com.kairos.dto.activity.cta.CTAWTAAndAccumulatedTimebankWrapper;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.unit_position.Employment;
import com.kairos.persistence.model.user.unit_position.query_result.PositionCtaWtaQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.EmploymentQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.unit_position.EmploymentGraphRepository;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
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

import static com.kairos.constants.ApiConstants.*;
import static com.kairos.service.unit_position.EmploymentUtility.convertEmploymentObject;

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
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private OrganizationGraphRepository organizationGraphRepository;
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

    public PositionCtaWtaQueryResult getCtaAndWtaWithExpertiseDetailByExpertiseId(Long unitId, Long expertiseId, Long staffId, LocalDate selectedDate){
        CTAWTAAndAccumulatedTimebankWrapper ctawtaAndAccumulatedTimebankWrapper = activityIntegrationService.getCTAWTAByExpertiseAndDate(expertiseId,unitId,selectedDate);
        Optional<Expertise> currentExpertise = expertiseGraphRepository.findById(expertiseId);
        SeniorityLevel appliedSeniorityLevel = employmentService.getSeniorityLevelByStaffAndExpertise(staffId, currentExpertise.get());
        SeniorityLevelQueryResult seniorityLevel = null;
        if (appliedSeniorityLevel != null) {
            seniorityLevel = seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId());
            if (selectedDate == null) {
                selectedDate = DateUtils.getCurrentLocalDate();
            }
            List<FunctionDTO> functionDTOs = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevel(currentExpertise.get().getId(), selectedDate.toString(), appliedSeniorityLevel.getId(), unitId);
            seniorityLevel.setFunctions(functionDTOs);
        }
        return new PositionCtaWtaQueryResult(ctawtaAndAccumulatedTimebankWrapper.getCta(),ctawtaAndAccumulatedTimebankWrapper.getWta(),currentExpertise.get().retrieveBasicDetails(),seniorityLevel,currentExpertise.get().getUnion());
    }

    //TODO this must be moved to activity
    public EmploymentQueryResult updateUnitPositionWTA(Long unitId, Long unitPositionId, BigInteger wtaId, WTADTO updateDTO) {
        Employment employment = employmentGraphRepository.findOne(unitPositionId);
        if (!Optional.ofNullable(employment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.InvalidUnitPositionId", unitPositionId);

        }
        if (employment.getEndDate() != null && updateDTO.getEndDate() != null && updateDTO.getEndDate().isBefore(employment.getEndDate())) {
            exceptionService.actionNotPermittedException("end_date.from.end_date");
        }
        if (employment.getEndDate() != null && updateDTO.getStartDate().isAfter(employment.getEndDate())) {
            exceptionService.actionNotPermittedException("start_date.from.end_date");
        }
        updateDTO.setId(wtaId);
        updateDTO.setUnitPositionEndDate(employment.getEndDate());
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.updateWTAOfUnitPosition(updateDTO, employment.isPublished());
        return employmentService.getBasicDetails(employment, wtaResponseDTO, employment.getEmploymentLines().get(0));
    }
    //  TODO Pradeep INCORRECT function NAME and working
    public StaffEmploymentDetails getUnitPositionCTA(Long unitPositionId, Long unitId) {
        EmploymentQueryResult unitPosition = employmentGraphRepository.getUnitPositionById(unitPositionId);
        StaffEmploymentDetails unitPositionDetails = null;
        if (Optional.ofNullable(unitPosition).isPresent()) {
            Long countryId = organizationService.getCountryIdOfOrganization(unitId);
            Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
            unitPositionDetails = convertEmploymentObject(unitPosition);
            unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
            unitPositionDetails.setCountryId(countryId);

            unitPositionDetails.setCountryId(countryId);
            unitPositionDetails.setUnitTimeZone(organization.get().getTimeZone());
        }
        return unitPositionDetails;

    }
    public CTATableSettingWrapper getAllCTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<EmploymentQueryResult> employmentQueryResults = employmentGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> upIds = employmentQueryResults.stream().map(EmploymentQueryResult::getId).collect(Collectors.toList());
        List<NameValuePair> requestParam = Collections.singletonList(new BasicNameValuePair("upIds", upIds.toString().replace("[", "").replace("]", "")));
        CTATableSettingWrapper ctaTableSettingWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_CTA, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTATableSettingWrapper>>() {
        });
        Map<Long, EmploymentQueryResult> unitPositionQueryResultMap = employmentQueryResults.stream().collect(Collectors.toMap(EmploymentQueryResult::getId, v -> v));
        ctaTableSettingWrapper.getAgreements().forEach(currentCTA -> {
            if (unitPositionQueryResultMap.containsKey(currentCTA.getEmploymentId())) {
                EmploymentQueryResult currentActiveUnitPosition = unitPositionQueryResultMap.get(currentCTA.getEmploymentId());
                currentCTA.setUnitInfo(currentActiveUnitPosition.getUnitInfo());
                currentCTA.setEmploymentId(currentActiveUnitPosition.getId());
            }
        });
        return ctaTableSettingWrapper;
    }
    public WTATableSettingWrapper getAllWTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<EmploymentQueryResult> employmentQueryResults = employmentGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> unitpositionIds = employmentQueryResults.stream().map(EmploymentQueryResult::getId).collect(Collectors.toList());

        List<NameValuePair> param = Collections.singletonList(new BasicNameValuePair("upIds", unitpositionIds.toString().replace("[", "").replace("]", "")));
        WTATableSettingWrapper wtaWithTableSettings = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_WTA, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>>() {
        });
        Map<Long, EmploymentQueryResult> unitPositionQueryResultMap = employmentQueryResults.stream().filter(u -> u.getHistory() != null && u.getHistory().equals(false)).collect(Collectors.toMap(EmploymentQueryResult::getId, v -> v));
        wtaWithTableSettings.getAgreements().forEach(currentWTA -> {
            EmploymentQueryResult employmentQueryResult = unitPositionQueryResultMap.get(currentWTA.getUnitPositionId());
            if (employmentQueryResult != null) {
                currentWTA.setUnitInfo(employmentQueryResult.getUnitInfo());
                currentWTA.setUnitPositionId(employmentQueryResult.getId());
            }
        });
        return wtaWithTableSettings;
    }





}
