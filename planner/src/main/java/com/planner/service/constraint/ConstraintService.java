package com.planner.service.constraint;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.planner.domain.constraint.common.Constraint;
import com.planner.repository.constraint.ConstraintsRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Deprecated
public class ConstraintService {
    @Inject
    private ConstraintsRepository constraintsRepository;


    //==========================================================================
    /*public void createConstraint(ConstraintDTO constraintDTO) {
        boolean nameExists = constraintsRepository.isNameExists(constraintDTO.getName(),null);
        if (!nameExists) {
            Constraint constraint = ObjectMapperUtils.copyPropertiesByMapper(constraintDTO, Constraint.class);
            constraintsRepository.saveObject(constraint);
        }

    }*/
    //=========================================================================
    public ConstraintDTO getConstraint(BigInteger constraintId) {
        ConstraintDTO constraintDTO = null;
        Optional<Constraint> constraintOptional = constraintsRepository.findById(constraintId);
        if (constraintOptional.isPresent()) {
            Constraint constraint = constraintOptional.get();
            constraintDTO = ObjectMapperUtils.copyPropertiesByMapper(constraint, ConstraintDTO.class);
        }
        return constraintDTO;
    }
    //=========================================================================
    public List<ConstraintDTO> getAllConstraint() {
        List<Constraint> constraintList = constraintsRepository.findAllNotDeleted();
        return ObjectMapperUtils.copyPropertiesOfListByMapper(constraintList, ConstraintDTO.class);
    }
    //=========================================================================
    //Only update if present
    /*public ConstraintDTO updateConstraint(ConstraintDTO constraintDTO) {
        Optional<Constraint> constraintOptional = constraintsRepository.findById(constraintDTO.getId());
        boolean nameExists = constraintsRepository.isNameExists(constraintDTO.getName(),constraintDTO.getId());

        if (constraintOptional.isPresent() && !nameExists) {
            Constraint constraint = ObjectMapperUtils.copyPropertiesByMapper(constraintDTO, Constraint.class);
            constraintsRepository.save(constraint);
        }
        return constraintDTO;
    }*/
    //=========================================================================
    //Soft Delete
    public boolean deleteConstraint(BigInteger constraintId) {
        boolean isPresent = constraintsRepository.findById(constraintId).isPresent();
        if (isPresent) {
            constraintsRepository.safeDeleteById(constraintId);
        }
        return isPresent;
    }

}
