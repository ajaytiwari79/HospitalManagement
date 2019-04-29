package com.kairos.service.widget;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.widget.DashboardWidgetDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.persistence.model.widget.DashboardWidget;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.widget.WidgetMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import com.kairos.utils.user_context.UserContext;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * pradeep
 * 28/4/19
 */
@Service
public class WidgetService {

    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private WTARuleTemplateCalculationService wtaRuleTemplateCalculationService;
    @Inject private UserIntegrationService userIntegrationService;
    @Inject private WidgetMongoRepository widgetMongoRepository;



    public DashboardWidgetDTO getWidgetData(){
        DashboardWidgetDTO dashBoardWidgetDTO = null;
        Date startDate = asDate(LocalDate.now().minusDays(1));
        Date endDate = asDate(LocalDate.now().minusDays(1));
        Long unitId = UserContext.getUserDetails().getLastSelectedOrganizationId();
        List<ShiftDTO> shiftDTOs = shiftMongoRepository.findAllShiftBetweenDurationByUnitId(unitId,startDate,endDate);
        Object[] objects = getEmploymentIdsAndStaffIds(shiftDTOs);
        List<Long> staffIds = (List<Long>)objects[0];
        List<Long> employmentIds = (List<Long>)objects[1];
        List<NameValuePair> requestParam = new ArrayList<>();
        requestParam.add(new BasicNameValuePair("staffIds", staffIds.toString()));
        requestParam.add(new BasicNameValuePair("employmentIds", employmentIds.toString()));
        List<StaffAdditionalInfoDTO> staffAdditionalInfoDTOS = userIntegrationService.getStaffAditionalDTOS(unitId,requestParam);
        Map<Long,StaffAdditionalInfoDTO> idAndStaffMap = staffAdditionalInfoDTOS.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        Map<Long,List<ShiftDTO>> employementIdAndStaffMap = shiftDTOs.stream().collect(Collectors.groupingBy(s->s.getEmploymentId(),Collectors.toList()));
        if(isCollectionNotEmpty(staffAdditionalInfoDTOS)){
            UserAccessRoleDTO userAccessRoleDTO = staffAdditionalInfoDTOS.get(0).getUserAccessRoleDTO();
            TimeSlotWrapper nightTimeSlotWrapper = staffAdditionalInfoDTOS.get(0).getTimeSlotSets().stream().filter(timeSlotWrapper -> timeSlotWrapper.getName().equals(PartOfDay.NIGHT.getValue())).findFirst().orElseGet(null);
            TimeSlotDTO nightTimeSlot = ObjectMapperUtils.copyPropertiesByMapper(nightTimeSlotWrapper,TimeSlotDTO.class);
            employementIdAndStaffMap.forEach((aLong, shiftDTOS) -> wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS,userAccessRoleDTO));
            dashBoardWidgetDTO = new DashboardWidgetDTO(nightTimeSlot,shiftDTOs,idAndStaffMap);
        }
        return dashBoardWidgetDTO;
    }

    private Object[] getEmploymentIdsAndStaffIds(List<ShiftDTO> shiftDTOs){
        List<Long> employmentIds = new ArrayList<>();
        List<Long> staffIds = new ArrayList<>();
        shiftDTOs.forEach(shiftDTO -> {
            employmentIds.add(shiftDTO.getEmploymentId());
            staffIds.add(shiftDTO.getStaffId());
        });
        return new Object[]{staffIds,employmentIds};
    }

    public DashboardWidgetDTO saveOrUpdateWidget(DashboardWidgetDTO dashBoardWidgetDTO){
        Long userId = UserContext.getUserDetails().getId();
        DashboardWidget dashboardWidget = widgetMongoRepository.findDashboardWidgetByUserId(userId);
        if(isNull(dashboardWidget)) {
            dashboardWidget = new DashboardWidget(dashBoardWidgetDTO.getTimeTypeIds(), dashBoardWidgetDTO.getWidgetFilterTypes());
            dashboardWidget.setUserId(userId);
        }else {
            dashboardWidget.setTimeTypeIds(dashBoardWidgetDTO.getTimeTypeIds());
            dashboardWidget.setWidgetFilterTypes(dashBoardWidgetDTO.getWidgetFilterTypes());
        }
        return dashBoardWidgetDTO;
    }
}
