package com.kairos.dto.kpermissions;

import lombok.*;

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
