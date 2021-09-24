package com.kairos.persistence.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by oodles on 25/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@NodeEntity
public class AccessToLocation extends UserBaseEntity {
    // Lock & Emergency Information

    private String alarmCode;
    private String alarmCodeDescription;
    private boolean haveAlarmCode;


    private String keySystem;
    private String keySystemDescription;

    private String portPhoneNumber;
    private String emergencyCallNumber;
    private String emergencyCallDeviceType;
    private String serialNumber;
    private String reasonForEmergencyCall;
    private boolean dailyPhoneCallIsAgreed;
    private String reasonForDailyPhoneCall;
    private String howToAccessAddress;
    private String accessPhotoURL;
    private String remarks;


    public AccessToLocation() {
    }

    public AccessToLocation(String keySystem, String portPhoneNumber, String emergencyCallNumber,
                            String emergencyCallDeviceType, String serialNumber,
                            String reasonForEmergencyCall, String keySystemDescription,
                            boolean dailyPhoneCallIsAgreed, String reasonForDailyPhoneCall,
                            String howToAccessAddress, String remarks) {
        this.keySystem = keySystem;
        this.portPhoneNumber = portPhoneNumber;
        this.emergencyCallNumber = emergencyCallNumber;
        this.emergencyCallDeviceType = emergencyCallDeviceType;
        this.serialNumber = serialNumber;
        this.reasonForEmergencyCall = reasonForEmergencyCall;
        this.keySystemDescription = keySystemDescription;
        this.dailyPhoneCallIsAgreed = dailyPhoneCallIsAgreed;
        this.reasonForDailyPhoneCall = reasonForDailyPhoneCall;
        this.howToAccessAddress = howToAccessAddress;
        this.remarks = remarks;
    }


    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getAlarmCodeDescription() {
        return alarmCodeDescription;
    }

    public void setAlarmCodeDescription(String alarmCodeDescription) {
        this.alarmCodeDescription = alarmCodeDescription;
    }

    public boolean isHaveAlarmCode() {
        return haveAlarmCode;
    }

    public void setHaveAlarmCode(boolean haveAlarmCode) {
        this.haveAlarmCode = haveAlarmCode;
    }

    public String getKeySystem() {
        return keySystem;
    }

    public void setKeySystem(String keySystem) {
        this.keySystem = keySystem;
    }

    public String getPortPhoneNumber() {
        return portPhoneNumber;
    }

    public void setPortPhoneNumber(String portPhoneNumber) {
        this.portPhoneNumber = portPhoneNumber;
    }

    public String getEmergencyCallNumber() {
        return emergencyCallNumber;
    }

    public void setEmergencyCallNumber(String emergencyCallNumber) {
        this.emergencyCallNumber = emergencyCallNumber;
    }

    public String getEmergencyCallDeviceType() {
        return emergencyCallDeviceType;
    }

    public void setEmergencyCallDeviceType(String emergencyCallDeviceType) {
        this.emergencyCallDeviceType = emergencyCallDeviceType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getReasonForEmergencyCall() {
        return reasonForEmergencyCall;
    }

    public void setReasonForEmergencyCall(String reasonForEmergencyCall) {
        this.reasonForEmergencyCall = reasonForEmergencyCall;
    }

    public String getKeySystemDescription() {
        return keySystemDescription;
    }

    public void setKeySystemDescription(String keySystemDescription) {
        this.keySystemDescription = keySystemDescription;
    }

    public boolean isDailyPhoneCallIsAgreed() {
        return dailyPhoneCallIsAgreed;
    }

    public void setDailyPhoneCallIsAgreed(boolean dailyPhoneCallIsAgreed) {
        this.dailyPhoneCallIsAgreed = dailyPhoneCallIsAgreed;
    }

    public String getReasonForDailyPhoneCall() {
        return reasonForDailyPhoneCall;
    }

    public void setReasonForDailyPhoneCall(String reasonForDailyPhoneCall) {
        this.reasonForDailyPhoneCall = reasonForDailyPhoneCall;
    }

    public String getHowToAccessAddress() {
        return howToAccessAddress;
    }

    public void setHowToAccessAddress(String howToAccessAddress) {
        this.howToAccessAddress = howToAccessAddress;
    }

    public String getAccessPhotoURL() {
        return accessPhotoURL;
    }

    public void setAccessPhotoURL(String accessPhotoURL) {
        this.accessPhotoURL = accessPhotoURL;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
