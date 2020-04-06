package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.enums.team.TeamType;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreferWorkingOnMainTeam implements Constraint {

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
}
