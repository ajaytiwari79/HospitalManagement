package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.enums.team.TeamType;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Team;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PreferWorkingOnMainTeam implements ConstraintHandler {

    private ScoreLevel level;
    private int weight;

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        int penality = 0;
        Optional<Team> teamOptional = shift.getEmployee().getTeams().stream().filter(team -> TeamType.MAIN.equals(team.getTeamType())).findFirst();
        if(teamOptional.isPresent()){
            for (ShiftActivity shiftActivity : shift.getShiftActivities()) {
                if(!teamOptional.get().getId().equals(shiftActivity.getActivity().getTeamId())){
                    penality++;
                }
            }
        }
        return penality;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return 0;
    }

    @Override
    public int verifyConstraints(Unit unit, Shift shiftImp, List<Shift> shiftImps){return 0;};
}
