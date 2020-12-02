package com.kairos.persistence.model.time_slot;

import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotSet extends MongoBaseEntity {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeSlotMode timeSlotMode;
    private TimeSlotType timeSlotType;
    private boolean defaultSet = false;
    private Long unitId;
    private List<TimeSlot> timeSlots=new ArrayList<>();

    public TimeSlotSet(String name, LocalDate startDate, TimeSlotMode timeSlotMode,Long unitId) {
        this.name = name;
        this.startDate = startDate;
        this.timeSlotMode = timeSlotMode;
        this.unitId=unitId;

    }


}
