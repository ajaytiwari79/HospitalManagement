package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by pavan on 13/3/18.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class FunctionDTO {
    private Long id;
    @NotBlank(message = "error.function.name.notEmpty")
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> unionIds;
    private List<Long> organizationLevelIds;
    private String icon;
    private List<LocalDate> appliedDates;
    private Long employmentId;
    private int code;

    public FunctionDTO(Long id, String name, String icon) {
        this.id = id;this.name = name;
        this.icon = icon;
    }


    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }


}