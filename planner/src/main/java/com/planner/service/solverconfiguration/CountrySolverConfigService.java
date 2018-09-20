package com.planner.service.solverconfiguration;

import com.planner.repository.config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CountrySolverConfigService {

    @Inject
    private SolverConfigRepository solverConfigRepository;
}
