package com.kairos.utils;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by oodles on 4/9/17.
 */
public class ApplicationUtil {

    public static List<BigInteger> removingDuplicates(List<BigInteger> listOne, List<BigInteger> listTwo){
        Set<BigInteger> set = new HashSet<>(listOne);
        set.addAll(listTwo);
        return new ArrayList<BigInteger>(set);
    }

    public static List<BigInteger> removingDuplicatesFromOneList(List<BigInteger> listOne){
        Set<BigInteger> set = new HashSet<>(listOne);
        return new ArrayList<BigInteger>(set);
    }
}
