package com.kairos.persistance.model.enums;

public enum  VersionNode {


    ROOT_VERSION("root version"), BASE_VERSION("base version"), VERSION("version");
    public String value;

     VersionNode(String value) {
        this.value = value;
    }


}
