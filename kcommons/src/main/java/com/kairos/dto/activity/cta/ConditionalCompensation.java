package com.kairos.dto.activity.cta;

import com.kairos.enums.cta.ConditionalCompensationType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ConditionalCompensation {
    @Builder.Default
    private List<ConditionalCompensationType> conditionalCompensationTypes = new ArrayList<>();
}
