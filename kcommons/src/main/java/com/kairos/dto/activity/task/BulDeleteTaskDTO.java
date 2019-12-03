package com.kairos.dto.activity.task;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by prabjot on 13/7/17.
 */
@Getter
@Setter
public class BulDeleteTaskDTO {

   private List<BigInteger> taskIds;
}
