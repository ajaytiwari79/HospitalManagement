package com.kairos.persistence.model.organization.group;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.persistence.model.common.TranslationConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

/**
 * Created By G.P.Ranjan on 19/11/19
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Long id;
    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String name;
    private String description;
    private List<FilterSelectionDTO> filtersData;
    private List<Long> excludedStaffs;
    private String roomId;
    private Long unitId;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    @Convert(TranslationConverter.class)
    private Map<String, TranslationInfo> translations;

    public GroupDTO(Long id, String name, String description, List<Long> excludedStaffs, String roomId){
        this.id = id;
        this.name = name;
        this.description = description;
        this.excludedStaffs = excludedStaffs;
        this.roomId = roomId;
    }
    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }

}
