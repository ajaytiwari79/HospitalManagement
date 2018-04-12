package com.kairos.activity.service.period;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.period.PeriodPhaseFlippingDate;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.persistence.model.enums.DurationType;
import com.kairos.response.dto.web.period.PeriodPhaseAndFlippingDateDTO;
import com.kairos.response.dto.web.period.PeriodPhaseFlippingDateDTO;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by prerna on 6/4/18.
 */
@Service
@Transactional
public class PlanningPeriodService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PlanningPeriodService.class);

    @Inject
    PhaseService phaseService;

    @Inject
    PlanningPeriodMongoRepository planningPeriodMongoRepository;

    @Inject PhaseMongoRepository phaseMongoRepository;

    public Date addDaysInDate(Date date,  int duration, DurationType durationType, int recurringNumber){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (durationType){
            case DAYS: {
                cal.add(Calendar.DATE, duration*recurringNumber);
                break;
            }
            case WEEKS: {
                cal.add(Calendar.DATE, duration *recurringNumber * 7);
                break;
            }
            case MONTHS: {
                cal.add(Calendar.MONTH, duration *recurringNumber );
                break;
            }
        }
        return cal.getTime();
    }

    public void addPeriod(Long unitId, Date startDate, List<PhaseDTO> phases, PlanningPeriodDTO planningPeriodDTO){
        //  Calculate END Date
        Date endDate = addDaysInDate(startDate, planningPeriodDTO.getDuration(), planningPeriodDTO.getDurationType(),1);
        // If End Date is greater than end date of last period (from DTO) and then return
        if (endDate.compareTo(planningPeriodDTO.getEndDate()) > 0){
            return;
        }

        BigInteger currentPhaseId = null;
        BigInteger nextPhaseId = null;

        List<PeriodPhaseFlippingDate> tempPhaseFlippingDate = new ArrayList<>();
        if(Optional.ofNullable(phases).isPresent()){

            Date tempFlippingDate = endDate;
            boolean scopeToFlipNextPhase =true;
            BigInteger previousPhaseId = null;
            for(PhaseDTO phase : phases){
                // Check if duration of period is enough to assign next flipping
                tempFlippingDate = addDaysInDate(tempFlippingDate, -phase.getDurationInDays(), DurationType.DAYS, 1);
                if (scopeToFlipNextPhase && startDate.compareTo(tempFlippingDate) >= 0){
                    currentPhaseId = phase.getId();
                    nextPhaseId = previousPhaseId;
                    scopeToFlipNextPhase = false;
                }
                previousPhaseId = phase.getId();
                 // Calculate flipping date by duration
                 PeriodPhaseFlippingDate periodPhaseFlippingDate = new PeriodPhaseFlippingDate(phase.getId(), scopeToFlipNextPhase ? tempFlippingDate : null);
                tempPhaseFlippingDate.add(periodPhaseFlippingDate);
            }
        }

        // TODO set name of period dynamically
        PlanningPeriod planningPeriod = new PlanningPeriod("Temp Period", startDate, endDate, unitId, tempPhaseFlippingDate, currentPhaseId, nextPhaseId);
        save(planningPeriod);
        addPeriod(unitId, endDate, phases, planningPeriodDTO);
    }

    public List<PlanningPeriodDTO> createPeriod(Long unitId, PlanningPeriodDTO planningPeriodDTO) {
        // TODO Check monday if duration is in week and first day of month if duration is in month
        List<PhaseDTO> phases = phaseService.getApplicablePhasesByOrganizationId(unitId);
        if(!Optional.ofNullable(phases).isPresent()){
            throw new DataNotFoundByIdException("No Phases found in the Organization " + unitId);
        }


        //Set Start Date and End date in PlanningPeriodDTO according to recurringNumber
        planningPeriodDTO.setStartDate(new Date(planningPeriodDTO.getStartDateMillis()));
        planningPeriodDTO.setEndDate(
                addDaysInDate(planningPeriodDTO.getStartDate(),
                        planningPeriodDTO.getDuration(), planningPeriodDTO.getDurationType(), planningPeriodDTO.getRecurringNumber() ) );


        PlanningPeriod firstPlanningPeriod = planningPeriodMongoRepository.getFirstPlanningPeriod(unitId);
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);

        // Check start date can end date of last period
        if(Optional.ofNullable(firstPlanningPeriod).isPresent() && planningPeriodDTO.getStartDate().compareTo(firstPlanningPeriod.getStartDate())==0){
            throw new ActionNotPermittedException("Invalid start date : "+planningPeriodDTO.getStartDateMillis());
        }
        // Check if any period already exist in the given period
        if(Optional.ofNullable(firstPlanningPeriod).isPresent() &&
                planningPeriodDTO.getStartDate().after(firstPlanningPeriod.getStartDate()) &&
                planningPeriodDTO.getStartDate().before(lastPlanningPeriod.getEndDate())  ){
            throw new ActionNotPermittedException("Period already exist between given dates ");
        }

        phases.forEach(phase->{
            switch (phase.getDurationType()){
                case DAYS:{
                    phase.setDurationInDays(phase.getDuration());
                    break;
                }
                case WEEKS:{
                    phase.setDurationInDays(phase.getDuration() * 7);
                    break;
                }
            }
        });
        addPeriod(unitId, planningPeriodDTO.getStartDate(), phases, planningPeriodDTO);
        return getPeriods(unitId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate());
    }

    public List<PlanningPeriodDTO> getPeriods(Long unitId, Date startDate, Date endDate){
        List<PhaseDTO> phases = phaseService.getApplicablePhasesByOrganizationId(unitId);
        Map<BigInteger,String> phaseIdAndNameMap = new HashMap<>();
        for(PhaseDTO phase : phases){
            phaseIdAndNameMap.put(phase.getId(), phase.getName());
        }
        List<PlanningPeriodDTO> planningPeriods = null;
        if(Optional.ofNullable(startDate).isPresent() && Optional.ofNullable(endDate).isPresent()){
            planningPeriods = planningPeriodMongoRepository.findPeriodsOfUnitByStartAndEndDate(unitId, startDate, endDate);
        } else {
            planningPeriods = planningPeriodMongoRepository.findAllPeriodsOfUnit(unitId);
        }

        for(PlanningPeriodDTO planningPeriod : planningPeriods){
            planningPeriod.setStartDateMillis(planningPeriod.getStartDate().getTime());
            planningPeriod.setEndDateMillis(planningPeriod.getEndDate().getTime());

            // Get duration of period
            Period period = Period.between(planningPeriod.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    planningPeriod.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            planningPeriod.setPeriodDuration(""+ (period.getMonths()>0 ? period.getMonths()+ " MONTHS " : "")+period.getDays()/7+" WEEKS");

            // Set flippind dates
            for(PeriodPhaseFlippingDateDTO flippingDate : planningPeriod.getPhaseFlippingDate()){
                String phaseName = phaseIdAndNameMap.get(flippingDate.getPhaseId());
                switch (phaseName){
                    case "DRAFT":{
                        planningPeriod.setConstructionToDraftDate(flippingDate.getFlippingDate().getTime());
                        break;
                    }
                    case "CONSTRUCTION":{
                        planningPeriod.setPuzzleToConstructionDate(flippingDate.getFlippingDate().getTime());
                        break;
                    }
                    case "PUZZLE":{
                        planningPeriod.setRequestToPuzzleDate(flippingDate.getFlippingDate().getTime());
                        break;
                    }
                }
            }
        }
        return planningPeriods;
    }

    public PeriodPhaseAndFlippingDateDTO getPeriodsPhaseFlippingDateByDates(Date startDate, Date endDate, List<PhaseDTO> phases){
        List<PeriodPhaseFlippingDateDTO> tempPhaseFlippingDate = new ArrayList<>();

        BigInteger currentPhaseId = null;
        BigInteger nextPhaseId = null;
        if(Optional.ofNullable(phases).isPresent()){

            Date tempFlippingDate = endDate;
            boolean scopeToFlipNextPhase =true;
            BigInteger previousPhaseId = null;
            for(PhaseDTO phase : phases){
                // Check if duration of period is enough to assign next flipping
                tempFlippingDate = addDaysInDate(tempFlippingDate, -phase.getDurationInDays(), DurationType.DAYS, 1);
                if (scopeToFlipNextPhase && startDate.compareTo(tempFlippingDate) >= 0){
                    currentPhaseId = phase.getId();
                    nextPhaseId = previousPhaseId;
                    scopeToFlipNextPhase = false;
                }
                previousPhaseId = phase.getId();
                // Calculate flipping date by duration
                PeriodPhaseFlippingDateDTO periodPhaseFlippingDate = new PeriodPhaseFlippingDateDTO(phase.getId(), scopeToFlipNextPhase ? tempFlippingDate : null);
                tempPhaseFlippingDate.add(periodPhaseFlippingDate);
            }
        }
        PeriodPhaseAndFlippingDateDTO periodPhaseAndFlippingDateDTO = new PeriodPhaseAndFlippingDateDTO(currentPhaseId, nextPhaseId, tempPhaseFlippingDate);
        return periodPhaseAndFlippingDateDTO;
    }

    public List<PeriodPhaseFlippingDate> convertPeriodPhaseFlippingDTO(List<PeriodPhaseFlippingDateDTO> periodPhaseFlippingDateDTO){
        List<PeriodPhaseFlippingDate> periodPhaseFlippingDates = new ArrayList<>();
        PeriodPhaseFlippingDate tempPhaseFlippingDate = new PeriodPhaseFlippingDate();
        for(PeriodPhaseFlippingDateDTO flippingDateDTO : periodPhaseFlippingDateDTO){
            tempPhaseFlippingDate.setFlippingDate(flippingDateDTO.getFlippingDate());
            tempPhaseFlippingDate.setPhaseId(flippingDateDTO.getPhaseId());
            periodPhaseFlippingDates.add(tempPhaseFlippingDate);
        }
        return periodPhaseFlippingDates;
    }

    public void updateDatesAndFlippingDateOfPeriod(BigInteger periodId, Date startDate, Date endDate, List<PhaseDTO> phases){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);
        PeriodPhaseAndFlippingDateDTO periodPhaseAndFlippingDateDTO = getPeriodsPhaseFlippingDateByDates(startDate, endDate, phases);
        planningPeriod.setPhaseFlippingDate(convertPeriodPhaseFlippingDTO(periodPhaseAndFlippingDateDTO.getPhaseFlippingDate()));
        planningPeriod.setCurrentPhaseId(periodPhaseAndFlippingDateDTO.getCurrentPhaseId());
        planningPeriod.setNextPhaseId(periodPhaseAndFlippingDateDTO.getNextPhaseId());
        save(planningPeriod);
    }

    public List<PlanningPeriodDTO> updatePeriod(Long unitId, BigInteger periodId, PlanningPeriodDTO planningPeriodDTO){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);

        //TODO check if deifference of date is zero
        planningPeriodDTO.setStartDate(new Date(planningPeriodDTO.getStartDateMillis()));
        planningPeriodDTO.setEndDate(new Date(planningPeriodDTO.getEndDateMillis()));
        if(planningPeriodDTO.getStartDate().compareTo(planningPeriod.getEndDate()) < 0 ){
            throw new ActionNotPermittedException("Not a valid duration for a period ");
        }

        //Check if startDate and endDate is different from the original one
        boolean mergeToPrevious = planningPeriodDTO.getStartDate().compareTo(planningPeriod.getStartDate()) == 0 ? false : true;
        boolean mergeToNext = planningPeriodDTO.getEndDate().compareTo(planningPeriod.getEndDate()) == 0 ? false : true;
        if(mergeToPrevious || mergeToNext){
            // Check if period is in request phase
            // We are checking request phase by its name, can be done by sequence, need to ask
            if(!phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
                throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
            }
        }


        // Fetch previous and next planning periods
        PlanningPeriod previousPeriod = new PlanningPeriod();
        PlanningPeriod nextPeriod = new PlanningPeriod();

        if(mergeToPrevious){
            previousPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(unitId, planningPeriodDTO.getStartDate());
        }
        if(mergeToNext){
            nextPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(unitId, planningPeriodDTO.getEndDate());
        }

        Date startDateOfPeriodToBeDeleted = previousPeriod.getStartDate();
        Date endDateOfPeriodToBeDeleted = nextPeriod.getEndDate();

        // TODO delete Periods between dates startDateOfPeriodToBeDeleted and endDateOfPeriodToBeDeleted
        planningPeriodMongoRepository.deletePlanningPeriodLiesBetweenDates(unitId, startDateOfPeriodToBeDeleted, endDateOfPeriodToBeDeleted);

        PlanningPeriodDTO tempPlanningPeriodDTO = null;
        List<PhaseDTO> phases = phaseService.getApplicablePhasesByOrganizationId(unitId);


        if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) != 0){
            // Create 2 Period with end date (planningPeriodDTO.getEndDate())

            tempPlanningPeriodDTO = new PlanningPeriodDTO(startDateOfPeriodToBeDeleted.getTime(),
                    startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate())
                    , DurationType.DAYS, 1);
            addPeriod(unitId,startDateOfPeriodToBeDeleted, phases,tempPlanningPeriodDTO );

            tempPlanningPeriodDTO = new PlanningPeriodDTO(planningPeriodDTO.getStartDate().getTime(),
                    planningPeriodDTO.getStartDate().compareTo(endDateOfPeriodToBeDeleted)
                    , DurationType.DAYS, 1);
            addPeriod(unitId,planningPeriodDTO.getStartDate(), phases,tempPlanningPeriodDTO );

        }

        /*if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) != 0){
            // Create 2 Period with end date (planningPeriodDTO.getEndDate())

            updateDatesAndFlippingDateOfPeriod(previousPeriod.getId(), startDateOfPeriodToBeDeleted, planningPeriodDTO.getStartDate(), phases);
            updateDatesAndFlippingDateOfPeriod(periodId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate(), phases);

        }*/


        if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) == 0){
            // Create Period with end date (planningPeriodDTO.getEndDate())

            tempPlanningPeriodDTO = new PlanningPeriodDTO(planningPeriodDTO.getStartDate().getTime(),
                    planningPeriodDTO.getStartDate().compareTo(endDateOfPeriodToBeDeleted)
                    , DurationType.DAYS, 1);
            addPeriod(unitId,planningPeriodDTO.getStartDate(), phases,tempPlanningPeriodDTO );
        }

        /*if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) == 0){
            // Create Period with end date (planningPeriodDTO.getEndDate())

            updateDatesAndFlippingDateOfPeriod(previousPeriod.getId(), startDateOfPeriodToBeDeleted, planningPeriodDTO.getStartDate(), phases);

            tempPlanningPeriodDTO = new PlanningPeriodDTO(planningPeriodDTO.getStartDate().getTime(),
                    planningPeriodDTO.getStartDate().compareTo(endDateOfPeriodToBeDeleted)
                    , DurationType.DAYS, 1);
            addPeriod(unitId,planningPeriodDTO.getStartDate(), phases,tempPlanningPeriodDTO );
        }*/

        if( planningPeriodDTO.getEndDate().compareTo(endDateOfPeriodToBeDeleted) != 0 ){
            // Create period from ( planningPeriodDTO.getEndDate() - endDateOfPeriodToBeDeleted )

            tempPlanningPeriodDTO = new PlanningPeriodDTO(planningPeriodDTO.getEndDate().getTime(),
                    planningPeriodDTO.getEndDate().compareTo(endDateOfPeriodToBeDeleted)
                    , DurationType.DAYS, 1);
            addPeriod(unitId,planningPeriodDTO.getEndDate(), phases,tempPlanningPeriodDTO );

        }

        // TODO Fetch periods from start date and end date and return
        return getPeriods(unitId, startDateOfPeriodToBeDeleted, endDateOfPeriodToBeDeleted);

    }

    public boolean deletePeriod(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);
        if(Optional.ofNullable(planningPeriod).isPresent()){
            throw new DataNotFoundByIdException("Period does not Exists Id " + periodId);
        }

        // Check if period is in request phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
        }


        // TODO Check if it is last period in Request Phase
        /*if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
        }*/
        planningPeriod.setDeleted(true);
        save(planningPeriod);
        return true;
    }

}
