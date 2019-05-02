package com.kairos.service.widget;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.widget.DashboardWidgetDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.widget.DashboardWidget;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.widget.WidgetMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import com.kairos.utils.user_context.UserContext;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;

/**
 * pradeep
 * 28/4/19
 */
@Service
public class WidgetService {

    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private WidgetMongoRepository widgetMongoRepository;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject private TimeTypeService timeTypeService;

    public DashboardWidgetDTO getWidgetData(Long unitId) {
        DashboardWidgetDTO dashBoardWidgetDTO = null;
        Date startDate = asDate(LocalDate.now().minusDays(1));
        Date endDate = asDate(LocalDate.now().plusDays(2));
        OrganizationDTO organizationDTO = userIntegrationService.getOrganizationWithCountryId(unitId);
        List<ShiftWithActivityDTO> shiftDTOs = shiftMongoRepository.findAllShiftBetweenDurationByUnitId(unitId, startDate, endDate);
        Object[] objects = getEmploymentIdsAndStaffIds(shiftDTOs);
        List<Long> staffIds = (List<Long>) objects[0];
        List<Long> employmentIds = (List<Long>) objects[1];
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
        requestParam.add(new BasicNameValuePair("employmentIds", employmentIds.toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(unitId, requestParam);
        if(isCollectionNotEmpty(staffAdditionalInfoDTOS)) {
            DashboardWidget dashboardWidget = widgetMongoRepository.findDashboardWidgetByUserId(UserContext.getUserDetails().getId());
            Map<Long, StaffAdditionalInfoDTO> idAndStaffMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(StaffAdditionalInfoDTO::getId, v -> v));
            Map<Long, List<ShiftWithActivityDTO>> employementIdAndStaffMap = shiftDTOs.stream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getEmploymentId, Collectors.toList()));
            Phase realTimePhase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.REALTIME.toString());
            UserAccessRoleDTO userAccessRoleDTO = staffAdditionalInfoDTOS.get(0).getUserAccessRoleDTO();
            TimeSlotWrapper nightTimeSlotWrapper = staffAdditionalInfoDTOS.get(0).getTimeSlotSets().stream().filter(timeSlotWrapper -> timeSlotWrapper.getName().equals(PartOfDay.NIGHT.getValue())).findFirst().orElseGet(null);
            TimeSlotDTO nightTimeSlot = ObjectMapperUtils.copyPropertiesByMapper(nightTimeSlotWrapper, TimeSlotDTO.class);
            employementIdAndStaffMap.forEach((aLong, shiftDTOS) -> wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS, userAccessRoleDTO));
            List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null,organizationDTO.getCountryId());
            updateTimeTypeDetails(shiftDTOs);
            dashBoardWidgetDTO = new DashboardWidgetDTO(nightTimeSlot, shiftDTOs, idAndStaffMap, realTimePhase.getRealtimeDuration(),timeTypeDTOS);
            if(isNotNull(dashboardWidget)){
                dashBoardWidgetDTO.setTimeTypeIds(dashboardWidget.getTimeTypeIds());
                dashBoardWidgetDTO.setWidgetFilterTypes(dashboardWidget.getWidgetFilterTypes());
            }
        }
        return dashBoardWidgetDTO;
    }

    private Object[] getEmploymentIdsAndStaffIds(List<ShiftWithActivityDTO> shiftDTOs) {
        List<Long> employmentIds = new ArrayList<>();
        List<Long> staffIds = new ArrayList<>();
        shiftDTOs.forEach(shiftDTO -> {
            employmentIds.add(shiftDTO.getEmploymentId());
            staffIds.add(shiftDTO.getStaffId());
        });
        return new Object[]{staffIds, employmentIds};
    }

    public DashboardWidgetDTO saveOrUpdateWidget(DashboardWidgetDTO dashBoardWidgetDTO) {
        Long userId = UserContext.getUserDetails().getId();
        DashboardWidget dashboardWidget = widgetMongoRepository.findDashboardWidgetByUserId(userId);
        if(isNull(dashboardWidget)) {
            dashboardWidget = new DashboardWidget(dashBoardWidgetDTO.getTimeTypeIds(), dashBoardWidgetDTO.getWidgetFilterTypes());
            dashboardWidget.setUserId(userId);
        } else {
            dashboardWidget.setTimeTypeIds(dashBoardWidgetDTO.getTimeTypeIds());
            dashboardWidget.setWidgetFilterTypes(dashBoardWidgetDTO.getWidgetFilterTypes());
        }
        widgetMongoRepository.save(dashboardWidget);
        return dashBoardWidgetDTO;
    }

    private void updateTimeTypeDetails(List<ShiftWithActivityDTO> shiftDTOs){
        List<TimeTypeDTO> timeTypeDTOS = timeTypeMongoRepository.findTimeTypeWithItsParent();
        Map<BigInteger,TimeTypeDTO> timeTypeDTOMap = timeTypeDTOS.stream().collect(Collectors.toMap(TimeTypeDTO::getId,v->v));
        for (ShiftWithActivityDTO shiftDTO : shiftDTOs) {
            ShiftActivityDTO shiftActivityDTO = shiftDTO.getActivities().get(0);
            BigInteger timeTypeId = shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId();
            TimeTypeDTO timeType = timeTypeDTOMap.get(timeTypeId);
            shiftActivityDTO.setSecondLevelType(timeType.getSecondLevelType());
            if(timeType.getParent().size()==2){
                TimeTypeDTO thirdLevelTimeType = timeType.getParent().stream().filter(timeTypeDTO -> timeTypeDTO.getId().equals(timeType.getUpperLevelTimeTypeId())).findFirst().get();
                shiftActivityDTO.setThirdLevelTimeTypeId(thirdLevelTimeType.getId());
                TimeTypeDTO secondLevelTimeType = timeType.getParent().stream().filter(timeTypeDTO -> timeTypeDTO.getId().equals(thirdLevelTimeType.getUpperLevelTimeTypeId())).findFirst().get();
                shiftActivityDTO.setSecondLevelTimeTypeId(secondLevelTimeType.getId());
                shiftActivityDTO.setFourthLevelTimeTypeId(timeTypeId);
            }else if(timeType.getParent().size()==1){
                shiftActivityDTO.setThirdLevelTimeTypeId(timeTypeId);
                shiftActivityDTO.setSecondLevelTimeTypeId(timeType.getParent().get(0).getId());
            }else {
                shiftActivityDTO.setSecondLevelTimeTypeId(timeTypeId);
            }
        }

    }
}
