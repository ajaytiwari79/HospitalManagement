package com.kairos.utils;

import com.kairos.commons.utils.ArrayUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by vipul on 2/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ArrayUtilUnitTest {
    @InjectMocks
    ArrayUtil arrayUtil;

    @Test
    public void getUniqueElementWhichIsNotInFirst() {
        List<Long> previousRes = new ArrayList<Long>();
        previousRes.add(1L);
        List<Long> originalRes = new ArrayList<Long>();
        originalRes.add(5L);
        List<Long> original = new ArrayList<Long>();
        original.add(1L);
        original.add(4L);
        original.add(7L);
        List<Long> previous = new ArrayList<Long>();
        previous.add(5L);
        previous.add(4L);
        previous.add(7L);
        List<Long> utilRes = ArrayUtil.getUniqueElementWhichIsNotInFirst(previous, original);
        assertEquals(previousRes, utilRes);
        utilRes = ArrayUtil.getUniqueElementWhichIsNotInFirst(original, previous);
        assertEquals(originalRes, utilRes);
    }

    @Test
    public void testing(){

        Map<String, TranslationInfo> stringTranslationInfoMap=new HashMap<>();
        TranslationInfo translationInfo=new TranslationInfo("pawan","pd");
        stringTranslationInfoMap.put("english",translationInfo);
        stringTranslationInfoMap.put("hindi",translationInfo);
        stringTranslationInfoMap.put("danish",translationInfo);
        stringTranslationInfoMap.put("urdu",translationInfo);
        String str= ObjectMapperUtils.objectToJsonString(stringTranslationInfoMap);
        Map<String, TranslationInfo> again=ObjectMapperUtils.jsonStringToObject(str,Map.class);
        System.out.println(again);

    }
}