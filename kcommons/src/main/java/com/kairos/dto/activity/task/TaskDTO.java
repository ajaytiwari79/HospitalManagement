package com.kairos.dto.activity.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * This
 * Created by oodles on 8/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TaskDTO {

    private String id;
    private Long resource;
    private String start;
    private String end;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT")
    private Date startDate;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT")
    private Date endDate;
    private String info1;
    private String info2;
    private String taskTypeId;
    private String taskTypeName;
    private Integer duration;
    private Boolean active;
    @NotBlank(message = "error.TaskDTO.startAddress.notEmpty")
    private String startAddress;
    @NotBlank(message = "error.TaskDTO.endAddress.notEmpty")
    private String endAddress;
    private Integer priority;
    private Long anonymousStaffId;
    private AbsencePlanningStatus status;
    private List<Long> forbiddenStaff;
    private List<Long> prefferedStaff;
    private List<String> skillsList;
    private String team;
    private List<BigInteger> taskIds;

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id='" + id + '\'' +
                ", resource=" + resource +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", info1='" + info1 + '\'' +
                ", info2='" + info2 + '\'' +
                ", taskTypeId=" + taskTypeId +
                ", duration=" + duration +
                ", active=" + active +
                ", startAddress='" + startAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", priority=" + priority +
                ", anonymousStaffId=" + anonymousStaffId +
                '}';
    }

}
