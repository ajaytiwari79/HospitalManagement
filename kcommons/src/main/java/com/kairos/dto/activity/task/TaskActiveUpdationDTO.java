package com.kairos.dto.activity.task;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by neuron on 23/5/17.
 */
@Getter
@Setter
public class TaskActiveUpdationDTO {
    private List<BigInteger> taskIds;
    private boolean makeActive;
    private int unitId;
}
