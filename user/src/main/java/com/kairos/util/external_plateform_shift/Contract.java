package com.kairos.util.external_plateform_shift;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by oodles on 14/12/16.
 */
public class Contract {
    @JacksonXmlProperty
    private String Active;
    @JacksonXmlProperty
    private String ExternalContractId;
    @JacksonXmlProperty
    private String Id;
    @JacksonXmlProperty
    private String Compulsory;
    @JacksonXmlProperty
    private String Reference;

    @JacksonXmlProperty
    private String IsExportable;

    public Contract() {

    }

    @Override
    public String toString() {
        return "ClassPojo [Active = " + Active + ", ExternalContractId = " + ExternalContractId + ", Id = " + Id + ", Compulsory = " + Compulsory + ", Reference = " + Reference + ", IsExportable = " + IsExportable + "]";
    }
}
