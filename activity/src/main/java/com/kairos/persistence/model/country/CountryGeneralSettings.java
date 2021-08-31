package com.kairos.persistence.model.country;

import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryGeneralSettings extends MongoBaseEntity {
    private Long unitId;
    private Long countryId;
    private boolean shiftCreationAllowForStaff;
    private boolean shiftCreationAllowForManagement;
}
