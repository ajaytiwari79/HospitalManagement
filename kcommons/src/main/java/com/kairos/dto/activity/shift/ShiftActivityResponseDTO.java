package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 19/9/18
 */
@Getter
@Setter
@NoArgsConstructor
public class ShiftActivityResponseDTO {

    private BigInteger id;
    private List<ShiftActivityDTO> activities = new ArrayList<>();

    public ShiftActivityResponseDTO(BigInteger id) {
        this.id = id;
    }

}
