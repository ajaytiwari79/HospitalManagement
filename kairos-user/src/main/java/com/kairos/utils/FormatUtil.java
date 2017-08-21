package com.kairos.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 16/2/17.
 */
public class FormatUtil {

    public static List<Map<String,Object>> formatNeoResponse(List<Map<String, Object>> queryData) {
        List<Map<String,Object>> response = new ArrayList<>();
        queryData.forEach(data->{
            response.add((Map<String, Object>) data.get("result"));
        });
        return response;
    }
}
