package com.kairos.persistence.model.public_legal_document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created By G.P.Ranjan on 26/6/19
 **/
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PublicLegalDocument extends BaseEntity {
    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String publicLegalDocumentLogo;
    @Column(columnDefinition = "text")
    private String bodyContentInHtml;

    public PublicLegalDocument(Long id,String name,String publicLegalDocumentLogo,String bodyContentInHtml){
        super.id=id;
        this.name=name;
        this.publicLegalDocumentLogo=publicLegalDocumentLogo;
        this.bodyContentInHtml=bodyContentInHtml;
    }
}
