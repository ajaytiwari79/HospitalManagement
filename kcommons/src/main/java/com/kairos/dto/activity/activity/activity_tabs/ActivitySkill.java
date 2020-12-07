package com.kairos.dto.activity.activity.activity_tabs;

import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Created by pawanmandhan on 28/8/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class ActivitySkill  {
    private  String name;
    private String level;
    private Long skillId;
    private Map<String, TranslationInfo> translations;


    public ActivitySkill(String name, String level, Long skillId) {
        this.name = name;
        this.level = level;
        this.skillId = skillId;
    }
}
