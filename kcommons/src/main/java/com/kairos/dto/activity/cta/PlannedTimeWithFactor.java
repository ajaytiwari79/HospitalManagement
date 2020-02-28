package com.kairos.dto.activity.cta;

import com.kairos.enums.cta.AccountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
public class PlannedTimeWithFactor {
    private float scale;
    private boolean add;
    private boolean subtract;
    private AccountType accountType;

    public PlannedTimeWithFactor(float scale, boolean add, AccountType accountType) {
        this.scale = scale;
        this.add = add;
        this.accountType = accountType;
    }

    public static PlannedTimeWithFactor buildPlannedTimeWithFactor(float scale, boolean add, AccountType accountType){
        return new PlannedTimeWithFactor(scale,add,accountType);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannedTimeWithFactor that = (PlannedTimeWithFactor) o;
        return Float.compare(that.scale, scale) == 0 &&
                add == that.add &&
                subtract == that.subtract &&
                accountType == that.accountType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(scale, add, subtract, accountType);
    }
}
