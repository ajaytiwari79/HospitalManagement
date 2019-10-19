package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ImportTimeSlotDTO {

    private Long id;
    private String title;
    private String start;
    private String end;
    private String category;
    private boolean active;
}