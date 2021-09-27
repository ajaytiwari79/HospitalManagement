package com.kairos.persistence.enums.task_type;

/**
 * Created by prabjot on 11/7/17.
 */
public enum DelayPenalty implements Serializable {
    AVOID(0),ALLOW(0.1F),PROHIBITED(1),STRONGLY_PROHIBITED(10);

    public float value;

    DelayPenalty(float value) {
        this.value = value;
    }

    public static DelayPenalty getByValue(int value) {
        for (DelayPenalty type : DelayPenalty.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }


}
