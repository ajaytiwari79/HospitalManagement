package com.kairos.service.open_shift;

import com.kairos.commons.service.mail.MailService;
import com.kairos.dto.activity.open_shift.OpenShiftResponseDTO;
import com.kairos.dto.activity.open_shift.OpenShiftWrapper;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.StaffUnitPositionDetails;
import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.enums.open_shift.OpenShiftAction;
import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.OpenShiftActivityWrapper;
import com.kairos.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.persistence.model.open_shift.Order;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.persistence.repository.open_shift.OrderMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.service.priority_group.PriorityGroupService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.time_bank.TimeBankService;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.utils.time_bank.TimeBankCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.dto.activity.open_shift.ShiftAssignmentCriteria.*;
import static com.kairos.constants.AppConstants.SHIFT_NOTIFICATION;
import static com.kairos.constants.AppConstants.SHIFT_NOTIFICATION_MESSAGE;

@Service
@Transactional
public class OpenShiftService extends MongoBaseService {

    private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);
    @Inject
    private OpenShiftMongoRepository openShiftMongoRepository;
    @Inject
    private PriorityGroupService priorityGroupService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private OrderMongoRepository orderMongoRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private TimeBankService timeBankService;
    @Inject
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private MailService mailService;
    @Inject
    private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;

    public OpenShiftResponseDTO createOpenShift(OpenShiftResponseDTO openShiftResponseDTO) {

        OpenShift openShift = new OpenShift();
        ObjectMapperUtils.copyProperties(openShiftResponseDTO, openShift);
        save(openShift);
        openShiftResponseDTO.setId(openShift.getId());
        return openShiftResponseDTO;
    }


    public List<OpenShiftResponseDTO> updateOpenShift(List<OpenShiftResponseDTO> openShiftResponseDTOs, BigInteger orderId) {

        List<OpenShift> openShifts = new ArrayList<>();
        OpenShift openShift;
        List<BigInteger> openShiftIds = openShiftResponseDTOs.stream().filter(openShiftResponseDTO -> openShiftResponseDTO.getId() != null).map(openShiftResponseDTO -> new BigInteger(openShiftResponseDTO.getId().toString())).collect(Collectors.toList());
        List<OpenShift> openShiftsUpdated = openShiftMongoRepository.findAllByIdsAndDeletedFalse(openShiftIds);
        Map<BigInteger, OpenShift> openShiftsMap = openShiftsUpdated.stream().collect(Collectors.toMap(OpenShift::getId,
                openShiftUpdated -> openShiftUpdated));
        for (OpenShiftResponseDTO openShiftResponseDTO : openShiftResponseDTOs) {
            if (Optional.ofNullable(openShiftResponseDTO.getId()).isPresent()) {
                openShift = openShiftsMap.get(openShiftResponseDTO.getId());
            } else {
                openShift = new OpenShift();
            }
            BeanUtils.copyProperties(openShiftResponseDTO, openShift, openShiftResponseDTO.getStartDate().toString());
            openShift.setOrderId(orderId);
            openShift.setEndDate(openShiftResponseDTO.getFromTime().isAfter(openShiftResponseDTO.getToTime()) ? DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate().plusDays(1), openShiftResponseDTO.getFromTime()) :
                    DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate(), openShiftResponseDTO.getFromTime()));
            openShifts.add(openShift);

        }
        save(openShifts);
        int currentElement = 0;
        OpenShift openShiftCurrent;
        for (OpenShiftResponseDTO openShiftResponseDTO : openShiftResponseDTOs) {
            openShiftCurrent = openShifts.get(currentElement);
            currentElement++;
            ObjectMapperUtils.copyProperties(openShiftCurrent, openShiftResponseDTO);
        }
        return openShiftResponseDTOs;
    }

    public void createOpenShiftFromOrder(List<OpenShiftResponseDTO> openShiftResponseDTOs, BigInteger orderId) {

        List<OpenShift> openShifts = new ArrayList<>();
        for (OpenShiftResponseDTO openShiftResponseDTO : openShiftResponseDTOs) {
            openShiftResponseDTO.setOrderId(orderId);
            OpenShift openShift = new OpenShift();
            BeanUtils.copyProperties(openShiftResponseDTO, openShift, openShiftResponseDTO.getStartDate().toString());
            openShift.setStartDate(DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate(), openShiftResponseDTO.getFromTime()));
            openShift.setEndDate(openShiftResponseDTO.getFromTime().isAfter(openShiftResponseDTO.getToTime()) ? DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate().plusDays(1), openShiftResponseDTO.getToTime()) :
                    DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate(), openShiftResponseDTO.getToTime()));
            openShifts.add(openShift);

        }
        save(openShifts);
    }

    public void deleteOpenShift(BigInteger openShiftId) {
        OpenShift openShift = openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
        if (!Optional.ofNullable(openShift).isPresent()) {
            throw new DataNotFoundByIdException("OpenShift does not exist by id" + openShiftId);
        }
        openShift.setDeleted(true);
        save(openShift);
    }


    public List<OpenShiftResponseDTO> getOpenshiftsByUnitIdAndOrderId(Long unitId, BigInteger orderId) {

        List<OpenShift> openShifts = openShiftMongoRepository.findOpenShiftsByUnitIdAndOrderId(unitId, orderId);
        List<OpenShiftResponseDTO> openShiftResponseDTOS = new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift, openShiftResponseDTO, openShift.getStartDate().toString(), openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });

        return openShiftResponseDTOS;
    }

    public OpenShiftAction pickOpenShiftByStaff(long unitId, BigInteger openShiftId, long staffId, String action) {
        OpenShiftAction openShiftAction = null;
        OpenShift openShift = openShiftMongoRepository.findByIdAndUnitIdAndDeletedFalse(openShiftId, unitId);
        if (!Optional.ofNullable(openShift).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShift", openShiftId);
        }
        Optional<Order> order = orderMongoRepository.findById(openShift.getOrderId());

        if (action.equals(OpenShiftAction.DECLINE.toString())) {
            openShift.getDeclinedBy().add(staffId);
            openShiftAction = OpenShiftAction.DECLINE;
        } else if (action.equals(OpenShiftAction.STAFF_INTEREST.toString())) {
            openShift.getInterestedStaff().add(staffId);
            openShiftAction = OpenShiftAction.STAFF_INTEREST;
        } else if (action.equals(OpenShiftAction.ASSIGN.toString())) {
            if (order.get().getShiftAssignmentCriteria().equals(PICKABLE)) {
                assignShiftToStaff(openShift, unitId, Collections.singletonList(staffId), order.get());
                openShiftAction = OpenShiftAction.ASSIGN;
            } else if (order.get().getShiftAssignmentCriteria().equals(SHOW_INTEREST_APPROVAL_BY_PLANNER)) {
                openShift.getInterestedStaff().add(staffId);
                openShiftAction = OpenShiftAction.NOTIFY;
            } else if (order.get().getShiftAssignmentCriteria().equals(PICKABLE_APPROVAL_BY_PLANNER)) {

            } else if (order.get().getShiftAssignmentCriteria().equals(SHOW_INTEREST_AUTO_SELECT)) {

            }
        }
        save(openShift);
        return openShiftAction;
    }

    List<OpenShiftResponseDTO> getOpenShiftsByUnitIdAndOrderId(Long unitId, BigInteger orderId) {
        List<OpenShift> openShifts = openShiftMongoRepository.getOpenShiftsByUnitIdAndOrderId(unitId, orderId);
        List<OpenShiftResponseDTO> openShiftResponseDTOS = new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift, openShiftResponseDTO, openShift.getStartDate().toString(), openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });
        return openShiftResponseDTOS;
    }


    public OpenShiftWrapper fetchOpenShiftDataByStaff(Long unitId, BigInteger openShiftId, Long staffId,AccessGroupRole role) {
        OpenShiftActivityWrapper openShiftActivityWrapper = openShiftMongoRepository.getOpenShiftAndActivity(openShiftId, unitId);
        OpenShift openShift = openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
        Date startDate = DateUtils.getStartDateOfWeekFromDate(DateUtils.asLocalDate(openShift.getStartDate()));
        Date endDate = DateUtils.getDateFromLocalDate(DateUtils.asLocalDate(startDate).plusDays(6));
        int[] data={0,0};
        if(role.equals(AccessGroupRole.STAFF)){
            Long unitPositionId = userIntegrationService.getUnitPositionId(unitId, staffId, openShiftActivityWrapper.getExpertiseId());
            UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = timeBankService.getCostTimeAgreement(unitPositionId,startDate,endDate);
            data = timeBankCalculationService.calculateDailyTimeBankForOpenShift(openShift, openShiftActivityWrapper.getActivity(), unitPositionWithCtaDetailsDTO);
        }

        List<OpenShift> similarShifts = openShiftMongoRepository.findAllOpenShiftsByActivityIdAndBetweenDuration(openShiftActivityWrapper.getActivity().getId(), startDate, endDate);

        List<OpenShiftResponseDTO> openShiftResponseDTOS = new ArrayList<>();
        similarShifts.forEach(openShift1 -> {
            OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift1, openShiftResponseDTO, openShift1.getStartDate().toString(), openShift1.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift1.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift1.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift1.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift1.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });
        OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
        BeanUtils.copyProperties(openShift, openShiftResponseDTO, openShift.getStartDate().toString(), openShift.getEndDate().toString());
        openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
        openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
        openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
        openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
        return new OpenShiftWrapper(data[0], data[1], 0, openShiftResponseDTOS, openShiftResponseDTO);
    }

    public boolean notifyStaff(Long unitId, BigInteger openShiftId, List<Long> staffIds, String action) {
        OpenShift openShift = openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
        if (!Optional.ofNullable(openShift).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShift", openShiftId);
        }
        if (OpenShiftAction.ASSIGN.name().equals(action)) {
            Optional<Order> order = orderMongoRepository.findById(openShift.getOrderId());
            assignShiftToStaff(openShift, unitId, staffIds, order.get());

        } else if (OpenShiftAction.NOTIFY.name().equals(action)) {
            List<String> emails = userIntegrationService.getEmailsOfStaffByStaffIds(unitId, staffIds);
            String[] recievers = emails.toArray(new String[emails.size()]);
            mailService.sendMailWithAttachment(recievers, SHIFT_NOTIFICATION, SHIFT_NOTIFICATION_MESSAGE, null);
            List<OpenShiftNotification> openShiftNotifications = new ArrayList<>();
            staffIds.forEach(staffId -> openShiftNotifications.add(new OpenShiftNotification(openShiftId, staffId)));
            save(openShiftNotifications);
        }
        return true;
    }

    private boolean assignShiftToStaff(OpenShift openShift, Long unitId, List<Long> staffIds, Order order) {
        List<StaffUnitPositionDetails> unitPositionDetails = userIntegrationService.getStaffIdAndUnitPositionId(unitId, staffIds, order.getExpertiseId());
        unitPositionDetails.forEach(unitPositionDetail -> {
            if (!Optional.ofNullable(unitPositionDetail.getId()).isPresent() || openShift.getNoOfPersonRequired()<1 ||
                    openShift.getAssignedStaff().contains(unitPositionDetail.getStaffId()) ) {
                return;
            }
            ShiftActivityDTO shiftActivity = new ShiftActivityDTO("",openShift.getStartDate(),openShift.getEndDate(),openShift.getActivityId());
            ShiftDTO shiftDTO = new ShiftDTO(Arrays.asList(shiftActivity), unitId, unitPositionDetail.getStaffId(), unitPositionDetail.getId());
            shiftDTO.setShiftDate(DateUtils.asLocalDate(openShift.getStartDate()));
            shiftDTO.setParentOpenShiftId(openShift.getId());
            shiftService.createShift(unitId, shiftDTO, "Organization", false);
            openShift.setNoOfPersonRequired(openShift.getNoOfPersonRequired() - 1);
            openShift.getAssignedStaff().add(unitPositionDetail.getStaffId());
        });
        save(openShift);
        return true;
    }

}

