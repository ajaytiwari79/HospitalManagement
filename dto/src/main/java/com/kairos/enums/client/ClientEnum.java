package com.kairos.enums.client;

/**
 * Created by prabjot on 19/10/16.
 */
public enum ClientEnum {

    INDIVIDUAL,ORGANIZATION;

    public enum CivilianStatus {
        DEAD("Dead"), SINGLE("Single"), MARRIED("Married"), DIVORCED("Divorced"), LONGEST_LIVING_PARTNER("Longest living partner"), REGISTERED_PARTNERSHIP("Registered partnership");
        public String value;

        CivilianStatus(String value) {
            this.value = value;
        }

        public static CivilianStatus getByValue(final String value) {
            for (CivilianStatus civilianStatus : CivilianStatus.values()) {
                if (civilianStatus.value.equals(value)) {
                    return civilianStatus;
                }
            }
            return null;
        }
    }

    public enum CitizenShip {
        DANISH("Danish"),EU("EU"),NON_EU("NON-EU");
        public String value;

        CitizenShip(String value) {
            this.value = value;
        }

        public static CitizenShip getByValue(String value){
            for(CitizenShip citizenShip : CitizenShip.values()){
                if(citizenShip.value.equals(value)){
                    return citizenShip;
                }
            }
            return null;
        }
    }


}
