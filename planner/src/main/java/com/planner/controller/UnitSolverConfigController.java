package com.planner.controller;

import com.planner.service.solverconfiguration.UnitSolverConfigService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping
public class UnitSolverConfigController {
    @Inject
    private UnitSolverConfigService unitSolverConfigService;
}
