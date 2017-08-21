package com.kairos.utils;

import java.util.UUID;

/**
 * Created by prabjot on 16/11/16.
 */
public class BadgeGenerator {

    private BadgeGenerator(){}

    public static String generateBadgeNumber(){
        return UUID.randomUUID().toString().toUpperCase();
    }
}
