package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClauseBasicDTO {


    private Long id;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String title;

    private String titleHtml;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    private String descriptionHtml;

    private boolean requireUpdate;

    @NotNull(message = "error.message.clause.order")
    private Integer orderedIndex;

    private UUID tempClauseId;

    public String getTitleHtml() {

        if (titleHtml == null) {
            titleHtml = "<p>"+title+"</p>";
        }
        return titleHtml.trim();
    }

    public String getDescriptionHtml() {
        if (descriptionHtml == null) {
            descriptionHtml = "<p>"+description+"</p>";
        }
        return descriptionHtml.trim();
    }

    public ClauseBasicDTO() {
        this.tempClauseId = UUID.randomUUID();
    }
}
