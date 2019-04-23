package com.kairos.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Created by pavan on 14/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
    public class CPRUtilUnitTest {
    @InjectMocks
    CPRUtil cprUtil;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getDateOfBirthFromCPR() {
        LocalDate expectedDOB=CPRUtil.getDateOfBirthFromCPR("1210611556");
        assertEquals(expectedDOB,LocalDate.of(1961,10,12));
    }
}