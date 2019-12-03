package com.kairos.dto.user.user.user_personalized_settings;

import com.kairos.enums.AbsenceViewSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by yatharth on 1/5/18.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelfRosteringViewDto {
    private AbsenceViewSettings absenceViewSettings;
}
