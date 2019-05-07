package com.kairos.persistence.model.staff;
/*
 *Created By Pavan on 6/5/19
 *
 */

import com.kairos.persistence.model.organization.StaffTeamRelationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;

@Getter
@Setter
@NoArgsConstructor
public class StaffTeamDTO {
    private Long staffId;
    private Long teamId;
    private StaffTeamRelationship.TeamType teamType;
    private boolean mainTeamLeader;
    private boolean actingTeamLeader;

    @AssertTrue(message = "message.same_staff.belongs_to.both_lead")
    public boolean isValid() {
        return mainTeamLeader && actingTeamLeader;
    }

}
