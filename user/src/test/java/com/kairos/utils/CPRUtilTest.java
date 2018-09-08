package com.kairos.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Created by pavan on 14/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
    public class CPRUtilTest {
    @InjectMocks
    CPRUtil cprUtil;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getDateOfBirthFromCPR() throws Exception {
        LocalDate expectedDOB=CPRUtil.getDateOfBirthFromCPR("0403812765");
        assertEquals(expectedDOB,LocalDate.of(1981,03,04));
    }
}