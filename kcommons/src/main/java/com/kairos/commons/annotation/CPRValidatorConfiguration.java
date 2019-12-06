package com.kairos.commons.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

import static java.lang.Character.getNumericValue;
import static java.lang.Integer.parseInt;
import static java.util.Optional.empty;

/**
 * Created By G.P.Ranjan on 14/11/19
 **/
public class CPRValidatorConfiguration implements ConstraintValidator<CPRValidation, String> {
    private static final String VALID_FORMAT_PATTERN = "\\d{10}";
    @Override
    public void initialize(CPRValidation constraintAnnotation) {
        //This is Override method
    }

    @Override
    public boolean isValid(String cprNumber, ConstraintValidatorContext context) {
        return birthday(cprNumber).isPresent();
    }

    public static Optional<LocalDate> birthday(final String idNumber) {
        if (!validateFormat(idNumber)) {
            return empty();
        }
        int day= parseInt(idNumber.substring(0, 2));
        int month= parseInt(idNumber.substring(2, 4));
        int shortYear= parseInt(idNumber.substring(4, 6));
        int year = calculateYear(shortYear, getNumericValue(idNumber.charAt(6)));
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return empty();
        }
        return Optional.of(parsedDate);
    }

    private static int calculateYear(int shortYear, int yearCenturyPart) {
        int century;
        if (yearCenturyPart >= 0 && yearCenturyPart <= 3) {
            century = 1900;
        } else if (yearCenturyPart == 4 || yearCenturyPart == 9) {
            if (shortYear >= 0 && shortYear <= 36) {
                century = 2000;
            } else {
                century = 1900;
            }
        } else {
            if (shortYear >= 58 && shortYear <= 99) {
                century = 1800;
            } else {
                century = 2000;
            }
        }
        return century + shortYear;
    }

    private static boolean validateFormat(final String number) {
        return number.matches(VALID_FORMAT_PATTERN);
    }
}

