package com.kairos.utils;

import com.kairos.commons.utils.ArrayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by vipul on 2/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ArrayUtilUnitTest {
    @InjectMocks
    ArrayUtil arrayUtil;

    @Test
    public void getUniqueElementWhichIsNotInFirst() throws Exception {
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
}