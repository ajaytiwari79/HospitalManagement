package com.kairos.activity.enums.counter;

import java.util.HashMap;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public enum CounterSize {
    SIZE_1X1(1,1);

    private int height=0;
    private int width =0;

    private CounterSize(int height, int width){
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Map<String, Map> getCounterSizeMap(){
        Map<String, Map> counterSizeMap = new HashMap<String, Map>();
        for(CounterSize cs: CounterSize.values()){
            Map<String, Integer> sizeRatio = new HashMap<String, Integer>();
            sizeRatio.put("width", cs.getWidth());
            sizeRatio.put("height", cs.getHeight());
            counterSizeMap.put(cs.toString(), sizeRatio);
        }
        return counterSizeMap;
    }
}
