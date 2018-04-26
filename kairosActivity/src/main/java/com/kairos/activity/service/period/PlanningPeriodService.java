package com.kairos.activity.service.period;

import com.kairos.activity.client.OrganizationRestClient;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.period.PeriodPhaseFlippingDate;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.util.DateUtils;
import com.kairos.persistence.model.enums.DurationType;
import com.kairos.response.dto.web.period.PeriodPhaseFlippingDateDTO;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
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

    @Inject
    OrganizationRestClient organizationRestClient;

    public Date addDaysInDate(Date date,  int duration, DurationType durationType, int recurringNumber, int millis){
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
        cal.add(Calendar.MILLISECOND, millis);
        return cal.getTime();
    }

    public void createPlanningPeriod(Long unitId, Date startDate, List<PhaseDTO> applicablePhases, PlanningPeriodDTO planningPeriodDTO){
        //  Calculate END Date
        Date endDate = addDaysInDate(startDate, planningPeriodDTO.getDuration(),
                planningPeriodDTO.getDurationType(),1,-1);
        // If End Date is greater than end date of last period (from DTO) and then return
        if (endDate.compareTo(planningPeriodDTO.getEndDate()) > 0){
            return;
        }

        BigInteger currentPhaseId = null;
        BigInteger nextPhaseId = null;

        List<PeriodPhaseFlippingDate> tempPhaseFlippingDate = new ArrayList<>();
        if(Optional.ofNullable(applicablePhases).isPresent()){

            Date tempFlippingDate = endDate;
            boolean scopeToFlipNextPhase =true;
            BigInteger previousPhaseId = null;
            int index = 0;
            for(PhaseDTO phase : applicablePhases){

                // Check if duration of period is enough to assign next flipping
                tempFlippingDate = addDaysInDate(tempFlippingDate, -phase.getDurationInDays(), DurationType.DAYS, 1,1);
                if (applicablePhases.size()==index+1 || (scopeToFlipNextPhase && DateUtils.getDate().compareTo(tempFlippingDate) >= 0)){
                    if(scopeToFlipNextPhase){
                        currentPhaseId = phase.getId();
                        nextPhaseId = previousPhaseId;
                    }
                    scopeToFlipNextPhase = false;
                }
                previousPhaseId = phase.getId();
                 // Calculate flipping date by duration
                 PeriodPhaseFlippingDate periodPhaseFlippingDate = new PeriodPhaseFlippingDate(phase.getId(), scopeToFlipNextPhase ? tempFlippingDate : null);
                tempPhaseFlippingDate.add(periodPhaseFlippingDate);
                index +=1;
            }
        }

        // Set name of period dynamically
        String name = DateUtils.getDateStringByTimeZone(startDate,planningPeriodDTO.getZoneId(), "dd.MMM.yyyy")+ "_" +
                DateUtils.getDateStringByTimeZone(endDate,planningPeriodDTO.getZoneId(), "dd.MMM.yyyy");
        PlanningPeriod planningPeriod = new PlanningPeriod(name, startDate, endDate, unitId, tempPhaseFlippingDate, currentPhaseId, nextPhaseId);
        save(planningPeriod);
        createPlanningPeriod(unitId, addDaysInDate(endDate, 0,
                planningPeriodDTO.getDurationType(),1,1), applicablePhases, planningPeriodDTO);
    }

    public List<PhaseDTO> getPhasesWithDurationInDays(Long unitId){
        List<PhaseDTO> phases = phaseService.getApplicablePhasesByOrganizationId(unitId);
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
        return phases;
    }

    public List<PlanningPeriodDTO> addPlanningPeriods(Long unitId, PlanningPeriodDTO planningPeriodDTO) {
        // TODO Check monday if duration is in week and first day of month if duration is in month
        List<PhaseDTO> phases = getPhasesWithDurationInDays(unitId);;
        if(!Optional.ofNullable(phases).isPresent()){
            throw new DataNotFoundByIdException("No Phases found in the Organization " + unitId);
        }

        //Set Start Date and End date in PlanningPeriodDTO according to recurringNumber
        planningPeriodDTO.setStartDate(DateUtils.getDate(planningPeriodDTO.getStartDateMillis()));
        planningPeriodDTO.setEndDate(
                addDaysInDate(planningPeriodDTO.getStartDate(),
                        planningPeriodDTO.getDuration(), planningPeriodDTO.getDurationType(), planningPeriodDTO.getRecurringNumber(), -1 ) );

        PlanningPeriod firstPlanningPeriod = planningPeriodMongoRepository.getFirstPlanningPeriod(unitId);
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);


        // Check if any period already exist in the given period

        // TODO period can't be created in past

        /*if(planningPeriodDTO.getStartDate().before(DateUtils.getDate())){
            throw new ActionNotPermittedException("Period can not be created in past date");
        }*/
        // TODO create generic method
        if(Optional.ofNullable(firstPlanningPeriod).isPresent() &&
                (((planningPeriodDTO.getStartDate().after(firstPlanningPeriod.getStartDate()) || planningPeriodDTO.getStartDate().equals(firstPlanningPeriod.getStartDate()) ) &&
                        planningPeriodDTO.getStartDate().before(lastPlanningPeriod.getEndDate()) )  ||
                        ((planningPeriodDTO.getEndDate().before(lastPlanningPeriod.getEndDate()) || planningPeriodDTO.getEndDate().equals(lastPlanningPeriod.getEndDate()) ) &&
                                planningPeriodDTO.getEndDate().after(firstPlanningPeriod.getStartDate())))  ){
            throw new ActionNotPermittedException("Period already exist between given dates ");
        }

        if(Optional.ofNullable(firstPlanningPeriod).isPresent() && !planningPeriodDTO.getEndDate().before(firstPlanningPeriod.getStartDate()) && firstPlanningPeriod.getEndDate().getTime() - planningPeriodDTO.getStartDate().getTime() > 1){
            throw new ActionNotPermittedException("Invalid duration for period to be created");
        }

        if(Optional.ofNullable(firstPlanningPeriod).isPresent() && planningPeriodDTO.getStartDate().after(lastPlanningPeriod.getEndDate()) && planningPeriodDTO.getStartDate().getTime() - lastPlanningPeriod.getEndDate().getTime() > 1){
            throw new ActionNotPermittedException("Invalid start date for period to be created");
        }

        planningPeriodDTO.setZoneId(getTimeZoneOfOrganization(unitId));
        createPlanningPeriod(unitId, planningPeriodDTO.getStartDate(), phases, planningPeriodDTO);
        return getPlanningPeriods(unitId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate());
    }

    // To get days difference of two dates
    // Note : If millis are exceeding then it will be considered as a day
    public int getDifferenceInDates(Date endDate, Date startDate){

        int diffInDays = (int) ( (endDate.getTime() - startDate.getTime())
                / (1000 * 60 * 60 * 24) )   ;
        int leftMillis = (int) ( (endDate.getTime() - startDate.getTime())
                % (1000 * 60 * 60 * 24) )   ;

        if(leftMillis > 0){
            diffInDays+= 1;
        }
        return diffInDays;
    }

    public String getDurationOfTwoDates(Date startDate, Date endDate){
        // Get duration of period
        Period period = Period.between(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return ""+ (period.getMonths()>0 ? period.getMonths()+ " MONTHS " : "")+
                (period.getDays()>=7 ? period.getDays()/7+ " WEEKS " : "")+
                (period.getDays()%7 >0 ? period.getDays() %7+ " DAYS " : "") ;
    }

    // Prepare map for phases with id as key and sequence as value
    public Map<BigInteger,Integer> getMapOfPhasesIdAndSequence(List<PhaseDTO> phases){
        Map<BigInteger,Integer> phaseIdAndSequenceMap = new HashMap<>();
        for(PhaseDTO phase : phases){
            phaseIdAndSequenceMap.put(phase.getId(), phase.getSequence());
        }
        return phaseIdAndSequenceMap;
    }

    public List<PlanningPeriodDTO> getPlanningPeriods(Long unitId, Date startDate, Date endDate){
        List<PhaseDTO> phases = phaseService.getPhasesByUnit(unitId);

        // Prepare map for phases with id as key and sequence as value
        Map<BigInteger,Integer> phaseIdAndSequenceMap = getMapOfPhasesIdAndSequence(phases);

        // Fetch planning periods
        List<PlanningPeriodDTO> planningPeriods = null;
        if(Optional.ofNullable(startDate).isPresent() && Optional.ofNullable(endDate).isPresent()){
            planningPeriods = planningPeriodMongoRepository.findPeriodsOfUnitByStartAndEndDate(unitId, startDate, endDate);
        } else {
            planningPeriods = planningPeriodMongoRepository.findAllPeriodsOfUnit(unitId);
        }

        for(PlanningPeriodDTO planningPeriod : planningPeriods){

            // Set date millis
            planningPeriod.setStartDateMillis(planningPeriod.getStartDate().getTime());
            planningPeriod.setEndDateMillis(planningPeriod.getEndDate().getTime());

            // Set duration of period
            planningPeriod.setPeriodDuration(getDurationOfTwoDates(planningPeriod.getStartDate(), planningPeriod.getEndDate()));

            // Set flippind dates
            for(PeriodPhaseFlippingDateDTO flippingDate : planningPeriod.getPhaseFlippingDate()){
                int phaseSequence = phaseIdAndSequenceMap.get(flippingDate.getPhaseId());
                switch (phaseSequence){
                    case 4:{
                        planningPeriod.setConstructionToDraftDate(Optional.ofNullable(flippingDate.getFlippingDate()).isPresent() ? flippingDate.getFlippingDate().getTime(): null);
                        break;
                    }
                    case 3:{
                        planningPeriod.setPuzzleToConstructionDate(Optional.ofNullable(flippingDate.getFlippingDate()).isPresent() ? flippingDate.getFlippingDate().getTime(): null);
                        break;
                    }
                    case 2:{
                        planningPeriod.setRequestToPuzzleDate(Optional.ofNullable(flippingDate.getFlippingDate()).isPresent() ? flippingDate.getFlippingDate().getTime(): null);
                        break;
                    }
                }
            }
        }
        return planningPeriods;
    }

    public void addPlanningPeriodOnUpdate(Long unitId, Date endDate, Date startDate, List<PhaseDTO> phases, ZoneId zoneId){
        int duration = getDifferenceInDates(endDate, startDate);
        PlanningPeriodDTO tempPlanningPeriodDTO = new PlanningPeriodDTO(startDate.getTime(),duration,
                 DurationType.DAYS, 1, endDate, zoneId);
        createPlanningPeriod(unitId,startDate, phases,tempPlanningPeriodDTO );
    }

    public boolean validateFlippingDateByStartAndEndDate(Date flippingDate, Date startDate, Date endDate){
        if((flippingDate.after(startDate) || flippingDate.equals(startDate)) &&
                (flippingDate.before(endDate) || flippingDate.equals(endDate)) ){
            return true;
        }
        return false;
    }

    public PlanningPeriod updatePhaseFlippingDateOfPeriod(PlanningPeriod planningPeriod, PlanningPeriodDTO planningPeriodDTO, Long unitId){
        List<PeriodPhaseFlippingDate>  phaseFlippingDateList = planningPeriod.getPhaseFlippingDate();
        List<PhaseDTO> phases = phaseService.getPhasesByUnit(unitId);
        Map<BigInteger,Integer> phasesMap = getMapOfPhasesIdAndSequence(phases);

        for(PeriodPhaseFlippingDate phaseFlippingDate : phaseFlippingDateList){
            switch (phasesMap.get(phaseFlippingDate.getPhaseId())){
                case 4:{
                    phaseFlippingDate.setFlippingDate(DateUtils.getDate(planningPeriodDTO.getConstructionToDraftDate()));
                    break;
                }
                case 3:{
                    phaseFlippingDate.setFlippingDate(DateUtils.getDate(planningPeriodDTO.getPuzzleToConstructionDate()));
                    break;
                }
                case 2:{
                    phaseFlippingDate.setFlippingDate(DateUtils.getDate(planningPeriodDTO.getRequestToPuzzleDate()));
                    break;
                }
            }
        }
//        phaseFlippingDateList.get(0).setFlippingDate(DateUtils.getDate(planningPeriodDTO.getConstructionToDraftDate()));
//        phaseFlippingDateList.get(1).setFlippingDate(DateUtils.getDate(planningPeriodDTO.getPuzzleToConstructionDate()));
//        phaseFlippingDateList.get(2).setFlippingDate(DateUtils.getDate(planningPeriodDTO.getRequestToPuzzleDate()));
        // Validation on flipping date, it is being done on front end, can be used in future
        /*if(validateFlippingDateByStartAndEndDate(DateUtils.getDate(planningPeriodDTO.getConstructionToDraftDate()),
                phaseFlippingDate.get(1).getFlippingDate(), planningPeriod.getEndDate())){
            phaseFlippingDate.get(0).setFlippingDate(DateUtils.getDate(planningPeriodDTO.getConstructionToDraftDate()));
        } else {
            throw new ActionNotPermittedException("Not a valid Construction To Draft flipping date");
        }

        if(validateFlippingDateByStartAndEndDate(DateUtils.getDate(planningPeriodDTO.getPuzzleToConstructionDate()),
                phaseFlippingDate.get(2).getFlippingDate(), phaseFlippingDate.get(0).getFlippingDate())){
            phaseFlippingDate.get(1).setFlippingDate(DateUtils.getDate(planningPeriodDTO.getPuzzleToConstructionDate()));
        } else {
            throw new ActionNotPermittedException("Not a valid Puzzle To Construction flipping date");
        }

        if(validateFlippingDateByStartAndEndDate(DateUtils.getDate(planningPeriodDTO.getRequestToPuzzleDate()),
                planningPeriod.getStartDate(), phaseFlippingDate.get(1).getFlippingDate())){
            phaseFlippingDate.get(2).setFlippingDate(DateUtils.getDate(planningPeriodDTO.getRequestToPuzzleDate()));
        } else {
            throw new ActionNotPermittedException("Not a valid Request To Puzzle flipping date");
        }*/
        return planningPeriod;
    }

    public ZoneId getTimeZoneOfOrganization(Long unitId){
        ZoneId unitZoneId = organizationRestClient.getOrganizationTimeZone(unitId);
        if(!Optional.ofNullable(unitZoneId).isPresent() ){
            throw new ActionNotPermittedException("Time Zone is not set for the unit");
        }
        return unitZoneId;
    }


    public List<PlanningPeriodDTO> updatePlanningPeriod(Long unitId, BigInteger periodId, PlanningPeriodDTO planningPeriodDTO){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            throw new DataNotFoundByIdException("No Period found in the Organization by Id " + periodId);
        }

        planningPeriodDTO.setStartDate(DateUtils.getDate(planningPeriodDTO.getStartDateMillis()));
        planningPeriodDTO.setEndDate(DateUtils.getDate(planningPeriodDTO.getEndDateMillis() ));

        // If start date is equal to end date or end date is less than start date
        if(planningPeriodDTO.getStartDate().compareTo(planningPeriodDTO.getEndDate()) >= 0 ){
            throw new ActionNotPermittedException("Not a valid duration for a period ");
        }

        planningPeriodDTO.setZoneId(getTimeZoneOfOrganization(unitId));

        // Check if period is in request phase (Changes for start date and end date can be done in Request Phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // TO DO check phase by sequence
        if(!phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
        }

        //Check if startDate and endDate is different from the original one
        if(planningPeriodDTO.getStartDate().compareTo(planningPeriod.getStartDate()) == 0 &&
                planningPeriodDTO.getEndDate().compareTo(planningPeriod.getEndDate()) == 0){
            //If No change in date

            planningPeriod = updatePhaseFlippingDateOfPeriod(planningPeriod, planningPeriodDTO, unitId);
            planningPeriod.setName(planningPeriodDTO.getName());
            save(planningPeriod);
            return getPlanningPeriods(unitId, planningPeriod.getStartDate(), planningPeriod.getEndDate());
        }

        // Fetch previous and next planning periods
        PlanningPeriod previousPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(unitId, planningPeriodDTO.getStartDate());
        PlanningPeriod nextPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(unitId, planningPeriodDTO.getEndDate());

        Date startDateOfPeriodToBeDeleted = Optional.ofNullable(previousPeriod).isPresent() ? previousPeriod.getStartDate() : planningPeriod.getStartDate() ;
        Date endDateOfPeriodToBeDeleted = Optional.ofNullable(nextPeriod).isPresent() ? nextPeriod.getEndDate() : planningPeriodDTO.getEndDate();

        // Delete Periods between dates startDateOfPeriodToBeDeleted and endDateOfPeriodToBeDeleted
        planningPeriodMongoRepository.deletePlanningPeriodLiesBetweenDates(unitId, startDateOfPeriodToBeDeleted, endDateOfPeriodToBeDeleted);

        List<PhaseDTO> phases = getPhasesWithDurationInDays(unitId);

        if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) != 0){
            // Create 2 Period with end date (planningPeriodDTO.getEndDate())
            addPlanningPeriodOnUpdate(unitId, planningPeriodDTO.getStartDate(), startDateOfPeriodToBeDeleted, phases, planningPeriodDTO.getZoneId());
            addPlanningPeriodOnUpdate(unitId, planningPeriodDTO.getEndDate(), planningPeriodDTO.getStartDate(), phases, planningPeriodDTO.getZoneId());
        }

        if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) == 0) {
            // Create Period with end date (planningPeriodDTO.getEndDate())
            addPlanningPeriodOnUpdate(unitId, planningPeriodDTO.getEndDate(), planningPeriodDTO.getStartDate(), phases, planningPeriodDTO.getZoneId());
        }

        if( planningPeriodDTO.getEndDate().compareTo(endDateOfPeriodToBeDeleted) != 0 ){
            // Create period from ( planningPeriodDTO.getEndDate() - endDateOfPeriodToBeDeleted )
            addPlanningPeriodOnUpdate(unitId, endDateOfPeriodToBeDeleted, DateUtils.getDate(planningPeriodDTO.getEndDateMillis() +1), phases, planningPeriodDTO.getZoneId());
        }

        // Fetch periods from start date and end date and return
        return getPlanningPeriods(unitId, startDateOfPeriodToBeDeleted, endDateOfPeriodToBeDeleted);
    }

    // To delete planning period
    public boolean deletePlanningPeriod(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            throw new DataNotFoundByIdException("No Period found in the Unit by Id " + periodId);
        }

        // Check if it is last period
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);

        if( !lastPlanningPeriod.getId().equals(planningPeriod.getId())){
            throw new ActionNotPermittedException("Only last period can be deleted");
        }

        // Check if period is in request phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // TO DO check phase by sequence
        if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
        }

        planningPeriod.setDeleted(true);
        save(planningPeriod);
        return true;
    }

    // To update phase of period to the next one
    public PlanningPeriodDTO setPlanningPeriodPhaseToNext(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            throw new DataNotFoundByIdException("No Period found in the Unit by Id " + periodId);
        }
        if(!Optional.ofNullable(planningPeriod.getNextPhaseId()).isPresent()){
            throw new ActionNotPermittedException("Period is in last phase.");
        }
        Phase initialNextPhase = phaseMongoRepository.findOne(planningPeriod.getNextPhaseId());
        List<PhaseDTO> toBeNextPhase = phaseMongoRepository.getNextApplicablePhasesOfUnitBySequence(unitId, initialNextPhase.getSequence());
        planningPeriod.setCurrentPhaseId(initialNextPhase.getId());
        planningPeriod.setNextPhaseId(Optional.ofNullable(toBeNextPhase).isPresent() && toBeNextPhase.size()>0 ? toBeNextPhase.get(0).getId() : null);
        save(planningPeriod);

        return getPlanningPeriods(unitId, planningPeriod.getStartDate(), planningPeriod.getEndDate()).get(0);
    }

    public boolean updateFlippingDate(BigInteger periodId, Long unitId, Date date){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);
        boolean updateCurrentAndNextPhases = false;
        BigInteger nextPhaseId = null;
        for(PeriodPhaseFlippingDate phaseFlippingDate : planningPeriod.getPhaseFlippingDate()){

            if(planningPeriod.getNextPhaseId().equals(phaseFlippingDate.getPhaseId()) ){
                if(phaseFlippingDate.getFlippingDate().compareTo(date) <= 0){
                    updateCurrentAndNextPhases = true;
                }
                break;
            }
            nextPhaseId = phaseFlippingDate.getPhaseId();
        }
        if(updateCurrentAndNextPhases){
            planningPeriod.setCurrentPhaseId(planningPeriod.getNextPhaseId());
            planningPeriod.setNextPhaseId(nextPhaseId);
            save(planningPeriod);
        }
        return true;
    }

    /**
     * Not in use, Can be used in future
     */
    /*public PeriodPhaseAndFlippingDateDTO getPeriodsPhaseFlippingDateByDates(Date startDate, Date endDate, List<PhaseDTO> phases){
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
    }*/
}
