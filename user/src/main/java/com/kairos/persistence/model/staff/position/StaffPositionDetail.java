package com.kairos.persistence.model.staff.position;

import com.kairos.dto.activity.tags.TagDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.ERROR_STAFF_EMPLOYEDSINCE_NOTNULL;

/**
 * Created by prabjot on 10/1/17.
 */
@Getter
@Setter
public class StaffPositionDetail {

    private String cardNumber;
    private String sendNotificationBy;
    private String email;
    private boolean copyKariosMailToLogin;
    @NotNull(message = ERROR_STAFF_EMPLOYEDSINCE_NOTNULL)
    private String employedSince;
    private long visitourId;
    private long engineerTypeId;
    private Long timeCareExternalId;
    private LocalDate dateOfBirth;

}
