package com.kairos.messaging.wshandlers;

import com.kairos.dto.activity.staffing_level.StaffingLevelGraphDTO;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

public class StaffingLevelGraphCustomStompFrameHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return StaffingLevelGraphDTO.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }
}
