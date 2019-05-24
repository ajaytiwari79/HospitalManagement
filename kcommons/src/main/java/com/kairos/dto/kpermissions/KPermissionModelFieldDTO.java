package com.kairos.dto.kpermissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KPermissionModelFieldDTO {
    private String modelName;
    private List<String> modelFields = new ArrayList<>();
}
