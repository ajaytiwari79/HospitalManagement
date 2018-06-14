package com.kairos.activity.service.open_shift;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.OpenShiftAndActivityWrapper;
import com.kairos.activity.persistence.model.open_shift.OpenShiftNotification;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftNotificationMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OrderMongoRepository;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.response.dto.shift.StaffUnitPositionDetails;
import com.kairos.activity.response.dto.time_bank.UnitPositionWithCtaDetailsDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.mail.MailService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.priority_group.PriorityGroupService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.service.time_bank.TimeBankService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.activity.util.time_bank.TimeBankCalculationService;
import com.kairos.response.dto.web.open_shift.OpenShiftAction;
import com.kairos.response.dto.web.open_shift.OpenShiftDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import com.kairos.response.dto.web.open_shift.OpenShiftWrapper;
import com.kairos.response.dto.web.staff.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.activity.constants.AppConstants.*;
import static com.kairos.response.dto.web.open_shift.ShiftAssignmentCriteria.*;

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
    @Inject private OrderMongoRepository orderMongoRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private TimeBankService timeBankService;
    @Inject private TimeBankCalculationService timeBankCalculationService;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private MailService mailService;
    @Inject private OpenShiftNotificationMongoRepository openShiftNotificationMongoRepository;

    public OpenShiftResponseDTO createOpenShift(OpenShiftResponseDTO openShiftResponseDTO) {

        OpenShift openShift = new OpenShift();
        ObjectMapperUtils.copyProperties(openShiftResponseDTO,openShift);
        save(openShift);
        openShiftResponseDTO.setId(openShift.getId());
        return openShiftResponseDTO;
    }





    public List<OpenShiftResponseDTO> updateOpenShift(List<OpenShiftResponseDTO> openShiftResponseDTOs,BigInteger orderId) {

        List<OpenShift> openShifts = new ArrayList<OpenShift>();
        OpenShift openShift = null;
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
            BeanUtils.copyProperties(openShiftResponseDTO,openShift,openShiftResponseDTO.getStartDate().toString());
            openShift.setOrderId(orderId);
            openShift.setEndDate(openShiftResponseDTO.getFromTime().isAfter(openShiftResponseDTO.getToTime())?DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate().plusDays(1),openShiftResponseDTO.getFromTime()):
                    DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate(),openShiftResponseDTO.getFromTime()));
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

        List<OpenShift> openShifts = new ArrayList<OpenShift>();
        for (OpenShiftResponseDTO openShiftResponseDTO : openShiftResponseDTOs) {
            openShiftResponseDTO.setOrderId(orderId);
            OpenShift openShift = new OpenShift();
            BeanUtils.copyProperties(openShiftResponseDTO,openShift,openShiftResponseDTO.getStartDate().toString());
            openShift.setStartDate(DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate(),openShiftResponseDTO.getFromTime()));
            openShift.setEndDate(openShiftResponseDTO.getFromTime().isAfter(openShiftResponseDTO.getToTime())?DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate().plusDays(1),openShiftResponseDTO.getFromTime()):
                    DateUtils.getDateByLocalDateAndLocalTime(openShiftResponseDTO.getStartDate(),openShiftResponseDTO.getFromTime()));
            openShifts.add(openShift);

        }
        save(openShifts);
    }

    public void deleteOpenShift(BigInteger openShiftId) {
        OpenShift openShift = openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
        if(!Optional.ofNullable(openShift).isPresent()) {
            throw new DataNotFoundByIdException("OpenShift does not exist by id"+ openShiftId);
        }
        openShift.setDeleted(true);
        save(openShift);
    }


    public List<OpenShiftResponseDTO> getOpenshiftsByUnitIdAndOrderId(Long unitId, BigInteger orderId) {

        List<OpenShift> openShifts = openShiftMongoRepository.findOpenShiftsByUnitIdAndOrderId(unitId,orderId);
        List<OpenShiftResponseDTO> openShiftResponseDTOS=new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO=new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift,openShiftResponseDTO,openShift.getStartDate().toString(),openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });

        return openShiftResponseDTOS;
    }
    public List<OpenShift> getOpenShiftsByUnitIdAndCurrentDate(Long unitId, Date startDate,Date endDate) {

        List<OpenShift> openShifts = openShiftMongoRepository.getOpenShiftsByUnitIdAndDate(unitId, startDate,endDate);

        return openShifts;
    }
    public OpenShiftAction pickOpenShiftByStaff(long unitId, BigInteger openShiftId, long staffId, String action) {
        OpenShiftAction openShiftAction=null;
        OpenShift openShift = openShiftMongoRepository.findByIdAndUnitIdAndDeletedFalse(openShiftId, unitId);
        if (!Optional.ofNullable(openShift).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShift", openShiftId);
        }
        Optional<Order> order=orderMongoRepository.findById(openShift.getOrderId());

        if(action.equals(OpenShiftAction.DECLINE.toString())){
            openShift.getDeclinedBy().add(staffId);
            openShiftAction=OpenShiftAction.DECLINE;
        }
        else if(action.equals(OpenShiftAction.STAFF_INTEREST.toString())){
            openShift.getInterestedStaff().add(staffId);
            openShiftAction=OpenShiftAction.STAFF_INTEREST;
        }
        else if(action.equals(OpenShiftAction.ASSIGN.toString())){
            if (order.get().getShiftAssignmentCriteria().equals(PICKABLE)) {
                assignShiftToStaff(openShift, unitId, Arrays.asList(staffId), order.get());
                openShiftAction=OpenShiftAction.ASSIGN;
            } else if (order.get().getShiftAssignmentCriteria().equals(SHOW_INTEREST_APPROVAL_BY_PLANNER)) {
                openShift.getInterestedStaff().add(staffId);
                openShiftAction=OpenShiftAction.NOTIFY;
            } else if (order.get().getShiftAssignmentCriteria().equals(PICKABLE_APPROVAL_BY_PLANNER)) {

            } else if (order.get().getShiftAssignmentCriteria().equals(SHOW_INTEREST_AUTO_SELECT)) {

            }
        }
        save(openShift);
        return openShiftAction;
    }

    List<OpenShiftResponseDTO> getOpenShiftsByUnitIdAndOrderId(Long unitId,BigInteger orderId){
       List<OpenShift> openShifts= openShiftMongoRepository.getOpenShiftsByUnitIdAndOrderId(unitId,orderId);
        List<OpenShiftResponseDTO> openShiftResponseDTOS=new ArrayList<>();
        openShifts.forEach(openShift -> {
            OpenShiftResponseDTO openShiftResponseDTO=new OpenShiftResponseDTO();
            BeanUtils.copyProperties(openShift,openShiftResponseDTO,openShift.getStartDate().toString(),openShift.getEndDate().toString());
            openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
            openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
            openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
            openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
            openShiftResponseDTOS.add(openShiftResponseDTO);
        });
        return openShiftResponseDTOS;
    }

   public OpenShiftWrapper fetchOpenShiftDataByStaff(Long unitId,  BigInteger openShiftId,  Long staffId){
        OpenShiftAndActivityWrapper openShiftAndActivityWrapper=openShiftMongoRepository.getOpenShiftAndActivity(openShiftId,unitId);
        Long unitPositionId=genericIntegrationService.getUnitPositionId(unitId,staffId,openShiftAndActivityWrapper.getExpertiseId());
        UnitPositionWithCtaDetailsDTO unitPositionWithCtaDetailsDTO = timeBankService.getCostTimeAgreement(unitPositionId);
        OpenShift openShift=openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
        Date startDate=DateUtils.getStartDateOfWeekFromDate(DateUtils.asLocalDate(openShift.getStartDate()));
        Date endDate =DateUtils.asDate(DateUtils.asLocalDate(startDate).plusDays(6));
        List<OpenShift> similarShifts=openShiftMongoRepository.findAllOpenShiftsByActivityIdAndBetweenDuration(openShiftAndActivityWrapper.getActivity().getId(), startDate,endDate);
        int [] data= timeBankCalculationService.calculateDailyTimeBankForOpenShift(openShift,openShiftAndActivityWrapper.getActivity(),unitPositionWithCtaDetailsDTO);
       List<OpenShiftResponseDTO> openShiftResponseDTOS=new ArrayList<>();
       similarShifts.forEach(openShift1 -> {
           OpenShiftResponseDTO openShiftResponseDTO=new OpenShiftResponseDTO();
           BeanUtils.copyProperties(openShift1,openShiftResponseDTO,openShift1.getStartDate().toString(),openShift1.getEndDate().toString());
           openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift1.getStartDate()));
           openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift1.getEndDate()));
           openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift1.getStartDate()));
           openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift1.getEndDate()));
           openShiftResponseDTOS.add(openShiftResponseDTO);
       });
       OpenShiftResponseDTO openShiftResponseDTO=new OpenShiftResponseDTO();
       BeanUtils.copyProperties(openShift,openShiftResponseDTO,openShift.getStartDate().toString(),openShift.getEndDate().toString());
       openShiftResponseDTO.setFromTime(DateUtils.asLocalTime(openShift.getStartDate()));
       openShiftResponseDTO.setToTime(DateUtils.asLocalTime(openShift.getEndDate()));
       openShiftResponseDTO.setStartDate(DateUtils.asLocalDate(openShift.getStartDate()));
       openShiftResponseDTO.setEndDate(DateUtils.asLocalDate(openShift.getEndDate()));
        OpenShiftWrapper openShiftWrapper=new OpenShiftWrapper(data[0],data[1],0,openShiftResponseDTOS,openShiftResponseDTO);
        return openShiftWrapper;

    }

    public boolean notifyStaff(Long unitId,BigInteger openShiftId,List<Long> staffIds,String action){
        OpenShift openShift=openShiftMongoRepository.findOpenShiftByIdAndEnabled(openShiftId);
        if (!Optional.ofNullable(openShift).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShift", openShiftId);
        }
        if(action.equals(OpenShiftAction.ASSIGN)){
            Optional<Order> order=orderMongoRepository.findById(openShift.getOrderId());
            assignShiftToStaff(openShift,unitId,staffIds,order.get());

            }
        else if(action.equals(OpenShiftAction.NOTIFY)){
            List<String> emails=genericIntegrationService.getEmailsOfStaffByStaffIds(unitId,staffIds);
            String [] recievers = emails.toArray(new String[emails.size()]);
            mailService.sendMailWithAttachment(recievers,SHIFT_NOTIFICATION,SHIFT_NOTIFICATION_MESSAGE,null);
            List<OpenShiftNotification> openShiftNotifications=new ArrayList<>();
            staffIds.forEach(staffId->{
                openShiftNotifications.add(new OpenShiftNotification(openShiftId,staffId));
            });
            save(openShiftNotifications);
            }
        return true;
    }
    private boolean assignShiftToStaff(OpenShift openShift,Long unitId,List<Long> staffIds ,Order order) {
        List<StaffUnitPositionDetails> unitPositionDetails=genericIntegrationService.getStaffIdAndUnitPositionId(unitId,staffIds,order.getExpertiseId());
        unitPositionDetails.forEach(unitPositionDetail->{
            if(Optional.ofNullable(unitPositionDetail.getId()).isPresent()){
                return;
            }
            ShiftDTO shiftDTO = new ShiftDTO(openShift.getActivityId(), unitId, unitPositionDetail.getStaffId(), unitPositionDetail.getId(), DateUtils.asLocalDate(openShift.getStartDate()),
                    DateUtils.asLocalDate(openShift.getEndDate()), DateUtils.asLocalTime(openShift.getStartDate()), DateUtils.asLocalTime(openShift.getStartDate()));
            shiftDTO.setShiftDate(DateUtils.asLocalDate(openShift.getStartDate()));
            shiftDTO.setParentOpenShiftId(openShift.getId());
            shiftService.createShift(unitId, shiftDTO, "Organization", false);
            openShift.setNoOfPersonRequired(openShift.getNoOfPersonRequired() - 1);
            openShift.getAssignedStaff().add(unitPositionDetail.getStaffId());
        });
        return true;
    }

}

