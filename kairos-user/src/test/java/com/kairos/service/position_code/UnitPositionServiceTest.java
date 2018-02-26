package com.kairos.service.position_code;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.response.dto.web.UnitPositionDTO;
import com.kairos.service.unit_employment_position.UnitPositionService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by vipul on 2/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitPositionServiceTest {
    @InjectMocks
    UnitPositionService unitPositionService;
    static List<UnitPosition> unitPositions = new ArrayList<UnitPosition>();
    static UnitPositionDTO unitPositionDTO;

    @Before
    public void setUp() throws Exception {
        UnitPosition uep = new UnitPosition(new DateTime("2017-02-10T00:00:00.000Z").getMillis(), new DateTime("2017-08-19T00:00:00.000Z").getMillis(), 733, 14, 15, 16, 18000);
        UnitPosition uep2 = new UnitPosition(new DateTime("2017-08-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000);

        //UnitEmploymentPosition uep3 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000);
        //UnitEmploymentPosition uep4 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000);
        unitPositions.add(uep);
        unitPositions.add(uep2);
        // unitEmploymentPositions.add(uep3);
        ///unitEmploymentPositions.add(uep4);


    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutEndDate() throws Exception {
        unitPositionDTO = new UnitPositionDTO(14L, 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 100, 10.2f, 10.2f, 10.2f, null);
        when(unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO))
                .thenThrow(new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period."));
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDates() throws Exception {
        unitPositionDTO = new UnitPositionDTO(14L, 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 100, 10.2f, 10.2f, 10.2f, null);
        unitPositions.get(1).setEndDateMillis(new DateTime("2017-08-11T00:00:00.000Z").getMillis());
        System.out.println(unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO));
    }

    //
    //@Test public  void  validateW
    /*        unitEmploymentPositionDTO.setStartDateMillis(1486020502000L);
        unitEmploymentPositionDTO.setEndDateMillis(1488439702000L);
        unitEmploymentPositionService.validateUnitPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
        when(unitEmploymentPositionService.validateUnitPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO)).thenThrow(new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period."));
        /*Epoch timestamp: 1549092502
          Timestamp in milliseconds: 1549092502000
          Human time (GMT): Saturday, 2 February 2019 07:28:22

UnitEmploymentPosition uep5 = new UnitEmploymentPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 1549092502000L, 733, 14, 15, 16, 18000);
        unitEmploymentPositions.add(uep5);
        unitEmploymentPositionService.validateUnitPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO);
*/
    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDate() throws Exception {

        unitPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitPositionDTO.setEndDateMillis(new DateTime("2018-02-10T00:00:00.000Z").getMillis());  // GMT: Thursday, 2 March 2017 07:28:22
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
        //when(unitEmploymentPositionService.validateUnitPositionWithExpertise(unitEmploymentPositions, unitEmploymentPositionDTO)).thenThrow(new ActionNotPermittedException(""));
        /*Epoch timestamp: 1549092502
          Timestamp in milliseconds: 1549092502000
          Human time (GMT): Saturday, 2 February 2019 07:28:22
          */
        UnitPosition uep5 = new UnitPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 1549092502000L, 733, 14, 15, 16, 18000);
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);

    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseOverLapCase() throws Exception {
        unitPositions.clear();
        //  new DateTime("2018-02-10T00:00:00.000Z").getMillis()    GMT: Friday, 2 February 2018 07:28:22
        // 1549092502000L   2019-02-02T12:58:22.000+05:30
        UnitPosition uep5 = new UnitPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitPositions.add(uep5);
        unitPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitPositionDTO.setEndDateMillis(new DateTime("2018-02-10T00:00:00.000Z").getMillis());  // GMT: Friday, 2 February 2018 07:28:22
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);

    }


    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutOverLap() throws Exception {
        unitPositions.clear();
        //  new DateTime("2018-02-10T00:00:00.000Z").getMillis()    GMT: Friday, 2 February 2018 07:28:22
        // 1549092502000L   2019-02-02T12:58:22.000+05:30
        UnitPosition uep5 = new UnitPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000);
        unitPositions.add(uep5);
        unitPositionDTO.setStartDateMillis(1486020502000L); //GMT: Thursday, 2 February 2017 07:28:22
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);

    }

}