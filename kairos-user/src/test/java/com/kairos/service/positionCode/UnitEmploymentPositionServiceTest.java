package com.kairos.service.positionCode;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.persistence.model.user.unitEmploymentPosition.UnitEmploymentPosition;
import com.kairos.response.dto.web.UnitEmploymentPositionDTO;
import com.kairos.service.unitEmploymentPosition.UnitEmploymentPositionService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        UnitEmploymentPosition uep = new UnitEmploymentPosition(new DateTime("2017-02-10T00:00:00.000Z").getMillis(), new DateTime("2017-08-19T00:00:00.000Z").getMillis(), 733, 14, 15, 16, 18000);
        UnitEmploymentPosition uep2 = new UnitEmploymentPosition(new DateTime("2017-08-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000);

        //UnitEmploymentPosition uep3 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000);
        //UnitEmploymentPosition uep4 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep);
        unitEmploymentPositions.add(uep2);
        // unitEmploymentPositions.add(uep3);
        ///unitEmploymentPositions.add(uep4);


    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutEndDate() throws Exception {
        unitEmploymentPositionDTO = new UnitEmploymentPositionDTO(14L, 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 100, 10.2f, 10.2f, 10.2f, null);
        when(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO))
                .thenThrow(new ActionNotPermittedException("Already a unit employment positionCode is active with same expertise on this period."));
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDates() throws Exception {
        unitEmploymentPositionDTO = new UnitEmploymentPositionDTO(14L, 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 100, 10.2f, 10.2f, 10.2f, null);
        unitEmploymentPositions.get(1).setEndDateMillis(new DateTime("2017-08-11T00:00:00.000Z").getMillis());
        System.out.println(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO));
    }

    //
    //@Test public  void  validateW
    /*        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L);
        unitEmploymentPositionDTO.setEndDateMillis(1488439702000L);
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
        when(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO)).thenThrow(new ActionNotPermittedException("Already a unit employment positionCode is active with same expertise on this period."));
        /*Epoch timestamp: 1549092502
          Timestamp in milliseconds: 1549092502000
          Human time (GMT): Saturday, 2 February 2019 07:28:22

UnitEmploymentPosition uep5 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
*/
    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDate() throws Exception {

        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitEmploymentPositionDTO.setEndDateMillis(new DateTime("2018-02-10T00:00:00.000Z").getMillis());  // GMT: Thursday, 2 March 2017 07:28:22
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
        //when(unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO)).thenThrow(new ActionNotPermittedException(""));
        /*Epoch timestamp: 1549092502
          Timestamp in milliseconds: 1549092502000
          Human time (GMT): Saturday, 2 February 2019 07:28:22
          */
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);

    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseOverLapCase() throws Exception {
        unitEmploymentPositions.clear();
        //  new DateTime("2018-02-10T00:00:00.000Z").getMillis()    GMT: Friday, 2 February 2018 07:28:22
        // 1549092502000L   2019-02-02T12:58:22.000+05:30
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitEmploymentPositionDTO.setEndDateMillis(new DateTime("2018-02-10T00:00:00.000Z").getMillis());  // GMT: Friday, 2 February 2018 07:28:22
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);

    }


    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutOverLap() throws Exception {
        unitEmploymentPositions.clear();
        //  new DateTime("2018-02-10T00:00:00.000Z").getMillis()    GMT: Friday, 2 February 2018 07:28:22
        // 1549092502000L   2019-02-02T12:58:22.000+05:30
        UnitEmploymentPosition uep5 = new UnitEmploymentPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitEmploymentPositionService.validateUnitEmploymentPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);

    }
}