package com.kairos.service.unit_position;

import com.kairos.commons.custom_exception.ActionNotPermittedException;
import com.kairos.persistence.model.user.unit_position.UnitPosition;
import com.kairos.service.unit_position.UnitPositionService;
import com.kairos.dto.user.staff.unit_position.UnitPositionDTO;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by vipul on 2/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitPositionServiceUnitTest {
    @InjectMocks
    UnitPositionService unitPositionService;
    static List<UnitPosition> unitPositions = new ArrayList<UnitPosition>();
    static UnitPositionDTO unitPositionDTO;

    @Before
    public void setUp() throws Exception {
        UnitPosition uep = new UnitPosition(LocalDate.now(),LocalDate.now().plusDays(10) );
        UnitPosition uep2 = new UnitPosition(LocalDate.now().plusDays(11), null );
        unitPositions.add(uep);
        unitPositions.add(uep2);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutEndDate() throws Exception {
        unitPositionDTO = new UnitPositionDTO( 733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), null, 100, 10.2f, new BigDecimal(10.2f), 10.2d, null);
        when(unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO))
                .thenThrow(new ActionNotPermittedException("Already a unit employment position_code is active with same expertise on this period."));
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDates() throws Exception {
        unitPositionDTO = new UnitPositionDTO(733L, new DateTime("2018-02-10T00:00:00.000Z").getMillis(), new DateTime("2018-02-10T00:00:00.000Z").getMillis(), 100, 10.2f, new BigDecimal(10.2f), 10.2d, null);
        unitPositions.get(1).setEndDate(LocalDate.now().plusDays(100));
        System.out.println(unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO));
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDate() throws Exception {
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
        UnitPosition uep5 = new UnitPosition(LocalDate.now(), LocalDate.now().plusDays(5));
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseOverLapCase() throws Exception {
        unitPositions.clear();
        UnitPosition uep5 = new UnitPosition(LocalDate.now(), LocalDate.now().plusDays(5));
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
    }


    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutOverLap() throws Exception {
        unitPositions.clear();
        UnitPosition uep5 = new UnitPosition(LocalDate.now(), LocalDate.now().plusDays(5));
        unitPositions.add(uep5);
        unitPositionService.validateUnitPositionWithExpertise(unitPositions, unitPositionDTO);
    }

}