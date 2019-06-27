package com.kairos.response.dto.public_legal_document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Created By G.P.Ranjan on 26/6/19
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class PublicLegalDocumentDTO {
    private Long id;

    @NotNull(message = "Public Legal Document Name Required")
    private String name;

    private String publicLegalDocumentLogo;

    @NotNull(message = "Public Legal Document Body Content Required")
    private String bodyContentInHtml;

    public PublicLegalDocumentDTO(Long id,String name,String publicLegalDocumentLogo,String bodyContentInHtml){
        this.id=id;
        this.name=name;
        this.publicLegalDocumentLogo=publicLegalDocumentLogo;
        this.bodyContentInHtml=bodyContentInHtml;
    }
}
