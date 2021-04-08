package com.kairos.dto.user.staff.staff;

import com.kairos.enums.StaffStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Long> employmentIds = new HashSet<>();

    public Staff(Long id, String firstName, String lastName, List<Long> skills, StaffStatusEnum currentStatus) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
        this.currentStatus = currentStatus;
    }

}
