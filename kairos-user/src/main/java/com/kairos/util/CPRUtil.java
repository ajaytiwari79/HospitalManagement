package com.kairos.util;

import java.time.LocalDate;

/**
 * Created by pavan on 14/2/18.
 */
public class CPRUtil {

    //Method for getting the DateOfBirth

    public static LocalDate getDateOfBirthFromCPR(String cprNumber) {
        LocalDate birthday=null;
        if (cprNumber==null){
            return null;

        }
        if (cprNumber.length()==9){
            cprNumber = "0"+cprNumber;
        }

        if (cprNumber!=null){
            Integer year= Integer.valueOf(cprNumber.substring(4,6));
            Integer month = Integer.valueOf(cprNumber.substring(2,4));
            Integer day= Integer.valueOf(cprNumber.substring(0,2));
            Integer century = Integer.parseInt(cprNumber.substring(6,7));

            if (century>=0 && century<=3){
                century = 1900;
            }
            if (century==4){
                if (year<=36){
                    century = 2000;
                }
                else {
                    century = 1900;
                }
            }
            if (century>=5 && century<=8){
                if (year<=57){
                    century =2000;
                }
                if (year>=58 && year<=99){
                    century = 1800;
                }
            }
            if (century==9){
                if (year<=36){
                    century = 2000;
                }
                else {
                    century = 1900;
                }
            }
            year = century+year;

            birthday = LocalDate.of(year, month, day);



        }
        return birthday;
    }
}
