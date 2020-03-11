package com.kairos.persistence.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.TranslationInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract  class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreatedDate
    protected Date createdAt;

    @LastModifiedDate
    protected Date updatedAt;

    @Column
    @Convert(converter = GDPRTranslationInfoConverter.class)
    protected Map<String, TranslationInfo> translations;

    @JsonIgnore
    protected boolean deleted;

    public void delete(){
        this.deleted = true;
    }

}
