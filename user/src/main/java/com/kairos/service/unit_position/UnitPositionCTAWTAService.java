package com.kairos.service.unit_position;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.cta.CTATableSettingWrapper;
import com.kairos.dto.activity.wta.basic_details.WTADTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.activity.wta.version.WTATableSettingWrapper;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.persistence.model.user.unit_position.query_result.PositionCtaWtaQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.auth.UserGraphRepository;
import com.kairos.persistence.repository.user.country.functions.FunctionGraphRepository;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorityLevelGraphRepository;
import com.kairos.persistence.repository.user.unit_position.UnitPositionGraphRepository;
import com.kairos.rest_client.WorkingTimeAgreementRestClient;
import com.kairos.rest_client.priority_group.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.organization.OrganizationService;
import org.apache.commons.collections.CollectionUtils;
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
import static com.kairos.service.unit_position.UnitPositionUtility.convertUnitPositionObject;

/**
 * CreatedBy vipulpandey on 28/11/18
 **/
@Service
@Transactional
public class UnitPositionCTAWTAService {
    @Inject
    private UnitPositionService unitPositionService;
    @Inject
    private UnitPositionGraphRepository unitPositionGraphRepository;
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

    public PositionCtaWtaQueryResult getCtaAndWtaWithExpertiseDetailByExpertiseId(Long unitId, Long expertiseId, Long staffId, LocalDate selectedDate) throws Exception {
        PositionCtaWtaQueryResult positionCtaWtaQueryResult = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_CTA_WTA_BY_EXPERTISE, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<PositionCtaWtaQueryResult>>() {
        }, expertiseId);
        Optional<Expertise> currentExpertise = expertiseGraphRepository.findById(expertiseId);
        SeniorityLevel appliedSeniorityLevel = unitPositionService.getSeniorityLevelByStaffAndExpertise(staffId, currentExpertise.get());
        positionCtaWtaQueryResult.setExpertise(currentExpertise.get().retrieveBasicDetails());
        //SeniorityLevelQueryResult seniorityLevel = (appliedSeniorityLevel != null) ? seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId()) : null;
        //positionCtaWtaQueryResult.setApplicableSeniorityLevel(seniorityLevel);
        positionCtaWtaQueryResult.setUnion(currentExpertise.get().getUnion());

        SeniorityLevelQueryResult seniorityLevel = null;
        if (appliedSeniorityLevel != null) {
            seniorityLevel = seniorityLevelGraphRepository.getSeniorityLevelById(appliedSeniorityLevel.getId());
            if (selectedDate == null) {
                selectedDate = DateUtils.getCurrentLocalDate();
            }
            List<FunctionDTO> functionDTOs = functionGraphRepository.getFunctionsByExpertiseAndSeniorityLevel(currentExpertise.get().getId(), DateUtils.getLongFromLocalDate(selectedDate), appliedSeniorityLevel.getId(), unitId);
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
        if (unitPosition.getEndDate() != null && updateDTO.getEndDate() != null && updateDTO.getEndDate().isBefore(unitPosition.getEndDate())) {
            exceptionService.actionNotPermittedException("end_date.from.end_date");
        }
        if (unitPosition.getEndDate() != null && updateDTO.getStartDate().isAfter(unitPosition.getEndDate())) {
            exceptionService.actionNotPermittedException("start_date.from.end_date");
        }
        updateDTO.setId(wtaId);
        updateDTO.setUnitPositionEndDate(unitPosition.getEndDate());
        WTAResponseDTO wtaResponseDTO = workingTimeAgreementRestClient.updateWTAOfUnitPosition(updateDTO, unitPosition.isPublished());
        UnitPositionQueryResult unitPositionQueryResult = unitPositionService.getBasicDetails(unitPosition, wtaResponseDTO, unitPosition.getUnitPositionLines().get(0));
        return unitPositionQueryResult;
    }
    //  TODO Pradeep INCORRECT function NAME and working
    public com.kairos.dto.activity.shift.StaffUnitPositionDetails getUnitPositionCTA(Long unitPositionId, Long unitId) {
        UnitPositionQueryResult unitPosition = unitPositionGraphRepository.getUnitPositionByIdAndDate(unitPositionId, DateUtils.getCurrentLocalDate().toString());
        if(unitPosition==null ){
            //For handling unitPosition applicable in future with nearest unitPositionLine from current date
            unitPosition=unitPositionGraphRepository.getUnitPositionById(unitPositionId);
        }
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails = null;
        if (Optional.ofNullable(unitPosition).isPresent()) {
            Long countryId = organizationService.getCountryIdOfOrganization(unitId);
            Optional<Organization> organization = organizationGraphRepository.findById(unitId, 0);
            unitPositionDetails = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
            unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
            unitPositionDetails.setCountryId(countryId);
            convertUnitPositionObject(unitPosition, unitPositionDetails);
            unitPositionDetails.setCountryId(countryId);
            unitPositionDetails.setUnitTimeZone(organization.get().getTimeZone());
        }
        return unitPositionDetails;

    }
    public CTATableSettingWrapper getAllCTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> upIds = unitPositionQueryResults.stream().map(UnitPositionQueryResult::getId).collect(Collectors.toList());
        List<NameValuePair> requestParam = Collections.singletonList(new BasicNameValuePair("upIds", upIds.toString().replace("[", "").replace("]", "")));
        CTATableSettingWrapper ctaTableSettingWrapper = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_CTA, requestParam, new ParameterizedTypeReference<RestTemplateResponseEnvelope<CTATableSettingWrapper>>() {
        });
        Map<Long, UnitPositionQueryResult> unitPositionQueryResultMap = unitPositionQueryResults.stream().collect(Collectors.toMap(UnitPositionQueryResult::getId, v -> v));
        ctaTableSettingWrapper.getAgreements().forEach(currentCTA -> {
            if (unitPositionQueryResultMap.containsKey(currentCTA.getUnitPositionId())) {
                UnitPositionQueryResult currentActiveUnitPosition = unitPositionQueryResultMap.get(currentCTA.getUnitPositionId());
                currentCTA.setUnitInfo(currentActiveUnitPosition.getUnitInfo());
                currentCTA.setUnitPositionId(currentActiveUnitPosition.getId());
                currentCTA.setPositionCode(ObjectMapperUtils.copyPropertiesByMapper(currentActiveUnitPosition.getPositionCode(), PositionCodeDTO.class));
            }
        });
        return ctaTableSettingWrapper;
    }
    public WTATableSettingWrapper getAllWTAOfStaff(Long unitId, Long staffId) {
        User user = userGraphRepository.getUserByStaffId(staffId);
        List<UnitPositionQueryResult> unitPositionQueryResults = unitPositionGraphRepository.getAllUnitPositionsBasicDetailsAndWTAByUser(user.getId());
        List<Long> unitpositionIds = unitPositionQueryResults.stream().map(UnitPositionQueryResult::getId).collect(Collectors.toList());

        List<NameValuePair> param = Collections.singletonList(new BasicNameValuePair("upIds", unitpositionIds.toString().replace("[", "").replace("]", "")));
        WTATableSettingWrapper wtaWithTableSettings = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, GET_VERSION_WTA, param, new ParameterizedTypeReference<RestTemplateResponseEnvelope<WTATableSettingWrapper>>() {
        });
        Map<Long, UnitPositionQueryResult> unitPositionQueryResultMap = unitPositionQueryResults.stream().filter(u -> u.getHistory() != null && u.getHistory().equals(false)).collect(Collectors.toMap(UnitPositionQueryResult::getId, v -> v));
        wtaWithTableSettings.getAgreements().forEach(currentWTA -> {
            UnitPositionQueryResult unitPositionQueryResult = unitPositionQueryResultMap.get(currentWTA.getUnitPositionId());
            if (unitPositionQueryResult != null) {
                currentWTA.setUnitInfo(unitPositionQueryResult.getUnitInfo());
                currentWTA.setUnitPositionId(unitPositionQueryResult.getId());
                currentWTA.setPositionCode(ObjectMapperUtils.copyPropertiesByMapper(unitPositionQueryResult.getPositionCode(), PositionCodeDTO.class));
            }
        });
        return wtaWithTableSettings;
    }





}
