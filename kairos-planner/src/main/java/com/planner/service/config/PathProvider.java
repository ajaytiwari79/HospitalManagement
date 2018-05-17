package com.planner.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PathProvider {
    @Value(value="shiftplanning.problem.xmlpath")
    private String problemXmlpath;
    @Value(value="shiftplanning.solverconfig.xmlpath")
    private String solverConfigXmlpath;
    @Value(value="shiftplanning.solution.xmlpath")
    private String solutionXmlpath;

    public String getProblemXmlpath() {
        return problemXmlpath;
    }

    public void setProblemXmlpath(String problemXmlpath) {
        this.problemXmlpath = problemXmlpath;
    }

    public String getSolverConfigXmlpath() {
        return solverConfigXmlpath;
    }

    public void setSolverConfigXmlpath(String solverConfigXmlpath) {
        this.solverConfigXmlpath = solverConfigXmlpath;
    }

    public String getSolutionXmlpath() {
        return solutionXmlpath;
    }

    public void setSolutionXmlpath(String solutionXmlpath) {
        this.solutionXmlpath = solutionXmlpath;
    }
}
