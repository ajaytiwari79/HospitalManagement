package com.planning.repository.config;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.planning.domain.config.Constraint;
import com.planning.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SolverConfigRepository extends BaseRepository{


    public List<Constraint> getAllContraintsBySolverConfigId(String solverConfigId) {
        Select select = QueryBuilder.select().from("constraint").allowFiltering();
        select.where(QueryBuilder.in("solverconfigid",solverConfigId));
        return findAllByQuery(select,Constraint.class);
    }
}
