package com.kairos.dto.activity.task_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 22/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class TaskTypeDTO {

    @NotBlank(message = "error.Tasktype.title.notnull")
    private String title;

    private Date expiresOn;


    private String description;

    private BigInteger id;

    private Boolean status;
    private int duration;
    private Long serviceId;
    private String parentTaskTypeId;
    private String colorForGantt;
    private List<BigInteger> tags = new ArrayList<>();


    public TaskTypeDTO(String title, Date expiresOn, String description, BigInteger id, boolean status) {
        this.title = title;
        this.expiresOn = expiresOn;
        this.description = description;
        this.id = id;
        this.status = status;
    }
}
