package com.planner.service.constraint.unit;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.constarints.unit.UnitConstraintDTO;
import com.planner.component.exception.ExceptionService;
import com.planner.domain.constraint.common.Constraint;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.repository.constraint.ConstraintsRepository;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

/**
 * Note:- All DTO fields null value validation if required will be validated on DTO itself
 * by Either {@code @NotNull} or {@code @NotEmpty} annotation
 */
@Service
public class UnitConstraintService {

    @Inject
    private ConstraintsRepository constraintsRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserNeo4jRepo userNeo4jRepo;
    @Inject
    private ExceptionService exceptionService;

    //======================================================
    public void createUnitConstraint(UnitConstraintDTO unitConstraintDTO) {
        if (preValidateUnitConstraintDTO(unitConstraintDTO, true)) {
            UnitConstraint unitConstraint = ObjectMapperUtils.copyPropertiesByMapper(unitConstraintDTO, UnitConstraint.class);
            constraintsRepository.saveObject(unitConstraint);
        }
    }


    //====================================================
    public void copyUnitConstraint(UnitConstraintDTO unitConstraintDTO) {
        Constraint constraint = constraintsRepository.findByIdNotDeleted(unitConstraintDTO.getId());
        if (constraint != null && preValidateUnitConstraintDTO(unitConstraintDTO, true)) {
            UnitConstraint unitConstraint = ObjectMapperUtils.copyPropertiesByMapper(unitConstraintDTO, UnitConstraint.class);
            unitConstraint.setParentUnitConstraintId(unitConstraintDTO.getId());
            unitConstraint.setId(null);//Unset Id
            constraintsRepository.saveObject(unitConstraint);

        }
    }

    //====================================================
    public List<UnitConstraint> getUnitConstraintsByUnitId(Long unitId) {
        List<Constraint> constraintList = constraintsRepository.findAllObjectsNotDeletedById(false, unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(constraintList, UnitConstraint.class);
    }

    //====================================================
    public void updateUnitConstraint(UnitConstraintDTO unitConstraintDTO) {
        Constraint constraint = constraintsRepository.findByIdNotDeleted(unitConstraintDTO.getId());
        if (constraint != null && preValidateUnitConstraintDTO(unitConstraintDTO, false)) {
            UnitConstraint unitConstraint = ObjectMapperUtils.copyPropertiesByMapper(unitConstraintDTO, UnitConstraint.class);
            constraintsRepository.saveObject(unitConstraint);
        }
    }

    //====================================================
    public void deleteUnitConstraint(BigInteger unitConstraintId) {
        boolean result = false;
        if (unitConstraintId != null)
            result = constraintsRepository.safeDeleteById(unitConstraintId);
        if (!result) {//TODO throw exception if reqired
        }

    }

    //===================common=======================================

    /**
     * Validation sequence should follow this ordering
     *
     * @param unitConstraintDTO
     * @return
     */
    private boolean preValidateUnitConstraintDTO(UnitConstraintDTO unitConstraintDTO, boolean isCurrentObjectIdNull) {
        String result = userNeo4jRepo.validateUnitConstraint(unitConstraintDTO.getUnitId());

        if ("unitNotExists".equals(result)) {

            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Unit", unitConstraintDTO.getUnitId());
        } else if (constraintsRepository.isNameExistsById(unitConstraintDTO.getName(), isCurrentObjectIdNull? null : unitConstraintDTO.getId(),false, unitConstraintDTO.getUnitId())) {

            exceptionService.dataNotFoundByIdException("message.name.alreadyExists");
        }
        return true;
    }
}
