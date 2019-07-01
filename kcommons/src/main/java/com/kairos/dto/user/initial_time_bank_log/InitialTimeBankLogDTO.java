package com.kairos.dto.user.initial_time_bank_log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created By G.P.Ranjan on 25/6/19
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class InitialTimeBankLogDTO {
    private Long id;
    @NotNull(message = "employmentId is missing")
    @Range(min = 0, message = "employmentId is missing")
    private Long employmentId;

    private Long previousInitialBalanceInMinutes;
    
    @NotNull(message = "update initial balance is required for time bank log")
    private Long updatedInitialBalanceInMinutes;

    private LocalDateTime updatedOn;

    private Long updatedBy;

    public InitialTimeBankLogDTO(Long id, Long employmentId, Long previousInitialBalanceInMinutes, Long updatedInitialBalanceInMinutes,LocalDateTime updatedOn,Long updatedBy){
        this.id=id;
        this.employmentId=employmentId;
        this.previousInitialBalanceInMinutes=previousInitialBalanceInMinutes;
        this.updatedInitialBalanceInMinutes=updatedInitialBalanceInMinutes;
        this.updatedOn=updatedOn;
        this.updatedBy=updatedBy;
    }
}
