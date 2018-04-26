package com.kairos.activity.schedular;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.organization.OrganizationPhaseDTO;
import com.kairos.activity.persistence.repository.activity.ShiftMongoRepository;
import com.kairos.activity.service.phase.PhaseService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.kairos.activity.constants.AppConstants.*;
import static com.kairos.activity.util.DateUtils.ONLY_DATE;

/**
 * Created by vipul on 20/9/17.
 */

public class PhaseChangeScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PhaseChangeScheduler.class);
    @Autowired
    private OrganizationRestClient organizationRestClient;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    @Autowired
    private PhaseService phaseService;


    @Scheduled(cron = "00 59 23  * * 4")
    public void changeRequestToPuzzle() throws ParseException {
        logger.info("-------------------------- SCHEDULER TO CHANGE PHASES ON THURSDAY---------------------------------------" );
        List<OrganizationPhaseDTO> organizationPhaseDTOS = phaseService.getPhasesGroupByOrganization();
        for (OrganizationPhaseDTO organizationPhaseDTO : organizationPhaseDTOS) {
            changeRequestToPuzzle(organizationPhaseDTO);
        }
    }

    @Scheduled(cron = "00 59 23  * * 7")
    public void changeConstructionToFinal() throws ParseException {
        logger.info("-------------------------- SCHEDULER TO CHANGE PHASES ON SUNDAY---------------------------------------");
        List<OrganizationPhaseDTO> organizationPhaseDTOS = phaseService.getPhasesGroupByOrganization();
        for (OrganizationPhaseDTO organizationPhaseDTO : organizationPhaseDTOS) {

            changeConstructionToFinal(organizationPhaseDTO);
        }

    }

    public void changeRequestToPuzzle(OrganizationPhaseDTO organizationPhaseDTO) throws ParseException {
        long requestPhaseStartFrom = 0;
        long puzzlePhaseStartFrom = 0;
        List<PhaseDTO> phaseDTOS = organizationPhaseDTO.getPhases();
        for (PhaseDTO phase : phaseDTOS) {
            if (phase.getName().equals("FINAL") || phase.getName().equals("PUZZLE")) {
                requestPhaseStartFrom = requestPhaseStartFrom + phase.getDuration();
            }
            if (phase.getName().equals("FINAL"))
                puzzlePhaseStartFrom += phase.getDuration();


        }

        requestPhaseStartFrom = (requestPhaseStartFrom * 7) ;  // converted ito days and added this week as well
        puzzlePhaseStartFrom = (puzzlePhaseStartFrom * 7) +7 ;// converted ito days and added this week as well + Added Request days as well
        logger.info("requestPhaseStartFrom " + requestPhaseStartFrom + " days after &  puzzlePhaseStartFrom " + puzzlePhaseStartFrom );
        updatePhases(organizationPhaseDTO.getId(), requestPhaseStartFrom, "REQUEST_TO_PUZZLE_PHASE");
        updatePhases(organizationPhaseDTO.getId(), puzzlePhaseStartFrom, "PUZZLE_TO_CONSTRUCTION_PHASE");
    }

    public void changeConstructionToFinal(OrganizationPhaseDTO organizationPhaseDTO) throws ParseException {
        long finalPhaseWeekDuration = 0;
        List<PhaseDTO> phaseDTOS = organizationPhaseDTO.getPhases();
        for (PhaseDTO phase : phaseDTOS) {
            if (phase.getName().equals("FINAL"))
                finalPhaseWeekDuration = phase.getDuration();
        }
        finalPhaseWeekDuration = (finalPhaseWeekDuration * 7);  // converted ito days and added this week as well + added  7 days
        logger.info("finalWeekDuration " + finalPhaseWeekDuration + " days after &  finalPhaseWeekDuration " + finalPhaseWeekDuration + " days.");
        updatePhases(organizationPhaseDTO.getId(), finalPhaseWeekDuration, CONSTRUCTION_TO_FINAL_PHASE);

    }

    private void updatePhases(Long orgId, long phaseStartDate, String phaseName) throws ParseException {
        DateFormat dateISOFormat = new SimpleDateFormat(ONLY_DATE);
        LocalDate startDateLocal = LocalDate.now().plusWeeks(1L).plusDays(phaseStartDate);
        Date startDate = dateISOFormat.parse(startDateLocal.toString());
        Date startDateInISO = new DateTime(startDate).toDate();

        LocalDate endDateLocal = LocalDate.now().plusWeeks(1L).plusDays(phaseStartDate + 6);
        Date endDate = dateISOFormat.parse(endDateLocal.toString());
        Date endDateInISO = new DateTime(endDate).toDate();
        if (phaseName.equals(REQUEST_TO_PUZZLE_PHASE)) {
            logger.info("REQUEST_TO_PUZZLE_PHASE"+" start Date"+startDateInISO+" end date   "+ endDateInISO+" "+ PUZZLE_PHASE_NAME+"  "+PUZZLE_PHASE_DESCRIPTION);
            shiftMongoRepository.updatePhasesOfActivities(orgId, startDateInISO, endDateInISO, PUZZLE_PHASE_NAME, PUZZLE_PHASE_DESCRIPTION);
        }
        if (phaseName.equals(PUZZLE_TO_CONSTRUCTION_PHASE)) {
            logger.info("PUZZLE_TO_CONSTRUCTION_PHASE" +"start Date"+ startDateInISO+" end date "+endDateInISO + CONSTRUCTION_PHASE_NAME, CONSTRUCTION_PHASE_DESCRIPTION);
            shiftMongoRepository.updatePhasesOfActivities(orgId, startDateInISO, endDateInISO, CONSTRUCTION_PHASE_NAME, CONSTRUCTION_PHASE_DESCRIPTION);
        }
        if (phaseName.equals(CONSTRUCTION_TO_FINAL_PHASE)) {
            logger.info("CONSTRUCTION_TO_FINAL_PHASE "+ " startDate " +startDateInISO+" snd date " +endDateInISO+ FINAL_PHASE_NAME, FINAL_PHASE_DESCRIPTION);
            shiftMongoRepository.updatePhasesOfActivities(orgId, startDateInISO, endDateInISO, FINAL_PHASE_NAME, FINAL_PHASE_DESCRIPTION);
        }
    }


}
