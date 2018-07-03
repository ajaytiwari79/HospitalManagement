package com.kairos.persistance.model;


import com.kairos.persistance.model.common.MongoBaseEntity;
import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "test")
@TypeName("test")
public class JaversTest extends MongoBaseEntity {


    @NotNull
    private String name;

    @NotNull
    private Long countryId;


    private List<Long> fd;

    public List<Long> getFd() {
        return fd;
    }

    public void setFd(List<Long> fd) {
        this.fd = fd;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
