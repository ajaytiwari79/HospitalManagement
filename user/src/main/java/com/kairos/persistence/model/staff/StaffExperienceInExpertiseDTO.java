package com.kairos.persistence.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.common.TranslationConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;
import java.util.Map;

/**
 * Created by pavan on 27/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class StaffExperienceInExpertiseDTO {
    private Long id;
    private String name;
    private Long expertiseId;
    private Integer relevantExperienceInMonths;
    @DateLong
    private Date expertiseStartDate;
    private Integer nextSeniorityLevelInMonths;
    @Convert(TranslationConverter.class)
    private Map<String, TranslationInfo> translations;

    public StaffExperienceInExpertiseDTO(Long id, String name, Long expertiseId, Integer relevantExperienceInMonths, Date expertiseStartDate) {
        this.id = id;
        this.name = name;
        this.expertiseId = expertiseId;
        this.relevantExperienceInMonths = relevantExperienceInMonths;
        this.expertiseStartDate = expertiseStartDate;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StaffExperienceInExpertiseDTO{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", expertiseId=").append(expertiseId);
        sb.append(", relevantExperienceInMonths=").append(relevantExperienceInMonths);
        sb.append(", expertiseStartDate=").append(expertiseStartDate);
        sb.append('}');
        return sb.toString();
    }

    public String getName(){
        return TranslationUtil.getName(translations,name);
    }
}
