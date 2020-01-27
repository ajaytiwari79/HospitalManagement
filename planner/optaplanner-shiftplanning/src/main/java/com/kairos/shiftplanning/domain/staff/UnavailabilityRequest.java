package com.kairos.shiftplanning.domain.staff;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("UnavailabilityList")
public class UnavailabilityRequest {
	private Long id;
    //@XStreamConverter(JodaTimeConverter.class)
    private DateTime startTime;
    //@XStreamConverter(JodaTimeConverter.class)
    private DateTime endTime;
    private int weight;
    private Employee employee;

    public UnavailabilityRequest(Long id, int weight, DateTime startTime, DateTime endTime,Employee employee) {
        this.weight = weight;
        this.endTime = endTime;
        this.startTime = startTime;
        this.id = id;
        this.employee=employee;
    }
}
