package com.kairos.persistence.model.embeddables;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountType {

    @NotNull
    private Long id;
    @NotNull
    private String name;

}
