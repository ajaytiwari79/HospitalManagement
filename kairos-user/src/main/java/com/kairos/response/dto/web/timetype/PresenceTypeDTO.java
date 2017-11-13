package com.kairos.response.dto.web.timetype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;

/**
 * Created by vipul on 10/11/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceTypeDTO extends UserBaseEntity {
    @NotEmpty(message = "error.PresenceTypeDTO.name.notEmpty")
    @NotNull(message = "error.PresenceTypeDTO.name.notnull")
    private String name;
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public PresenceTypeDTO() {
    }

    public PresenceTypeDTO(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
