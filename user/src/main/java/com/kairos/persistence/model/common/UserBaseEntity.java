package com.kairos.persistence.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Properties;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains common fields of an entity
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

@Getter
@Setter
public abstract class UserBaseEntity  {

    //@GeneratedValue
    @GraphId protected Long id;
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
    @Properties
    protected Map<String,String> translatedDescriptions;


    @JsonIgnore
    public Map<String, TranslationInfo> getTranslatedData() {
        Map<String, TranslationInfo> infoMap=new HashMap<>();
        translatedNames.forEach((k,v)-> infoMap.put(k,new TranslationInfo(v,translatedDescriptions.get(k))));
        return infoMap;
    }
}
