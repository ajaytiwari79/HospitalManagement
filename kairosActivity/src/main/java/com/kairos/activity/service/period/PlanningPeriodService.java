package com.kairos.activity.service.period;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.client.dto.organization.OrganizationDTO;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.period.PeriodSettings;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.repository.period.PlanningPeriodMongoRepository;
import com.kairos.activity.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.DurationType;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by prerna on 6/4/18.
 */
@Service
@Transactional
public class PlanningPeriodService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);

    @Inject
    PhaseService phaseService;

    @Inject
    PlanningPeriodMongoRepository planningPeriodMongoRepository;

    @Inject PhaseMongoRepository phaseMongoRepository;

    public void addPeriod(Long unitId, Date startDate, List<PhaseDTO> phases, PlanningPeriodDTO planningPeriodDTO){
        // TODO calculate END Date
        Date endDate = startDate;
        // TODO if End Date is greater than end date in DTO and return

        PlanningPeriod planningPeriod = new PlanningPeriod("Temp Period", startDate, endDate, unitId, null, phases);
        save(planningPeriod);
        addPeriod(unitId, endDate, phases, planningPeriodDTO);
        //
    }

    public void createPeriod(Long unitId, PlanningPeriodDTO planningPeriodDTO) {
        // TODO Check monday if duration is in week
        List<PhaseDTO> phases = phaseService.getApplicablePhasesByOrganizationId(unitId);
        if(!Optional.ofNullable(phases).isPresent()){
            throw new DataNotFoundByIdException("No Phases found in the Organization " + unitId);
        }
        // TODO set End date in PlanningPeriodDTO according to recurringNumber
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
//        addPeriod(unitId, planningPeriodDTO.getStartDate(), phases, planningPeriodDTO);
    }

    public void updatePeriod(Long unitId, BigInteger periodId, PlanningPeriodDTO planningPeriodDTO){
        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findOne(periodId);

        //TODO check if startDate and endDate is different from the original one
        boolean mergeToPrevious = true;
        boolean mergeToNext = true;
        if(mergeToPrevious || mergeToNext){
            // Check if period is in request phase
            // We are checking request phase by its name, can be done by sequence, need to ask
            if(!phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
                throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
            }
        }
        // TODO Fetch previous and next planning periods
        PlanningPeriod previousPeriod = new PlanningPeriod();
        PlanningPeriod nextPeriod = new PlanningPeriod();

        // TODO Merge all periods and save

        // TODO Fetch periods from start date and end date and return

    }

    public void deletePeriod(Long unitId, BigInteger periodId){

        PlanningPeriod planningPeriod = planningPeriodMongoRepository.findByIdAndUnitId(periodId, unitId);
        if(Optional.ofNullable(planningPeriod).isPresent()){
            throw new DataNotFoundByIdException("Period does not Exists Id " + periodId);
        }

        // Check if period is in request phase
        // We are checking request phase by its name, can be done by sequence, need to ask
        if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
        }

        // TODO Check if any of next phases is not in Request Phase
        if( !phaseMongoRepository.checkPhaseByName(planningPeriod.getCurrentPhaseId(), "REQUEST")){
            throw new ActionNotPermittedException("Period with name : " + planningPeriod.getName() + " is not in Request Phase.");
        }
    }

    public List<PlanningPeriodDTO> getPeriods(Long unitId){
        List<PlanningPeriodDTO> planningPeriods = new ArrayList<>();
        return planningPeriods;
    }

    /*id: 11,
    name: 'p12',
    startDate: 1541052026772,
    endDate: 1543557626772,
    duration: 1,
    durationType: 'WEEKS'
    requestToPuzzleDate: 1541829626772,
    puzzleToConstructionDate: 1542520826772,
    constructionToDraftDate: 1543125626772,
    currentPhase: string;
    recurringNumber: number;*/
}
