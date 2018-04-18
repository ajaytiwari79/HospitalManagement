package com.planning.service.shiftPlanningService;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.junit.Assert.*;

public class ShiftRequestPhasePlanningSolutionServiceTest {

    static final Logger logger = LoggerFactory.getLogger(ShiftRequestPhasePlanningSolutionServiceTest.class);
    @Test
    public void getDataFromKairos() throws Exception {
        ShiftRequestPhasePlanningSolutionService solutionService = new ShiftRequestPhasePlanningSolutionService();
        solutionService.getDataFromKairos(145l,new Date(),new Date());
        logger.info("complete");
    }

}