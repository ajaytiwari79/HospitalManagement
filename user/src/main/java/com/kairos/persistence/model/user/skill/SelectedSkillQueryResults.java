package com.kairos.persistence.model.user.skill;

import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.common.TranslationConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@QueryResult
public class SelectedSkillQueryResults {
   private Long id;
   private String name;
   private String description;
   private Long unitId;
   private List<Map<String,Object>> children;
   private Map<String,String> translatedNames;
   private Map<String,String> translatedDescriptions;
   @Convert(TranslationConverter.class)
   private Map<String, TranslationInfo> translations;


}
