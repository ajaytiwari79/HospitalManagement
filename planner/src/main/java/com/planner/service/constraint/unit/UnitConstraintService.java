package com.planner.service.constraint.unit;

import com.kairos.commons.planning_setting.ConstraintSetting;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.planner.constarints.unit.UnitConstraintDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.repository.constraint.UnitConstraintRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.enums.constraint.ScoreLevel.HARD;

@Service
public class UnitConstraintService {

    @Inject
    private UnitConstraintRepository unitConstraintRepository;

    public UnitConstraintDTO createUnitConstraint(UnitConstraintDTO unitConstraintDTO){
        UnitConstraint unitConstraint = unitConstraintRepository.findByUnitIdAndConstraintSubTypeAndDeletedFalse(unitConstraintDTO.getUnitId(),unitConstraintDTO.getConstraintSubType());
        if(ObjectUtils.isNotNull(unitConstraint)){
            if(!unitConstraint.getMandatory()) {
                unitConstraint.setConstraintSetting(unitConstraintDTO.getConstraintSetting());
            }
        }else{
            unitConstraint = ObjectMapperUtils.copyPropertiesByMapper(unitConstraintDTO, UnitConstraint.class);

        }
        unitConstraintRepository.save(unitConstraint);
        return unitConstraintDTO;
    }


    public List<UnitConstraintDTO> getAllUnitConstraintByUnitId(Long unitId){
        List<UnitConstraint> unitConstraints = unitConstraintRepository.findAllByUnitIdAndDeletedFalse(unitId);
        if(ObjectUtils.isCollectionNotEmpty(unitConstraints)) {
            return ObjectMapperUtils.copyCollectionPropertiesByMapper(unitConstraints, UnitConstraintDTO.class);
        }else{
          return getunitconstraints();
        }
    }

    public List<UnitConstraintDTO> getunitconstraints(){
        List<UnitConstraintDTO> unitConstraintDTOS = new ArrayList<>();
        for(int i=0;i < ConstraintSubType.values().length;i++) {
            ConstraintSetting constraintSetting1 = new ConstraintSetting(HARD, 2,false);
            UnitConstraintDTO unitConstraintDTO1 = new UnitConstraintDTO(constraintSetting1, ConstraintSubType.values()[i]);
            unitConstraintDTOS.add(unitConstraintDTO1);
        }
        return unitConstraintDTOS;
    }






}
