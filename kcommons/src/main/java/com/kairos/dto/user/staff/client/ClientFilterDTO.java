package com.kairos.dto.user.staff.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Jasgeet on 12/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClientFilterDTO {
    private String name;
    private String cprNumber;
    private Long phoneNumber;
    private String clientStatus;
    private List<String> taskTypes;
    private List<Long> servicesTypes;
    private List<Long> localAreaTags;
    private boolean newDemands;
    private List<Long> timeSlots;
}
