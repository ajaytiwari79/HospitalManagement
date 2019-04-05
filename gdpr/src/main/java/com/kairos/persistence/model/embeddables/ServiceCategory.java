package com.kairos.persistence.model.embeddables;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategory {

    @NotNull(message = "id can't be null")
    private Long id;

    @NotBlank(message = "name can't be null or empty")
    private String name;

}
