package com.kairos.service.organization;

import com.kairos.persistence.model.organization.AbsenceTypes;
import com.kairos.persistence.repository.organization.AbsenceTypesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by oodles on 16/12/16.
 */
@Transactional
@Service
public class AbsenceTypesService {

    @Inject
    private AbsenceTypesRepository absenceTypesRepository;

    public List<AbsenceTypes> getAllAbsenceTypes() {
        return absenceTypesRepository.findAll();
    }


    public AbsenceTypes getAbsenceTypeByATVTID(Long ATVTID) {
        return absenceTypesRepository.findByATVTID(ATVTID);
    }

    public AbsenceTypes getAbsenceTypeByName(String name){
        return absenceTypesRepository.findByName(name);
    }

    public AbsenceTypes createAbsenceTypes(Long ATVTID, String name, Long organizationId){
        AbsenceTypes absenceTypes = new AbsenceTypes(ATVTID, name, organizationId);
        return absenceTypesRepository.save(absenceTypes);
    }

    public AbsenceTypes removeAbsenceTypes(Long absenceTypeId){
        AbsenceTypes absenceType = absenceTypesRepository.findOne(absenceTypeId);
        return absenceTypesRepository.save(absenceType);
    }

    /**
     *  @auther anil maurya
     *  this method is called from task micro service
     * @param name
     * @return
     */
    public Map<String,Object> getAbsenceTypeMapByName(String name){
        Map<String, Object> workScheduleMetaData = new HashMap<>();
        AbsenceTypes absenceTypes =getAbsenceTypeByName(name);
        if (absenceTypes == null) workScheduleMetaData.put("type", 6);
        else workScheduleMetaData.put("type", absenceTypes.getATVTID());
        return workScheduleMetaData;
    }

}
