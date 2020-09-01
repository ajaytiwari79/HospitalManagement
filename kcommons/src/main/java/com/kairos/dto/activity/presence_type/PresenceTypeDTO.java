package com.kairos.dto.activity.presence_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author pradeep
 * @date - 26/4/18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PresenceTypeDTO {
    @NotBlank(message = "error.PresenceTypeDTO.name.notEmpty")
    private String name;
    private BigInteger id;
    private String imageName;
    private Long countryId;
    private Map<String,TranslationInfo> translations;

    public PresenceTypeDTO(@NotBlank(message = "error.PresenceTypeDTO.name.notEmpty") String name, BigInteger id) {
        this.name = name;
        this.id = id;
    }


    @Override
    public String toString() {
        return "PresenceTypeDTO{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }


}
