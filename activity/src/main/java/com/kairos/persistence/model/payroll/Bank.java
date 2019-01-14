package com.kairos.persistence.model.payroll;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/*
 *Created By Pavan on 17/12/18
 *
 */
@Document
public class Bank extends MongoBaseEntity {
    private String name;
    private String description;
    private String registrationNumber;
    private String internationalAccountNumber;
    private String swiftCode; //stands for Society for Worldwide Interbank Financial Telecommunication
    private Long countryId;
    private Long organizationId;
    private Long staffId;
    private Long accountNumber;

    public Bank() {
        //Default Constructor
    }


    public Bank(BigInteger id,String name, String description, String registrationNumber, String internationalAccountNumber, String swiftCode,Long countryId) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.registrationNumber = registrationNumber;
        this.internationalAccountNumber = internationalAccountNumber;
        this.swiftCode = swiftCode;
        this.countryId=countryId;
    }

    public Bank(Long staffId,BigInteger id,String name, String description, String registrationNumber, String internationalAccountNumber, String swiftCode) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.registrationNumber = registrationNumber;
        this.internationalAccountNumber = internationalAccountNumber;
        this.swiftCode = swiftCode;
        this.staffId=staffId;
    }

    public Bank(BigInteger id,String name, String description, String registrationNumber, String internationalAccountNumber, Long organizationId,String swiftCode) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.registrationNumber = registrationNumber;
        this.internationalAccountNumber = internationalAccountNumber;
        this.swiftCode = swiftCode;
        this.staffId=staffId;
    }

    public String getName() {
        return name;
    }


    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getInternationalAccountNumber() {
        return internationalAccountNumber;
    }

    public void setInternationalAccountNumber(String internationalAccountNumber) {
        this.internationalAccountNumber = internationalAccountNumber;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }
}
