package com.kairos.service.widget;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.widget.DashboardWidgetDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.dto.user_context.UserContext;
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
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.night_worker.NightWorkerService;
import com.kairos.service.wta.WTARuleTemplateCalculationService;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.REALTIME_DURATION_NOT_CONFIGURED;
import static com.kairos.enums.widget.WidgetFilterType.*;
import static java.util.Comparator.comparing;

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
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private NightWorkerService nightWorkerService;

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
        Phase realTimePhase = phaseMongoRepository.findByUnitIdAndPhaseEnum(unitId, PhaseDefaultName.REALTIME.toString());
        List<TimeTypeDTO> timeTypeDTOS = timeTypeService.getAllTimeType(null, organizationDTO.getCountryId());
        if (isNull(realTimePhase) || isNull(realTimePhase.getRealtimeDuration())) {
            exceptionService.dataNotFoundException(REALTIME_DURATION_NOT_CONFIGURED);
        }
        dashBoardWidgetDTO = new DashboardWidgetDTO(null, shiftDTOs, new HashMap<>(), realTimePhase.getRealtimeDuration(), timeTypeDTOS);
        if (isCollectionNotEmpty(staffAdditionalInfoDTOS)) {
            Map<Long, StaffAdditionalInfoDTO> idAndStaffMap = staffAdditionalInfoDTOS.stream().filter(distinctByKey(staffAdditionalInfoDTO -> staffAdditionalInfoDTO.getId())).collect(Collectors.toMap(StaffAdditionalInfoDTO::getId, v -> v));
            setStaffNightWorker(idAndStaffMap);
            Map<Long, List<ShiftWithActivityDTO>> employementIdAndStaffMap = shiftDTOs.stream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getEmploymentId, Collectors.toList()));
            TimeSlotWrapper nightTimeSlotWrapper = staffAdditionalInfoDTOS.get(0).getTimeSlotSets().stream().filter(timeSlotWrapper -> timeSlotWrapper.getName().equals(PartOfDay.NIGHT.getValue())).findFirst().orElseGet(null);
            TimeSlotDTO nightTimeSlot = ObjectMapperUtils.copyPropertiesByMapper(nightTimeSlotWrapper, TimeSlotDTO.class);
            employementIdAndStaffMap.forEach((aLong, shiftDTOS) -> wtaRuleTemplateCalculationService.updateRestingTimeInShifts(shiftDTOS));
            updateTimeTypeDetails(shiftDTOs);
            updateRestingHoursInShift(shiftDTOs);
            dashBoardWidgetDTO.setNightTimeSlot(nightTimeSlot);
            dashBoardWidgetDTO.setStaffIdAndstaffInfoMap(idAndStaffMap);
        }
        DashboardWidget dashboardWidget = widgetMongoRepository.findDashboardWidgetByUserId(UserContext.getUserDetails().getId());
        if (isNull(dashboardWidget)) {
            dashboardWidget = new DashboardWidget(new HashSet<>(), newHashSet(CURRENTLY_WORKING, UPCOMING_SHIFTS, ON_LEAVE, RESTING, SLEEPING));
            dashboardWidget.setUserId(UserContext.getUserDetails().getId());
            widgetMongoRepository.save(dashboardWidget);
        }
        dashBoardWidgetDTO.setTimeTypeIds(dashboardWidget.getTimeTypeIds());
        dashBoardWidgetDTO.setWidgetFilterTypes(dashboardWidget.getWidgetFilterTypes());
        return dashBoardWidgetDTO;
    }

    private void setStaffNightWorker(Map<Long, StaffAdditionalInfoDTO> idAndStaffMap) {
        List<Long> staffIds = idAndStaffMap.keySet().stream().collect(Collectors.toList());
        Map<Long, Boolean> staffIdAndNightWorkerMap = nightWorkerService.getStaffIdAndNightWorkerMap(staffIds);
        for (Long staffId : staffIds) {
            idAndStaffMap.get(staffId).setNightWorker(staffIdAndNightWorkerMap.get(staffId));
        }
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
        if (isNull(dashboardWidget)) {
            dashboardWidget = new DashboardWidget(dashBoardWidgetDTO.getTimeTypeIds(), dashBoardWidgetDTO.getWidgetFilterTypes());
            dashboardWidget.setUserId(userId);
        } else {
            dashboardWidget.setTimeTypeIds(dashBoardWidgetDTO.getTimeTypeIds());
            dashboardWidget.setWidgetFilterTypes(dashBoardWidgetDTO.getWidgetFilterTypes());
        }
        widgetMongoRepository.save(dashboardWidget);
        return dashBoardWidgetDTO;
    }

    private void updateTimeTypeDetails(List<ShiftWithActivityDTO> shiftDTOs) {
        List<TimeTypeDTO> timeTypeDTOS = timeTypeMongoRepository.findTimeTypeWithItsParent();
        Map<BigInteger, TimeTypeDTO> timeTypeDTOMap = timeTypeDTOS.stream().collect(Collectors.toMap(TimeTypeDTO::getId, v -> v));
        for (ShiftWithActivityDTO shiftDTO : shiftDTOs) {
            ShiftActivityDTO shiftActivityDTO = shiftDTO.getActivities().get(0);
            BigInteger timeTypeId = shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeTypeId();
            TimeTypeDTO timeType = timeTypeDTOMap.get(timeTypeId);
            shiftActivityDTO.setSecondLevelType(timeType.getSecondLevelType());
            if (timeType.getParent().size() == 2) {
                TimeTypeDTO thirdLevelTimeType = timeType.getParent().stream().filter(timeTypeDTO -> timeTypeDTO.getId().equals(timeType.getUpperLevelTimeTypeId())).findFirst().orElseThrow(()-> new DataNotFoundByIdException("Third Level Time Type Not Found By Id :"+timeType.getUpperLevelTimeTypeId()));
                shiftActivityDTO.setThirdLevelTimeTypeId(thirdLevelTimeType.getId());
                TimeTypeDTO secondLevelTimeType = timeType.getParent().stream().filter(timeTypeDTO -> timeTypeDTO.getId().equals(thirdLevelTimeType.getUpperLevelTimeTypeId())).findFirst().orElseThrow(()-> new DataNotFoundByIdException("Second Level Time Type Not Found By Id :"+thirdLevelTimeType.getUpperLevelTimeTypeId()));
                shiftActivityDTO.setSecondLevelTimeTypeId(secondLevelTimeType.getId());
                shiftActivityDTO.setFourthLevelTimeTypeId(timeTypeId);
            } else if (timeType.getParent().size() == 1) {
                shiftActivityDTO.setThirdLevelTimeTypeId(timeTypeId);
                shiftActivityDTO.setSecondLevelTimeTypeId(timeType.getParent().get(0).getId());
            } else {
                shiftActivityDTO.setSecondLevelTimeTypeId(timeTypeId);
            }
        }

    }

    private void updateRestingHoursInShift(List<ShiftWithActivityDTO> shiftDTOs) {
        Map<Long, List<ShiftWithActivityDTO>> staffIdAndShiftMap = shiftDTOs.stream().collect(Collectors.groupingBy(ShiftWithActivityDTO::getStaffId, Collectors.toList()));
        for (Long staffId : staffIdAndShiftMap.keySet()) {
            List<ShiftWithActivityDTO> shifts = staffIdAndShiftMap.get(staffId);
            shifts.sort(comparing(ShiftDTO::getStartDate));
            for (int i = 1; i < shifts.size(); i++) {
                ShiftWithActivityDTO previousShift = shifts.get(i - 1);
                ShiftWithActivityDTO currentShift = null;
                if(previousShift.isPresence()){
                    currentShift = shifts.stream().filter(shiftWithActivityDTO -> previousShift.getEndDate().before(shiftWithActivityDTO.getStartDate()) && (shiftWithActivityDTO.isPresence() || shiftWithActivityDTO.isAbsence())).findFirst().orElse(null);
                }else if(previousShift.isAbsence()){
                    currentShift = shifts.stream().filter(shiftWithActivityDTO -> previousShift.getEndDate().before(shiftWithActivityDTO.getStartDate()) && (shiftWithActivityDTO.isPresence())).findFirst().orElse(null);
                }
                if(isNotNull(currentShift)) {
                    Long minutes = DateUtils.getMinutesBetweenDate(previousShift.getEndDate(), currentShift.getStartDate());
                    if (previousShift.getRestingMinutes() > minutes) {
                        previousShift.setRestingMinutes(minutes.intValue());
                    }
                }
            }
        }
    }
}
