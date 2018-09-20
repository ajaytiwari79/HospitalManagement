package com.kairos.service.position_code;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.dto.user.staff.unit_position.UnitPositionDTO;
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
        UnitPosition uep = new UnitPosition(new DateTime("2017-02-10T00:00:00.000Z").getMillis(), new DateTime("2017-08-19T00:00:00.000Z").getMillis(), 733, 14, 15, 16, 18000d);
        UnitPosition uep2 = new UnitPosition(new DateTime("2017-08-10T00:00:00.000Z").getMillis(), null, 733, 14, 15, 16, 18000d);
        unitPositions.add(uep);
        unitPositions.add(uep2);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutEndDate() throws Exception {
        unitPositionDTO = new UnitPositionDTO(14L, 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 100, 10.2f, 10.2f, 10.2d, null);
        when(unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO))
                .thenThrow(new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period."));
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDates() throws Exception {
        unitPositionDTO = new UnitPositionDTO(14L, 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 100, 10.2f, 10.2f, 10.2d, null);
        unitPositions.get(1).setEndDateMillis(new DateTime("2017-08-11T00:00:00.000Z").getMillis());
        System.out.println(unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO));
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDate() throws Exception {
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
        UnitPosition uep5 = new UnitPosition(new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 1549092502000L, 733, 14, 15, 16, 18000d);
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseOverLapCase() throws Exception {
        unitPositions.clear();
        UnitPosition uep5 = new UnitPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000d);
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
    }


    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutOverLap() throws Exception {
        unitPositions.clear();
        UnitPosition uep5 = new UnitPosition(1486020502000L, 1549092502000L, 733, 14, 15, 16, 18000d);
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
    }

}