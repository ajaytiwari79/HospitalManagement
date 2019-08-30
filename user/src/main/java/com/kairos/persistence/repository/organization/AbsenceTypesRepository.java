package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.AbsenceTypes;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

import java.util.List;

/**
 * Created by oodles on 16/12/16.
 */
public interface AbsenceTypesRepository extends Neo4jBaseRepository<AbsenceTypes,Long> {

    List<AbsenceTypes> findAll();

    AbsenceTypes findByATVTID(Long ATVTID);

    AbsenceTypes findByName(String name);


}
