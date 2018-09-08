package com.kairos.activity.presence_type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 26/4/18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceTypeDTO {//extends UserBaseEntity {
    @NotEmpty(message = "error.PresenceTypeDTO.name.notEmpty")
    @NotNull(message = "error.PresenceTypeDTO.name.notnull")
    private String name;
    private BigInteger id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public PresenceTypeDTO() {
    }

    public PresenceTypeDTO(@NotEmpty(message = "error.PresenceTypeDTO.name.notEmpty") @NotNull(message = "error.PresenceTypeDTO.name.notnull") String name, BigInteger id) {
        this.name = name;
        this.id = id;
    }

    public PresenceTypeDTO(String name) {
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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
