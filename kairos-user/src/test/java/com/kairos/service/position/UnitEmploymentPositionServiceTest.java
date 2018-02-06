package com.kairos.service.position;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by vipul on 2/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitEmploymentPositionServiceTest {
    @InjectMocks
    UnitEmploymentPositionService unitEmploymentPositionService;
    static List<UnitEmploymentPosition> unitEmploymentPositions = new ArrayList<UnitEmploymentPosition>();
    static UnitEmploymentPositionDTO unitEmploymentPositionDTO;

    @Before
    public void setUp() throws Exception {
        /*Epoch timestamp: 1517556502
          Timestamp in milliseconds: 1517556502000
          Human time (GMT): Friday, 2 February 2018 07:28:22
          */
        UnitEmploymentPosition uep = new UnitEmploymentPosition(1517556502000L, null, 733, 14, 15, 16, 18000);
        UnitEmploymentPosition uep2 = new UnitEmploymentPosition(1517556502000L, null, 733, 14, 15, 16, 18000);
        UnitEmploymentPosition uep3 = new UnitEmploymentPosition(1517556502000L, null, 733, 14, 15, 16, 18000);
        UnitEmploymentPosition uep4 = new UnitEmploymentPosition(1517556502000L, null, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep);
        unitEmploymentPositions.add(uep2);
        unitEmploymentPositions.add(uep3);
        unitEmploymentPositions.add(uep4);
        unitEmploymentPositionDTO = new UnitEmploymentPositionDTO(14L, 733L, 1517556502000L, null, 100, 10.2f, 10.2f, 10.2f, null);


    }

    @Test
    public void validateUnitEmploymentPositionWithExpertise() throws Exception {
        //  unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
        /*Timestamp in milliseconds: 1549092502000
         Human time (GMT): Saturday, 2 February 2019 07:28:22*/
        when(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO))
                .thenThrow(new ActionNotPermittedException("Already a unit employment position is active with same expertise on this period."));
          /*Timestamp in milliseconds: 1486020502000
          Human time (GMT): Thursday, 2 February 2017 07:28:22
          Timestamp in milliseconds: 1488439702000
            Human time (GMT): Thursday, 2 March 2017 07:28:22
          */
        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L);
        unitEmploymentPositionDTO.setEndDateMillis(1488439702000L);
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
        when(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO)).thenThrow(new ActionNotPermittedException(""));
        /*Epoch timestamp: 1549092502
          Timestamp in milliseconds: 1549092502000
          Human time (GMT): Saturday, 2 February 2019 07:28:22
          */
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(1517556502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);


    }

    @Test
    public void validateUnitEmploymentPositionWithExpertise1() throws Exception {

        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitEmploymentPositionDTO.setEndDateMillis(1517556502000L);  // GMT: Thursday, 2 March 2017 07:28:22
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
        //when(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO)).thenThrow(new ActionNotPermittedException(""));
        /*Epoch timestamp: 1549092502
          Timestamp in milliseconds: 1549092502000
          Human time (GMT): Saturday, 2 February 2019 07:28:22
          */
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(1517556502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);

    }

    @Test
    public void validateUnitEmploymentPositionWithExpertise2() throws Exception {
        unitEmploymentPositions.clear();
        //  1517556502000L    GMT: Friday, 2 February 2018 07:28:22
        // 1549092502000L   2019-02-02T12:58:22.000+05:30
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitEmploymentPositionDTO.setEndDateMillis(1517556502000L);  // GMT: Friday, 2 February 2018 07:28:22
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);

    }


    @Test
    public void validateUnitEmploymentPositionWithExpertise3() throws Exception {
        unitEmploymentPositions.clear();
        //  1517556502000L    GMT: Friday, 2 February 2018 07:28:22
        // 1549092502000L   2019-02-02T12:58:22.000+05:30
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);

    }
}