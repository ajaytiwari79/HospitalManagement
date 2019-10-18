package com.kairos.dto.user.user.password;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * Created by prabjot on 6/10/17.
 */
@Getter
@Setter
public class FirstTimePasswordUpdateDTO {

    @Size(min = 6,max = 50)
    private char[] password;
    @Size(min = 6,max = 50)
    private char[] repeatPassword;
    @Email
    private String email;


    @AssertTrue(message="The password fields mush match")
    private boolean isValid() {
        return Arrays.equals(this.password,this.repeatPassword);
    }
}
