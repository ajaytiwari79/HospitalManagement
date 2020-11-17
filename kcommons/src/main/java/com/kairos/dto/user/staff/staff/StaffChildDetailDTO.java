package com.kairos.dto.user.staff.staff;

import com.kairos.commons.annotation.CPRValidation;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.enums.Gender;
import com.kairos.utils.CPRUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created By G.P.Ranjan on 5/11/19
 **/
@Getter
@Setter
@NoArgsConstructor
public class StaffChildDetailDTO {
    private Long id;
    private String name;
    @CPRValidation(message = "error.cpr.number.not.valid")
    private String cprNumber;
    private boolean childCustodyRights;
    private Map<String,String> translatedNames;
    private Map<String,String> translatedDescriptions;
    private Map<String, TranslationInfo> translations;

    public Gender getGender(){
        return CPRUtil.getGenderFromCPRNumber(this.getCprNumber());
    }

    public LocalDate getDateOfBirth(){
        return CPRUtil.fetchDateOfBirthFromCPR(this.getCprNumber());
    }

    public String getName(){
        return TranslationUtil.getName(TranslationUtil.convertUnmodifiableMapToModifiableMap(translations),name);
    }
}