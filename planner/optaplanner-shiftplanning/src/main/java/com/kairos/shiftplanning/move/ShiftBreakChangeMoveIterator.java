package com.kairos.shiftplanning.move;

import java.util.Iterator;
import java.util.List;

public class ShiftBreakChangeMoveIterator implements Iterator<ShiftBreakChangeMove> {
    List<ShiftBreakChangeMove> shiftBreakChangeMoveList;
    int n=0;

    public ShiftBreakChangeMoveIterator(List<ShiftBreakChangeMove> shiftBreakChangeMoveList) {
        this.shiftBreakChangeMoveList = shiftBreakChangeMoveList;
    }

    @Override
    public boolean hasNext() {
        return n<shiftBreakChangeMoveList.size();
    }

    @Override
    public ShiftBreakChangeMove next() {
        return shiftBreakChangeMoveList.get(n++);
    }
}
