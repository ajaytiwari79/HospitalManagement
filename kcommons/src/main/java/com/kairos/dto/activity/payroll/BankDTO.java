package com.kairos.dto.activity.payroll;
/*
 *Created By Pavan on 17/12/18
 *
 */

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

public class BankDTO {
    private BigInteger id;
    @NotBlank(message = "name.absent")
    private String name;
    private String description;
    @NotBlank(message = "registrationNumber.absent")
    private String registrationNumber;
    @NotBlank(message = "internationalAccountNumber.absent")
    private String internationalAccountNumber;
    @NotBlank(message = "swiftCode.absent")
    private String swiftCode; //stands for Society for Worldwide Interbank Financial Telecommunication

    public BankDTO() {
        //Default Constructor
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
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
        this.registrationNumber = registrationNumber.trim();
    }

    public String getInternationalAccountNumber() {
        return internationalAccountNumber;
    }

    public void setInternationalAccountNumber(String internationalAccountNumber) {
        this.internationalAccountNumber = internationalAccountNumber.trim();
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode.trim();
    }
}
