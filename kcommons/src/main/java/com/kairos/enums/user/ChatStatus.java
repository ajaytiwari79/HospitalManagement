package com.kairos.enums.user;

public enum ChatStatus {

    ONLINE("Online"), OFFLINE("Offline"), IDLE("Idle"), AWAY("Away");
    public String value;

    ChatStatus(String value) {
        this.value = value;
    }

    public static ChatStatus getByValue(String value) {
        for (ChatStatus chatStatus : ChatStatus.values()) {
            if (chatStatus.value.equals(value)) {
                return chatStatus;
            }
        }
        return null;
    }

}