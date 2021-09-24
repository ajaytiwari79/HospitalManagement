package com.kairos.dto.user.staff.staff;

import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by oodles on 3/2/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class Staff {

    private Long id;
    private String firstName;
    private String lastName;
    private String profilePic;
    private List<Long> skills;
    private StaffStatusEnum currentStatus;

    public Staff(Long id, String firstName, String lastName, List<Long> skills, StaffStatusEnum currentStatus) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
        this.currentStatus = currentStatus;
    }

}
