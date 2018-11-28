package com.kairos.service.activity;

import com.kairos.dto.activity.shift.ButtonConfig;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import org.bouncycastle.util.test.FixedSecureRandom;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

/**
 * Created by vipul on 19/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShiftServicejUnitTest {

    @InjectMocks
    private ShiftService shiftService;
    @Mock
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Mock
    private ExceptionService exceptionService;

    @Test
    public void findButtonConfigForSendToPayrollNegativeCase() {
        Date startDate = new Date(2018,11,19);
        Date endDate = new Date(2018,11,25);
        List<ShiftDTO> shifts = new ArrayList<>();

        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13870L),new Date(2018,11,19,13,0),new Date(2018,11,19,16,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13562L),new Date(2018,11,22,9,0),new Date(2018,11,22,14,0),35602L,32545L);
        shifts.add(shift);

        List<ShiftState> shiftStates = new ArrayList<>();
        ShiftState shiftState = new ShiftState(BigInteger.valueOf(13879L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shiftStates.add(shiftState);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());

        when(shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(shiftStates);

        ButtonConfig buttonConfig = shiftService.findButtonConfig(shifts,startDate,endDate,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),false);;


    }


    @Test
    public void findButtonConfigForSendToPayrollPositiveCase() {
        Date startDate = new Date(2018,11,19);
        Date endDate = new Date(2018,11,25);
        List<ShiftDTO> shifts = new ArrayList<>();

        ShiftDTO shift = new ShiftDTO(BigInteger.valueOf(13879L),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shifts.add(shift);
        shift = new ShiftDTO(BigInteger.valueOf(13880L),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,18752L);
        shifts.add(shift);


        List<ShiftState> shiftStates = new ArrayList<>();
        ShiftState shiftState = new ShiftState(BigInteger.valueOf(13879L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,20,0),35602L,14139L);
        shiftStates.add(shiftState);
        shiftState = new ShiftState(BigInteger.valueOf(13880L),AccessGroupRole.MANAGEMENT,"TIME & ATTENDANCE",LocalDate.of(2018,11,21),new Date(2018,11,21,15,0),new Date(2018,11,21,21,0),35602L,14139L);
        shiftStates.add(shiftState);
        Set<BigInteger> shiftIds = shifts.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());

        when(shiftStateMongoRepository.findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(shiftIds,AccessGroupRole.MANAGEMENT)).thenReturn(shiftStates);

        ButtonConfig buttonConfig = shiftService.findButtonConfig(shifts,startDate,endDate,true);
        Assert.assertEquals(buttonConfig.isSendToPayrollEnabled(),true);;


    }

}
