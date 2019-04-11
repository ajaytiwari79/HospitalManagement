package com.kairos.service.unit_position;

import com.kairos.dto.user.staff.unit_position.EmploymentDTO;
import com.kairos.persistence.model.user.unit_position.Employment;
import com.kairos.service.exception.ExceptionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 2/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmploymentServiceUnitTest {
    @InjectMocks
    EmploymentService employmentService;
    @Mock
    ExceptionService exceptionService;
    static List<Employment> employments = new ArrayList<Employment>();
    static EmploymentDTO employmentDTO;

    @Before
    public void setUp() throws Exception {
        Employment uep = new Employment(LocalDate.now(),LocalDate.now().plusDays(10) );
        Employment uep2 = new Employment(LocalDate.now().plusDays(11), null );
        employments.add(uep);
        employments.add(uep2);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutEndDate() throws Exception {
        employmentDTO = new EmploymentDTO( 733L, LocalDate.of(2018,12,02), null, 100, 10.2f, new BigDecimal(10.2f), 10.2d, null);
        boolean result = employmentService.validateEmploymentWithExpertise(employments, employmentDTO);
        Assert.assertTrue(result);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDates() throws Exception {
        employmentDTO = new EmploymentDTO(733L,LocalDate.of(2018,12,02), LocalDate.of(2019,06,02), 100, 10.2f, new BigDecimal(10.2f), 10.2d, null);
        employments.get(1).setEndDate(LocalDate.now().plusDays(100));
        boolean result= employmentService.validateEmploymentWithExpertise(employments, employmentDTO);
        Assert.assertTrue(result);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithEndDate() throws Exception {
        employmentService.validateEmploymentWithExpertise(employments, employmentDTO);
        Employment uep5 = new Employment(LocalDate.now(), LocalDate.now().plusDays(5));
        employments.add(uep5);
        boolean result= employmentService.validateEmploymentWithExpertise(employments, employmentDTO);
        Assert.assertTrue(result);
    }

    @Test
    public void validateUnitEmploymentPositionWithExpertiseOverLapCase() throws Exception {
        employments.clear();
        Employment uep5 = new Employment(LocalDate.now(), LocalDate.now().plusDays(5));
        employments.add(uep5);
        boolean result = employmentService.validateEmploymentWithExpertise(employments, employmentDTO);
        Assert.assertTrue(result);
    }


    @Test
    public void validateUnitEmploymentPositionWithExpertiseWithoutOverLap() throws Exception {
        employments.clear();
        Employment uep5 = new Employment(LocalDate.now(), LocalDate.now().plusDays(5));
        employments.add(uep5);
        boolean result =  employmentService.validateEmploymentWithExpertise(employments, employmentDTO);
        Assert.assertTrue(result);
    }

}