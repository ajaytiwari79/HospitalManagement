package com.kairos.dto.activity.activity;

import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
public class ActivityPriorityDTO {

    private BigInteger id;
    private Long countryId;
    private Long organizationId;
    @NotBlank(message = "error.name.notnull")
    private String name;
    private String description;
    @Range(min = 1,message = "message.activity.priority.sequence")
    private int sequence;
    private String colorCode;
    private Map<String, TranslationInfo> translations;

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return TranslationUtil.getDescription(translations,description);
    }

}
