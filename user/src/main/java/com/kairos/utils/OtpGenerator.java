package com.kairos.utils;

import java.util.Random;

/**
 * Created by prabjot on 2/12/16.
 */
public class OtpGenerator {

    private OtpGenerator() {
    }

    public static int generateOtp() {
        Random random = new Random();
        return (100000 + random.nextInt(900000));
    }
}
