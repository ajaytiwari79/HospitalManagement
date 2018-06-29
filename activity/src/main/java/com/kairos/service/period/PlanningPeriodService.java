package com.kairos.service.period;

import com.kairos.rest_client.OrganizationRestClient;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.period.PeriodPhaseFlippingDate;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.util.DateUtils;
import com.kairos.enums.DurationType;
import com.kairos.activity.period.PeriodPhaseFlippingDateDTO;
import com.kairos.activity.period.PlanningPeriodDTO;
import com.kairos.activity.phase.PhaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
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
    @Inject
    ExceptionService exceptionService;

    // To get list of phases with duration in days
    public List<PhaseDTO> getPhasesWithDurationInDays(Long unitId){
        List<PhaseDTO> phases = phaseService.getApplicablePlanningPhasesByOrganizationId(unitId);
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

    // Prepare map for phases with id as key and sequence as value
    public Map<BigInteger,Integer> getMapOfPhasesIdAndSequence(List<PhaseDTO> phases){
        Map<BigInteger,Integer> phaseIdAndSequenceMap = new HashMap<>();
        for(PhaseDTO phase : phases){
            phaseIdAndSequenceMap.put(phase.getId(), phase.getSequence());
        }
        return phaseIdAndSequenceMap;
    }

    // To fetch list of planning periods
    public List<PlanningPeriodDTO> getPlanningPeriods(Long unitId, LocalDate startDate, LocalDate endDate){
        List<PhaseDTO> phases = phaseService.getPlanningPhasesByUnit(unitId);

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

            // Set duration of period
            planningPeriod.setPeriodDuration(DateUtils.getDurationOfTwoLocalDates(planningPeriod.getStartDate(), planningPeriod.getEndDate().plusDays(1)));

            // Set flippind dates
            for(PeriodPhaseFlippingDateDTO flippingDate : planningPeriod.getPhaseFlippingDate()){
                int phaseSequence = phaseIdAndSequenceMap.get(flippingDate.getPhaseId());
                switch (phaseSequence){
                    case 4:{
                        planningPeriod.setConstructionToDraftDate(Optional.ofNullable(flippingDate.getFlippingDate()).isPresent() ? flippingDate.getFlippingDate(): null);
                        break;
                    }
                    case 3:{
                        planningPeriod.setPuzzleToConstructionDate(Optional.ofNullable(flippingDate.getFlippingDate()).isPresent() ? flippingDate.getFlippingDate(): null);
                        break;
                    }
                    case 2:{
                        planningPeriod.setRequestToPuzzleDate(Optional.ofNullable(flippingDate.getFlippingDate()).isPresent() ? flippingDate.getFlippingDate(): null);
                        break;
                    }
                }
            }
        }
        return planningPeriods;
    }

    // To create Planning Period object and to save the list
    public void createPlanningPeriod(Long unitId, LocalDate startDate, List<PlanningPeriod> planningPeriods, List<PhaseDTO> applicablePhases, PlanningPeriodDTO planningPeriodDTO, int recurringNumber){
        //  Calculate END Date
        LocalDate endDate = DateUtils.addDurationInLocalDateExcludingLastDate(startDate, planningPeriodDTO.getDuration(),
                planningPeriodDTO.getDurationType(),1);

        BigInteger currentPhaseId = null;
        BigInteger nextPhaseId = null;

        List<PeriodPhaseFlippingDate> tempPhaseFlippingDate = new ArrayList<>();
        if(Optional.ofNullable(applicablePhases).isPresent()){

            LocalDate tempFlippingDate = startDate;
            boolean scopeToFlipNextPhase =true;
            BigInteger previousPhaseId = null;
            int index = 0;


            for(PhaseDTO phase : applicablePhases){
                // Check if duration of period is enough to assign next flipping
                tempFlippingDate = DateUtils.addDurationInLocalDate(tempFlippingDate, -phase.getDurationInDays(), DurationType.DAYS, 1);
                // DateUtils.getDate().compareTo(tempFlippingDate) >= 0
                if (applicablePhases.size() == index+1 || (scopeToFlipNextPhase && DateUtils.getLocalDateFromDate(DateUtils.getDate()).isAfter(tempFlippingDate)) ){
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
        String name = DateUtils.formatLocalDate(startDate, "dd.MMM.yyyy")+ "  " +DateUtils.formatLocalDate(endDate, "dd.MMM.yyyy");
        PlanningPeriod planningPeriod = new PlanningPeriod(name, startDate,
                endDate, unitId, tempPhaseFlippingDate, currentPhaseId, nextPhaseId);

        // Add planning period object in list
        planningPeriods.add(planningPeriod);
        if(recurringNumber > 1){
            createPlanningPeriod(unitId,
                    DateUtils.addDurationInLocalDate(startDate, planningPeriodDTO.getDuration(),planningPeriodDTO.getDurationType(),1),
                    planningPeriods, applicablePhases, planningPeriodDTO,--recurringNumber);
        }

    }

    public List<PlanningPeriodDTO> addPlanningPeriods(Long unitId, PlanningPeriodDTO planningPeriodDTO) {

        // TODO Check monday if duration is in week and first day of month if duration is in month
        List<PhaseDTO> phases = getPhasesWithDurationInDays(unitId);;
        if(!Optional.ofNullable(phases).isPresent()){
            exceptionService.dataNotFoundByIdException("message.organization.phases", unitId);
        }

        //Set Start Date and End date in PlanningPeriodDTO according to recurringNumber
        planningPeriodDTO.setStartDate(planningPeriodDTO.getStartDate());
        LocalDate endDate = DateUtils.addDurationInLocalDate(planningPeriodDTO.getStartDate(),planningPeriodDTO.getDuration(),
                planningPeriodDTO.getDurationType(), planningPeriodDTO.getRecurringNumber());

        // period can't be created in past
        if(DateUtils.getLocalDateFromDate(DateUtils.getDate()).isAfter(planningPeriodDTO.getStartDate())){
            exceptionService.actionNotPermittedException("error.period.past.date.creation");
        }

        // Check if any period already exist in the given period
        if(planningPeriodMongoRepository.checkIfPeriodsExistsOrOverlapWithStartAndEndDate(unitId, planningPeriodDTO.getStartDate(), endDate)){
            exceptionService.actionNotPermittedException("message.period.invalid.startDate.or.duration");
        }

        List<PlanningPeriod> planningPeriods = new ArrayList<PlanningPeriod>(planningPeriodDTO.getRecurringNumber());
        createPlanningPeriod(unitId, planningPeriodDTO.getStartDate(),planningPeriods, phases, planningPeriodDTO, planningPeriodDTO.getRecurringNumber());
        save(planningPeriods);
        return getPlanningPeriods(unitId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate());
    }

    public boolean updateFlippingDate(BigInteger periodId, Long unitId, LocalDate date){
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


    public PlanningPeriod updatePhaseFlippingDateOfPeriod(PlanningPeriod planningPeriod, PlanningPeriodDTO planningPeriodDTO, Long unitId){
        List<PeriodPhaseFlippingDate>  phaseFlippingDateList = planningPeriod.getPhaseFlippingDate();
        List<PhaseDTO> phases = phaseService.getPlanningPhasesByUnit(unitId);
        Map<BigInteger,Integer> phasesMap = getMapOfPhasesIdAndSequence(phases);

        for(PeriodPhaseFlippingDate phaseFlippingDate : phaseFlippingDateList){
            switch (phasesMap.get(phaseFlippingDate.getPhaseId())){
                case 4:{
                    phaseFlippingDate.setFlippingDate(planningPeriodDTO.getConstructionToDraftDate());
                    break;
                }
                case 3:{
                    phaseFlippingDate.setFlippingDate(planningPeriodDTO.getPuzzleToConstructionDate());
                    break;
                }
                case 2:{
                    phaseFlippingDate.setFlippingDate(planningPeriodDTO.getRequestToPuzzleDate());
                    break;
                }
            }
        }
        return planningPeriod;
    }


    public List<PlanningPeriodDTO> updatePlanningPeriod(Long unitId, BigInteger periodId, PlanningPeriodDTO planningPeriodDTO){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.period.organization.notfound", periodId);
        }

        // If start date is equal to end date or end date is less than start date
        if(planningPeriodDTO.getStartDate().compareTo(planningPeriodDTO.getEndDate()) >= 0 ){
            exceptionService.actionNotPermittedException("message.period.invalid.duration");
        }

        // Check if period is in request phase (Changes for start date and end date can be done in Request Phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // We know here that sequence of request phase is 0
        if(!phaseMongoRepository.checkPhaseBySequence(planningPeriod.getCurrentPhaseId(), AppConstants.REQUEST_PHASE_SEQUENCE)){
            exceptionService.actionNotPermittedException("message.period.phase.request.name", planningPeriod.getName());
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

        // We know here that sequence of request phase is 0
        if(planningPeriodMongoRepository.checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(
                unitId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate(),AppConstants.REQUEST_PHASE_SEQUENCE)){
            exceptionService.actionNotPermittedException("message.period.phase.request.merge");
        }

        // Fetch previous and next planning periods
        PlanningPeriod previousPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(unitId, planningPeriodDTO.getStartDate());
        PlanningPeriod nextPeriod = planningPeriodMongoRepository.getPlanningPeriodContainsDate(unitId, planningPeriodDTO.getEndDate());

        LocalDate startDateOfPeriodToBeDeleted = Optional.ofNullable(previousPeriod).isPresent() ? previousPeriod.getStartDate() : planningPeriod.getStartDate() ;
        LocalDate endDateOfPeriodToBeDeleted = Optional.ofNullable(nextPeriod).isPresent() ? nextPeriod.getEndDate() : planningPeriodDTO.getEndDate();

        // Delete Periods between dates startDateOfPeriodToBeDeleted and endDateOfPeriodToBeDeleted
        planningPeriodMongoRepository.deletePlanningPeriodLiesBetweenDates(unitId, startDateOfPeriodToBeDeleted, endDateOfPeriodToBeDeleted);

        List<PhaseDTO> phases = getPhasesWithDurationInDays(unitId);

        List<PlanningPeriod> planningPeriodsToSave = new ArrayList<PlanningPeriod>(3);
        HashMap<LocalDate,LocalDate> mapOfDatesForPlanningPeriods = new HashMap<>();

        if(startDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getStartDate()) != 0){
            mapOfDatesForPlanningPeriods.put(startDateOfPeriodToBeDeleted, DateUtils.addDurationInLocalDate(planningPeriodDTO.getStartDate(), -1, DurationType.DAYS, 1));
        }

        mapOfDatesForPlanningPeriods.put(planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate());

        if(endDateOfPeriodToBeDeleted.compareTo(planningPeriodDTO.getEndDate()) != 0){
            mapOfDatesForPlanningPeriods.put(DateUtils.addDurationInLocalDate(planningPeriodDTO.getEndDate(), 1, DurationType.DAYS, 1), endDateOfPeriodToBeDeleted);
        }

        mapOfDatesForPlanningPeriods.forEach((startDate,endDate)->{
            addPlanningPeriodOnUpdate(unitId, endDate, startDate,phases, planningPeriodsToSave );
        });

        save(planningPeriodsToSave);

        // Fetch periods from start date and end date and return
        return getPlanningPeriods(unitId, startDateOfPeriodToBeDeleted, endDateOfPeriodToBeDeleted);
    }


    public void addPlanningPeriodOnUpdate(Long unitId, LocalDate endDate, LocalDate startDate, List<PhaseDTO> phases, List<PlanningPeriod> planningPeriods){
        if(endDate.compareTo(startDate) <= 0){
            return;
        }
        int duration = DateUtils.getDurationBetweenTwoLocalDatesIncludingLastDate(endDate, startDate, DurationType.DAYS).intValue();//Period.between(startDate, endDate).getDays();//endDate.compareTo(startDate);

        PlanningPeriodDTO tempPlanningPeriodDTO = new PlanningPeriodDTO(startDate,duration,
                DurationType.DAYS, 1, endDate);

        createPlanningPeriod(unitId, startDate, planningPeriods, phases, tempPlanningPeriodDTO, 1);
    }

    // To delete planning period
    public boolean deletePlanningPeriod(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.period.unit.id", periodId);
        }

        // Check if it is last period
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);

        if( !lastPlanningPeriod.getId().equals(planningPeriod.getId())){
            exceptionService.actionNotPermittedException("message.period.delete.last");
        }

        // Check if period is in request phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // TO DO check phase by sequence
        if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            exceptionService.actionNotPermittedException("message.period.phase.request.name" ,planningPeriod.getName());
        }

        planningPeriod.setDeleted(true);
        save(planningPeriod);
        return true;
    }

    public PlanningPeriodDTO setPlanningPeriodPhaseToNext(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.period.unit.id", periodId);
        }
        if(!Optional.ofNullable(planningPeriod.getNextPhaseId()).isPresent()){
            exceptionService.actionNotPermittedException("message.period.phase.last");
        }
        Phase initialNextPhase = phaseMongoRepository.findOne(planningPeriod.getNextPhaseId());
        List<PhaseDTO> toBeNextPhase = phaseMongoRepository.getNextApplicablePhasesOfUnitBySequence(unitId, initialNextPhase.getSequence());
        planningPeriod.setCurrentPhaseId(initialNextPhase.getId());
        planningPeriod.setNextPhaseId(Optional.ofNullable(toBeNextPhase).isPresent() && toBeNextPhase.size()>0 ? toBeNextPhase.get(0).getId() : null);
        save(planningPeriod);

        return getPlanningPeriods(unitId, planningPeriod.getStartDate(), planningPeriod.getEndDate()).get(0);
    }



        /*public Date addDaysInDate(Date date,  int duration, DurationType durationType, int recurringNumber, int millis){
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
    }*/

    /*public void createPlanningPeriod(Long unitId, Date startDate, List<PhaseDTO> applicablePhases, PlanningPeriodDTO planningPeriodDTO){
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
    }*/

    /*public LinkedHashMap<Integer, Integer> getPhasesWithSuccessiveDurationInDays(Long unitId){
        List<PhaseDTO> phases = phaseService.getApplicablePlanningPhasesByOrganizationId(unitId);
        LinkedHashMap<Integer, Integer> phasesMap = new LinkedHashMap<Integer, Integer>(4);
        int duration = 0;
        for(PhaseDTO phase : phases){
            switch (phase.getDurationType()){
                case DAYS:{
                    duration += phase.getDuration();
                    break;
                }
                case WEEKS:{
                    duration += phase.getDuration() * 7 ;
                    break;
                }
            }
            phasesMap.put(phase.getSequence(), duration);
        }
        return phasesMap;
    }*/

    /*public List<PlanningPeriodDTO> addPlanningPeriods(Long unitId, PlanningPeriodDTO planningPeriodDTO) {
        // TODO Check monday if duration is in week and first day of month if duration is in month
        List<PhaseDTO> phases = getPhasesWithDurationInDays(unitId);;
        if(!Optional.ofNullable(phases).isPresent()){
            exceptionService.dataNotFoundByIdException("message.organization.phases",unitId);
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

        *//*if(planningPeriodDTO.getStartDate().before(DateUtils.getDate())){
            throw new ActionNotPermittedException("Period can not be created in past date");
        }*//*
        // TODO create generic method
        if(Optional.ofNullable(firstPlanningPeriod).isPresent() &&
                (((planningPeriodDTO.getStartDate().after(firstPlanningPeriod.getStartDate()) || planningPeriodDTO.getStartDate().equals(firstPlanningPeriod.getStartDate()) ) &&
                        planningPeriodDTO.getStartDate().before(lastPlanningPeriod.getEndDate()) )  ||
                        ((planningPeriodDTO.getEndDate().before(lastPlanningPeriod.getEndDate()) || planningPeriodDTO.getEndDate().equals(lastPlanningPeriod.getEndDate()) ) &&
                                planningPeriodDTO.getEndDate().after(firstPlanningPeriod.getStartDate())))  ){
            exceptionService.actionNotPermittedException("message.period.date.alreadyexists");
        }

        if(Optional.ofNullable(firstPlanningPeriod).isPresent() && planningPeriodDTO.getEndDate().before(firstPlanningPeriod.getStartDate()) && firstPlanningPeriod.getStartDate().getTime() - planningPeriodDTO.getEndDate().getTime() > 1){
            exceptionService.actionNotPermittedException("message.period.duration");
        }

        if(Optional.ofNullable(firstPlanningPeriod).isPresent() && planningPeriodDTO.getStartDate().after(lastPlanningPeriod.getEndDate()) && planningPeriodDTO.getStartDate().getTime() - lastPlanningPeriod.getEndDate().getTime() > 1){
            exceptionService.actionNotPermittedException("message.period.date.start");
        }

        planningPeriodDTO.setZoneId(getTimeZoneOfOrganization(unitId));
        createPlanningPeriod(unitId, planningPeriodDTO.getStartDate(), phases, planningPeriodDTO);
        return getPlanningPeriods(unitId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate());
    }*/

    // To get days difference of two dates
    // Note : If millis are exceeding then it will be considered as a day
    /*public int getDifferenceInDates(Date endDate, Date startDate){

        int diffInDays = (int) ( (endDate.getTime() - startDate.getTime())
                / (1000 * 60 * 60 * 24) )   ;
        int leftMillis = (int) ( (endDate.getTime() - startDate.getTime())
                % (1000 * 60 * 60 * 24) )   ;

        if(leftMillis > 0){
            diffInDays+= 1;
        }
        return diffInDays;
    }*/

    /*public String getDurationOfTwoDates(Date startDate, Date endDate){
        // Get duration of period
        Period period = Period.between(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return ""+ (period.getMonths()>0 ? period.getMonths()+ " MONTHS " : "")+
                (period.getDays()>=7 ? period.getDays()/7+ " WEEKS " : "")+
                (period.getDays()%7 >0 ? period.getDays() %7+ " DAYS " : "") ;
    }*/

    /*public List<PlanningPeriodDTO> getPlanningPeriods(Long unitId, Date startDate, Date endDate){
        List<PhaseDTO> phases = phaseService.getPlanningPhasesByUnit(unitId);

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
    }*/

    /*public void addPlanningPeriodOnUpdate(Long unitId, Date endDate, Date startDate, List<PhaseDTO> phases, ZoneId zoneId){
        if(endDate.compareTo(startDate) <= 0){
            return;
        }
        int duration = getDifferenceInDates(endDate, startDate);
        PlanningPeriodDTO tempPlanningPeriodDTO = new PlanningPeriodDTO(startDate.getTime(),duration,
                 DurationType.DAYS, 1, endDate, zoneId);
        createPlanningPeriod(unitId,startDate, phases,tempPlanningPeriodDTO );
    }*/

    /*public boolean validateFlippingDateByStartAndEndDate(Date flippingDate, Date startDate, Date endDate){
        if((flippingDate.after(startDate) || flippingDate.equals(startDate)) &&
                (flippingDate.before(endDate) || flippingDate.equals(endDate)) ){
            return true;
        }
        return false;
    }*/

    /*public PlanningPeriod updatePhaseFlippingDateOfPeriod(PlanningPeriod planningPeriod, PlanningPeriodDTO planningPeriodDTO, Long unitId){
        List<PeriodPhaseFlippingDate>  phaseFlippingDateList = planningPeriod.getPhaseFlippingDate();
        List<PhaseDTO> phases = phaseService.getPlanningPhasesByUnit(unitId);
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
        *//*if(validateFlippingDateByStartAndEndDate(DateUtils.getDate(planningPeriodDTO.getConstructionToDraftDate()),
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
        }*//*
        return planningPeriod;
    }*/

    /*public ZoneId getTimeZoneOfOrganization(Long unitId){
        ZoneId unitZoneId = organizationRestClient.getOrganizationTimeZone(unitId);
        if(!Optional.ofNullable(unitZoneId).isPresent() ){
            exceptionService.actionNotPermittedException("message.unit.timezone");
        }
        return unitZoneId;
    }*/


    /*public List<PlanningPeriodDTO> updatePlanningPeriod(Long unitId, BigInteger periodId, PlanningPeriodDTO planningPeriodDTO){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.period.organization.notfound",periodId);
        }

        planningPeriodDTO.setStartDate(DateUtils.getDate(planningPeriodDTO.getStartDateMillis()));
        planningPeriodDTO.setEndDate(DateUtils.getDate(planningPeriodDTO.getEndDateMillis() ));

        // If start date is equal to end date or end date is less than start date
        if(planningPeriodDTO.getStartDate().compareTo(planningPeriodDTO.getEndDate()) >= 0 ){
            exceptionService.actionNotPermittedException("message.period.duration");
        }

        planningPeriodDTO.setZoneId(getTimeZoneOfOrganization(unitId));

        // Check if period is in request phase (Changes for start date and end date can be done in Request Phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // We know here that sequence of request phase is 0
        if(!phaseMongoRepository.checkPhaseBySequence(planningPeriod.getCurrentPhaseId(), AppConstants.REQUEST_PHASE_SEQUENCE)){
           exceptionService.actionNotPermittedException("message.period.phase.request.name",planningPeriod.getName());
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

        // We know here that sequence of request phase is 0
        if(planningPeriodMongoRepository.checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(
                unitId, planningPeriodDTO.getStartDate(), planningPeriodDTO.getEndDate(),AppConstants.REQUEST_PHASE_SEQUENCE)){
           exceptionService.actionNotPermittedException("message.period.phase.request.merge");
        }

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
    }*/

    // To delete planning period
/*    public boolean deletePlanningPeriod(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.period.unit.id",periodId);
        }

        // Check if it is last period
        PlanningPeriod lastPlanningPeriod = planningPeriodMongoRepository.getLastPlanningPeriod(unitId);

        if( !lastPlanningPeriod.getId().equals(planningPeriod.getId())){
            exceptionService.actionNotPermittedException("message.period.delete.last");
        }

        // Check if period is in request phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        // TO DO check phase by sequence
        if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
           exceptionService.actionNotPermittedException("message.period.phase.request.name",planningPeriod.getName());
        }

        planningPeriod.setDeleted(true);
        save(planningPeriod);
        return true;
    }*/

    // To update phase of period to the next one
    /*public PlanningPeriodDTO setPlanningPeriodPhaseToNext(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);

        if(!Optional.ofNullable(planningPeriod).isPresent()){
            exceptionService.dataNotFoundByIdException("message.period.unit.id",periodId);
        }
        if(!Optional.ofNullable(planningPeriod.getNextPhaseId()).isPresent()){
            exceptionService.actionNotPermittedException("message.period.phase.last");
        }
        Phase initialNextPhase = phaseMongoRepository.findOne(planningPeriod.getNextPhaseId());
        List<PhaseDTO> toBeNextPhase = phaseMongoRepository.getNextApplicablePhasesOfUnitBySequence(unitId, initialNextPhase.getSequence());
        planningPeriod.setCurrentPhaseId(initialNextPhase.getId());
        planningPeriod.setNextPhaseId(Optional.ofNullable(toBeNextPhase).isPresent() && toBeNextPhase.size()>0 ? toBeNextPhase.get(0).getId() : null);
        save(planningPeriod);

        return getPlanningPeriods(unitId, planningPeriod.getStartDate(), planningPeriod.getEndDate()).get(0);
    }*/

    /*public boolean updateFlippingDate(BigInteger periodId, Long unitId, Date date){
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
    }*/






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
