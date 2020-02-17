package com.kairos.persistence.model.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by oodles on 20/9/17.
 */
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillExpertise extends MongoBaseEntity {

    private Long skillVisitourId;
    private Long skillLevel;
    private String skillName;


    public Long getSkillVisitourId() {
        return skillVisitourId;
    }

    public void setSkillVisitourId(Long skillVisitourId) {
        this.skillVisitourId = skillVisitourId;
    }

    public Long getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Long skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }




}
