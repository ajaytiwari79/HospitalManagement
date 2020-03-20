package com.kairos.persistence.repository.repository_impl;


import com.kairos.enums.data_filters.StaffFilterSelectionDTO;
import org.neo4j.ogm.session.Session;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Procedure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StaffFilterProcedures {

    @Autowired
    private Session session;

    @Procedure(value = "kairos.staffsearch")
    @Description(value = "Return the list of staff on the basis of selected filters and their ids")
    public String findByFilterIds(List<StaffFilterSelectionDTO> staffFilters,Long unitId){
            return null;
    }

}
