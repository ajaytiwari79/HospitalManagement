package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.AbsenceTypes;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * Created by oodles on 16/12/16.
 */
public interface AbsenceTypesRepository extends GraphRepository<AbsenceTypes> {

    List<AbsenceTypes> findAll();

    AbsenceTypes findByATVTID(Long ATVTID);

    AbsenceTypes findAllByOrganizationId(Long organizationId);

    AbsenceTypes findByName(String name);


}
