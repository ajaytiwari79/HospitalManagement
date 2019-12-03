package com.kairos.dto.planner.vrp.vrpPlanning;


import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 9/7/18
 */
@Getter
@Setter
public class VRPIndictmentDTO {

    private BigInteger solverConfigId;
    private ScoreDTO totalScore;
    private List<ConstraintScoreDTO> scores;
}
