package com.planner.repository.config;

import com.planner.domain.config.Constraint;
import com.planner.repository.customRepository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SolverConfigRepository extends BaseRepository{


    public List<Constraint> getAllContraintsBySolverConfigId(String solverConfigId) {
        /*Select select = QueryBuilder.select().from("constraint").allowFiltering();
        select.where(QueryBuilder.in("solverconfigid",solverConfigId));
        return findAllByQuery(select,Constraint.class);*/
        return null;
    }
}
