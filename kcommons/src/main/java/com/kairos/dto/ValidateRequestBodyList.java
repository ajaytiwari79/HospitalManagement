package com.kairos.dto;

import lombok.*;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ValidateRequestBodyList<T> {
    @Valid
    private List<T> requestBody;
}
