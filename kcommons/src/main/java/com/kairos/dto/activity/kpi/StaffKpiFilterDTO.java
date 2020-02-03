package com.kairos.dto.activity.kpi;



import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.tag.TagDTO;
import com.kairos.dto.user.team.TeamDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class StaffKpiFilterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private List<Long> unitIds;
    private Long unitId;
    private String unitName;
    private String cprNumber;
    private int staffAge;
    private List<EmploymentWithCtaDetailsDTO> employment;
    private List<DayTypeDTO> dayTypeDTOS;
    private List<TeamDTO> teams;
    private List<TagDTO> tags;


    public String getFullName(){
        return this.firstName+" "+this.getLastName();
    }

    public boolean isTagValid(Set<Long> tagIds){
        return tags.stream().anyMatch(tag->tagIds.contains(tag.getId()));
    }


}
