package com.kairos.persistence.model.clause;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class AgreementSectionClause {

    @NotNull(message = "error.message.id.notnull")
    private Long id;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    private String title;

    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Column(columnDefinition = "text")
    private String titleHtml;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    @Column(columnDefinition = "text")
    private String description;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    @Column(columnDefinition = "text")
    private String descriptionHtml;

    private UUID tempClauseId;

    private boolean deleted;

    public AgreementSectionClause(Long id, String titleHtml, String descriptionHtml, String description) {
        this.id = id;
        this.titleHtml = titleHtml;
        this.descriptionHtml = descriptionHtml;
        this.description  = description;
    }

}
