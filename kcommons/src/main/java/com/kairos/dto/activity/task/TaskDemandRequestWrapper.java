package com.kairos.dto.activity.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskDemandRequestWrapper {
   private Long citizenId;
   private Long unitId;
   private Long timeSlotId;
   private Date startDate;
   private Date endDate;
}
