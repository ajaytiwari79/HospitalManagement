package com.kairos.dto.kpermissions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModelDTO {

    private Long id;
    private String modelName;

    private List<FieldDTO> fields = new ArrayList<>();

    List<ModelDTO> subModels = new ArrayList<>();
}
