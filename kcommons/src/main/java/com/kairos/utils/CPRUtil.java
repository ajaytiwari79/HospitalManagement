package com.kairos.utils;

import com.kairos.enums.Gender;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Period;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.constants.CommonMessageConstants.MESSAGE_CPRNUMBER_NOT_VALID;

/**
 * Created by pavan on 14/2/18.
 */
public class CPRUtil {

    public static Integer getAgeFromCPRNumber(String cprNumber) {
        return StringUtils.isNotBlank(cprNumber) ? Period.between(getDateOfBirthFromCPR(cprNumber), LocalDate.now()).getYears() : 0;
    }

    public static Integer getAgeByCPRNumberAndStartDate(String cprNumber, LocalDate startDate) {
        return StringUtils.isNotBlank(cprNumber) ? Period.between(getDateOfBirthFromCPR(cprNumber), startDate).getYears() : 0;
    }

    public static LocalDate fetchDateOfBirthFromCPR(String cprNumber) {
        return cprNumber != null ? getDateOfBirthFromCPR(cprNumber) : null;
    }

    public static Gender getGenderFromCPRNumber(String cprNumber) {
        if (StringUtils.isEmpty(cprNumber)) {
            return null;
        }
        return Integer.parseInt(cprNumber.substring(cprNumber.length() - 1)) % 2 == 0 ? Gender.FEMALE : Gender.MALE;
    }

    //Method for getting the DateOfBirth From CPR Number
    public static LocalDate getDateOfBirthFromCPR(String cprNumber) {
        if (StringUtils.isEmpty(cprNumber)) {
            return null;
        }
        if (cprNumber.length() == 9) {
            cprNumber = "0" + cprNumber;
        }
        Integer year = Integer.valueOf(cprNumber.substring(4, 6));
        int month = Integer.parseInt(cprNumber.substring(2, 4));
        int day = Integer.parseInt(cprNumber.substring(0, 2));
        Integer centuryDigit = Integer.parseInt(cprNumber.substring(6, 7));
        LocalDate birthday;
        year = getYearFromCPR(year, centuryDigit);
        if(month>12 || day>31){
            throwException(MESSAGE_CPRNUMBER_NOT_VALID);
        }
        birthday = LocalDate.of(year, month, day);
        return birthday;
    }

    private static Integer getYearFromCPR(Integer year, Integer centuryDigit) {
        int century = 1900;

        if (((centuryDigit == 4 || centuryDigit == 9) && year <= 36) || (centuryDigit >= 5 && centuryDigit <= 8 && year <= 57)) {
            century = 2000;
        } else if (centuryDigit >= 5 && centuryDigit <= 8 && year >= 58 && year <= 99) {
            century = 1800;
        }
        year = century + year;
        return year;
    }

}
