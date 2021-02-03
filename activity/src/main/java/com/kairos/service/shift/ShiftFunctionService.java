package com.kairos.service.shift;

import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.ERROR_FUNCTION_CAN_NOT_APPLY_WITH_ABSENCE_ACTIVITY;

@Service
public class ShiftFunctionService {

    @Inject private UserIntegrationService userIntegrationService;
    @Inject private ExceptionService exceptionService;

    public Map<LocalDate, List<FunctionDTO>> addFunction(StaffAdditionalInfoDTO staffAdditionalInfoDTO, List<FunctionDTO> appliedFunctionDTOs) {
        Map<LocalDate, List<FunctionDTO>> functionDTOMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(appliedFunctionDTOs)) {
            for (FunctionDTO appliedFunctionDTO : appliedFunctionDTOs) {
                if (CollectionUtils.isNotEmpty(appliedFunctionDTO.getAppliedDates())) {
                    FunctionDTO functionDTO = new FunctionDTO(appliedFunctionDTO.getId(), appliedFunctionDTO.getName(), appliedFunctionDTO.getIcon());
                    functionDTO.setCode(appliedFunctionDTO.getCode());
                    functionDTO.setEmploymentId(staffAdditionalInfoDTO.getEmployment().getId());
                    for (LocalDate date : appliedFunctionDTO.getAppliedDates()) {
                        functionDTOMap.put(date, Arrays.asList(functionDTO));
                    }
                }
            }
        }
        return functionDTOMap;
    }

    public void updateAppliedFunctionDetail(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift, Long functionId) {
        if (isNotNull(functionId)) {
            if (activityWrapperMap.values().stream().anyMatch(k -> TimeTypeEnum.PRESENCE.equals(k.getActivity().getActivityBalanceSettings().getTimeType()))) {
                Map<LocalDate, Long> dateAndFunctionIdMap = new HashMap<>();
                dateAndFunctionIdMap.put(asLocalDate(shift.getStartDate()), functionId);
                userIntegrationService.applyFunction(shift.getUnitId(), shift.getEmploymentId(), dateAndFunctionIdMap, HttpMethod.POST, null);
            } else {
                exceptionService.actionNotPermittedException(ERROR_FUNCTION_CAN_NOT_APPLY_WITH_ABSENCE_ACTIVITY);
            }
        }
        //TODO pavan please verify it is required or not as I discussed with sakshi we can't remove function at the time of shift update
        /* else {
            BasicNameValuePair appliedDate = new BasicNameValuePair("appliedDate", asLocalDate(shift.getStartDate()).toString());
            userIntegrationService.applyFunction(shift.getUnitId(), shift.getEmploymentId(), null, HttpMethod.DELETE, Arrays.asList(appliedDate));
        }*/
    }

}
