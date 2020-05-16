package com.kairos.persistence.model.staff;

import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 4/12/17.
 */
@Getter
@Setter
public class StaffUploadBySheetQueryResult {
    private List<StaffDTO> staffList = new ArrayList<>();
    private List<StaffDTO> staffErrorList = new ArrayList<>();
}
