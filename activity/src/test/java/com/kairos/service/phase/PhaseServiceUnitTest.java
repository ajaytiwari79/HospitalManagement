package com.kairos.service.phase;

import com.kairos.rest_client.CountryRestClient;
import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.commons.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by vipul on 8/2/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhaseServiceUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(PhaseServiceUnitTest.class);
    @Mock
    private PhaseMongoRepository phaseMongoRepository;
    @Mock
    private OrganizationRestClient organizationRestClient;
    @Mock
    private CountryRestClient countryRestClient;

    @InjectMocks
    private PhaseService phaseService;
    ArrayList<Phase> phases = new ArrayList();


   // @Before
   /* public void setUp() throws Exception {

        Phase draftPhase = new Phase(DRAFT_PHASE_NAME, DRAFT_PHASE_DESCRIPTION, 0, DurationType.WEEKS, 4, 4L, false, null, null, null, null);
        Phase constructionPhase = new Phase(CONSTRUCTION_PHASE_NAME, CONSTRUCTION_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 3, 4L, false, null, null, null, null);
        Phase puzzlePhase = new Phase(PUZZLE_PHASE_NAME, PUZZLE_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 2, 4L, false, null, null, null, null);
        Phase requestPhase = new Phase(REQUEST_PHASE_NAME, REQUEST_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 1, 4L, false, null, null, null, null);

        phases.add(draftPhase);
        phases.add(constructionPhase);
        phases.add(puzzlePhase);
        phases.add(requestPhase);
    }

    @Test
    public void getCurrentPhaseInUnitByDate() throws Exception {

        Phase requestPhase = new Phase(REQUEST_PHASE_NAME, REQUEST_PHASE_DESCRIPTION, 1, DurationType.WEEKS, 1, 4L, false, null, null, null, null);
        assertEquals(phaseService.getCurrentPhaseInUnitByDate(phases, new Date("2018/03/20")), requestPhase);

    }*/

    @Test
    public void getDifferenceBetweenDatesInMinute() throws Exception {
        // 1516703598355L Tuesday, 23 January 2018 10:33:18.355
        // 1516704318000 (GMT): Tuesday, 23 January 2018 17:03:18

        Long response = DateUtils.getDifferenceBetweenDatesInMinute(new Date(1516703598355L), new Date(1516704318000L));
        logger.info(response + "");
    }
    @Test
    public void tp(){
        for(int i=0;i<Integer.MAX_VALUE;i++){
            String s="sdasdasda"+i;
        }
    }


}