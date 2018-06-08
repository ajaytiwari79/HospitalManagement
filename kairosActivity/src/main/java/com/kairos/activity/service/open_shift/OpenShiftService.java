package com.kairos.activity.service.open_shift;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.open_shift.OpenShift;
import com.kairos.activity.persistence.model.open_shift.Order;
import com.kairos.activity.persistence.repository.open_shift.OpenShiftMongoRepository;
import com.kairos.activity.persistence.repository.open_shift.OrderMongoRepository;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.service.priority_group.PriorityGroupService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import com.kairos.response.dto.web.open_shift.ShiftAssignmentCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.response.dto.web.open_shift.ShiftAssignmentCriteria.PICKABLE;
import static com.kairos.response.dto.web.open_shift.ShiftAssignmentCriteria.SHOW_INTEREST_APPROVAL_BY_PLANNER;

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
            ObjectMapperUtils.copyProperties(openShiftResponseDTO, openShift);
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
            ObjectMapperUtils.copyProperties(openShiftResponseDTO, openShift);
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
    public List<OpenShift> getOpenShiftsByUnitIdAndCurrentDate(Long unitId, Date selectedDate,Date startDate) {

        List<OpenShift> openShifts = openShiftMongoRepository.getOpenShiftsByUnitIdAndSelectedDate(unitId, selectedDate,startDate);

        return openShifts;
    }
    public OpenShiftResponseDTO pickOpenShiftByStaff(long unitId, BigInteger openShiftId, long staffId) {
        OpenShift openShift = openShiftMongoRepository.findByIdAndUnitIdAndDeletedFalse(openShiftId, unitId);
        if (!Optional.ofNullable(openShift).isPresent()) {
            exceptionService.dataNotFoundByIdException("exception.dataNotFound", "openShift", openShiftId);
        }
        Optional<Order> order=orderMongoRepository.findById(openShift.getOrderId());
        Long unitPositionId=genericIntegrationService.getUnitPositionId(unitId,staffId,order.get().getExpertiseId());

        if (order.get().getShiftAssignmentCriteria().equals(PICKABLE)) {
            ShiftDTO shiftDTO=new ShiftDTO(openShift.getActivityId(),unitId,staffId,unitPositionId,DateUtils.asLocalDate(openShift.getStartDate()),
                    DateUtils.asLocalDate(openShift.getEndDate()),DateUtils.asLocalTime(openShift.getStartDate()),DateUtils.asLocalTime(openShift.getStartDate()));
            shiftDTO.setShiftDate(DateUtils.asLocalDate(openShift.getStartDate()));
            shiftDTO.setParentOpenShiftId(openShiftId);
            shiftService.createShift(unitId,shiftDTO,"Organization",false);
            openShift.setNoOfPersonRequired(openShift.getNoOfPersonRequired()-1);

        } else if (order.get().getShiftAssignmentCriteria().equals(SHOW_INTEREST_APPROVAL_BY_PLANNER)) {
            openShift.getInterestedStaff().add(staffId);
        }
        save(openShift);
        OpenShiftResponseDTO openShiftResponseDTO = new OpenShiftResponseDTO();
        ObjectMapperUtils.copyProperties(openShift, openShiftResponseDTO);
        return openShiftResponseDTO;
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

}

