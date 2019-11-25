package com.planner.service.constraint.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.constarints.country.CountryConstraintDTO;
import com.kairos.enums.planning_problem.PlanningProblemType;
import com.planner.component.exception.ExceptionService;
import com.planner.domain.constraint.common.Constraint;
import com.planner.domain.constraint.country.CountryConstraint;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.repository.constraint.ConstraintsRepository;
import com.planner.repository.planning_problem.PlanningProblemRepository;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.service.constraint.country.default_.DefaultCountryConstraintService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;

/**
 * Note:- All DTO fields null value validation if required will be validated on DTO itself
 * by Either {@code @NotNull} or {@code @NotEmpty} annotation
 */
@Service
public class CountryConstraintService {

    public static final String MESSAGE_DATA_NOT_FOUND = "message.dataNotFound";
    @Inject
    private ConstraintsRepository constraintsRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserNeo4jRepo userNeo4jRepo;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private PlanningProblemRepository planningProblemRepository;

    public CountryConstraintDTO createCountryConstraint(CountryConstraintDTO countryConstraintDTO) {
        if (preValidateCountryConstraintDTO(countryConstraintDTO, true)) {
            CountryConstraint countryConstraint = ObjectMapperUtils.copyPropertiesByMapper(countryConstraintDTO, CountryConstraint.class);
            constraintsRepository.saveEntity(countryConstraint);
            //Now copy same Constraints on units
            createUnitConstraintByOrganizationServiceAndSubService(countryConstraintDTO.getOrganizationServiceId(), countryConstraintDTO.getOrganizationSubServiceId(), countryConstraint);
            countryConstraintDTO.setId(countryConstraint.getId());
        }
        return countryConstraintDTO;
    }

    /**
     * Copy CountryConstraint on applicable
     * units
     *
     * @param organizationServiceId
     * @param organizationSubServiceId
     * @param countryConstraint
     */
    private void createUnitConstraintByOrganizationServiceAndSubService(Long organizationServiceId, Long organizationSubServiceId, CountryConstraint countryConstraint) {
        List<Long> applicableUnitIdForConstraint = userNeo4jRepo.getUnitIdsByOrganizationServiceAndSubServiceId(organizationServiceId, organizationSubServiceId);
        List<UnitConstraint> unitConstraintList = new ArrayList<>();
        if (!applicableUnitIdForConstraint.isEmpty()) {
            for (Long unitId : applicableUnitIdForConstraint) {
                UnitConstraint unitConstraint = ObjectMapperUtils.copyPropertiesByMapper(countryConstraint, UnitConstraint.class);
                unitConstraint.setId(null);//Unset Id
                unitConstraint.setUnitId(unitId);
                unitConstraint.setParentCountryConstraintId(countryConstraint.getId());
                unitConstraintList.add(unitConstraint);
            }

            if (unitConstraintList.size() > 0) {
                constraintsRepository.saveList(unitConstraintList);
            }
        }
    }

    public CountryConstraintDTO copyCountryConstraint(CountryConstraintDTO countryConstraintDTO) {
        Constraint constraint = constraintsRepository.findByIdNotDeleted(countryConstraintDTO.getId());
        if (constraint != null && preValidateCountryConstraintDTO(countryConstraintDTO, true)) {
            CountryConstraint countryConstraint = ObjectMapperUtils.copyPropertiesByMapper(countryConstraintDTO, CountryConstraint.class);
            countryConstraint.setParentConstraintId(countryConstraintDTO.getId());
            countryConstraint.setId(null);//Unset Id
            constraintsRepository.saveEntity(countryConstraint);
            //Now copy same Constraints on unit
            createUnitConstraintByOrganizationServiceAndSubService(countryConstraintDTO.getOrganizationServiceId(), countryConstraintDTO.getOrganizationSubServiceId(), countryConstraint);
            countryConstraintDTO.setId(countryConstraint.getId());
        }
        return countryConstraintDTO;
    }

    public List<CountryConstraint> getAllCountryConstraintByCountryId(Long countryId) {
        List<Constraint> constraintList = constraintsRepository.findAllObjectsNotDeletedById(true, countryId);
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(constraintList, CountryConstraint.class);
    }

    public void updateCountryConstraint(CountryConstraintDTO countryConstraintDTO) {
        Constraint constraint = constraintsRepository.findByIdNotDeleted(countryConstraintDTO.getId());
        if (constraint != null && preValidateCountryConstraintDTO(countryConstraintDTO, false)) {
            CountryConstraint countryConstraint = ObjectMapperUtils.copyPropertiesByMapper(countryConstraintDTO, CountryConstraint.class);
            constraintsRepository.saveEntity(countryConstraint);
        }
    }

    public void deleteCountryConstraint(BigInteger countryConstraintId) {
        boolean result = false;
        if (countryConstraintId != null)
            result = constraintsRepository.safeDeleteById(countryConstraintId);
        if (!result) {//TODO throw exception if required
        }

    }

    /**
     * Validation sequence should follow this ordering
     *
     * @param countryConstraintDTO
     * @return
     */
    private boolean preValidateCountryConstraintDTO(CountryConstraintDTO countryConstraintDTO, boolean isCurrentObjectIdNull) {
        String result = userNeo4jRepo.validateCountryOrganizationServiceAndSubService(countryConstraintDTO.getCountryId(), countryConstraintDTO.getOrganizationServiceId(), countryConstraintDTO.getOrganizationSubServiceId());
        if ("countryNotExists".equals(result)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATA_NOT_FOUND, "Country", countryConstraintDTO.getCountryId());
        } else if (constraintsRepository.isNameExistsById(countryConstraintDTO.getName(), isCurrentObjectIdNull ? null : countryConstraintDTO.getId(), true, countryConstraintDTO.getCountryId())) {
            exceptionService.dataNotFoundByIdException("message.name.alreadyExists");
        } else if ("organizationServiceNotExists".equals(result)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATA_NOT_FOUND, "OrganizationService", countryConstraintDTO.getOrganizationServiceId());
        } else if ("organizationSubServiceNotExists".equals(result)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATA_NOT_FOUND, "OrganizationSubService", countryConstraintDTO.getOrganizationSubServiceId());
        } else if ("relationShipNotValid".equals(result)) {
            exceptionService.relationShipNotValidException("message.relationship.notValid");
        }
        return true;
    }

    public List<CountryConstraint> createDefaultCountryConstraints(Long countryId) {
        PlanningProblem planningProblem=planningProblemRepository.findPlanningProblemByType("shiftPlanning");
        BigInteger planningProblemId=null;
        if(isNotNull(planningProblem)){
            planningProblemId=planningProblem.getId();
        }
        else {
            planningProblem = new PlanningProblem("Shift planning","Shift planning", PlanningProblemType.SHIFT_PLANNING,countryId);
            planningProblemRepository.saveEntity(planningProblem);
            planningProblemId = planningProblem.getId();
        }
        List<CountryConstraint> countryConstraintList = DefaultCountryConstraintService.createDefaultCountryConstraints(countryId,planningProblemId);
        if (isCollectionNotEmpty(countryConstraintList)) {
            constraintsRepository.saveList(countryConstraintList);
        }
        return countryConstraintList;
    }

}
