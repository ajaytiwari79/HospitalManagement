package com.kairos.dto.user.user.password;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * Created by prabjot on 6/10/17.
 */
@Getter
@Setter
public class PasswordUpdateByAdminDTO {

    @NotEmpty
    private char[] confirmPassword;

    @Size(min = 6,max = 50)
    private char[] newPassword;

    @AssertTrue(message="message.staff.user.password.notmatch")
    public boolean isValid() {
        return Arrays.equals(this.newPassword,this.confirmPassword);
    }
}
