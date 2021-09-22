package com.kairos.persistence.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Contains common fields of an entity
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

@Getter
@Setter
public abstract class UserBaseEntity  implements Serializable {

    private static final long serialVersionUID = 8338404773846966110L;
    //@GeneratedValue
    @GraphId
    protected Long id;
    @JsonIgnore
    protected boolean deleted;
    @JsonIgnore
    @CreatedDate
    private LocalDateTime creationDate;
    @JsonIgnore
    @LastModifiedDate
    private LocalDateTime lastModificationDate;
    @JsonIgnore
    @CreatedBy
    protected Long createdBy;
    @JsonIgnore
    @LastModifiedBy
    protected Long lastModifiedBy;
    @Properties
    protected Map<String,String> translatedNames;
    @Convert(TranslationConverter.class)
    protected Map<String, TranslationInfo> translations;

}
