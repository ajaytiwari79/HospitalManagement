package com.kairos.persistence.model.user.expertise.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * Created by prerna on 14/11/17.
 */
@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class SkillQueryResult {

    private Long id;
    @NotBlank(message = "error.SkillCategory.name.notEmpty")
    private String name;
    private String description;
    private String shortName;
    private List<Long> tags;
    private Set<SkillLevelQueryResult> skillLevels;
    private String skillCategory;


    public SkillQueryResult(Long id, Set<SkillLevelQueryResult> skillLevels,String name,String skillCategory) {
        this.id = id;
        this.skillLevels = skillLevels;
        this.name=name;
        this.skillCategory=skillCategory;
    }

}
