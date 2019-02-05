package com.kairos.persistence.model.embeddables;


import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class AccountType {

    @NotNull
    private Long id;
    @NotNull
    private String name;

    public AccountType(@NotNull Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public AccountType() {
    }


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
