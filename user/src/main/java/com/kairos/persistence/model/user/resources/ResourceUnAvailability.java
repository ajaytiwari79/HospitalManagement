package com.kairos.persistence.model.user.resources;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.kairos.utils.DateUtil.MONGODB_QUERY_DATE_FORMAT;

/**
 * Created by arvind on 6/10/16.
 */

@NodeEntity
public class ResourceUnAvailability extends UserBaseEntity {

    @Inject
    private ExceptionService exceptionService;
    private Long date;
    private Long startTime;
    private Long endTime;
    private boolean fullDay;

    public ResourceUnAvailability() {
        //default constructor
    }

    public ResourceUnAvailability(boolean fullDay) {
        this.fullDay = fullDay;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public boolean isFullDay() {
        return fullDay;
    }

    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }

    public ResourceUnAvailability setUnavailability(ResourceUnavailabilityDTO unavailabilityDTO, String unavailabilityDate)  {
        try{
            LocalDateTime startDateIncludeTime = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDate,
                    MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
            this.date = startDateIncludeTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            if(!unavailabilityDTO.isFullDay() && !StringUtils.isBlank(unavailabilityDTO.getStartTime())){
                LocalDateTime timeFrom = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDTO.getStartTime(),
                        MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                this.startTime = timeFrom.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }

            if(!unavailabilityDTO.isFullDay() && !StringUtils.isBlank(unavailabilityDTO.getEndTime())){
                LocalDateTime timeTo = LocalDateTime.ofInstant(DateUtil.convertToOnlyDate(unavailabilityDTO.getEndTime(),
                        MONGODB_QUERY_DATE_FORMAT).toInstant(), ZoneId.systemDefault());
                this.endTime = timeTo.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            return this;
        } catch (Exception e){
            exceptionService.dataNotFoundByIdException("message.date.somethingwrong");

        }
        return null;
    }
}
