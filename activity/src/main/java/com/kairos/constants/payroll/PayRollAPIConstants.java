package com.kairos.constants.payroll;
/*
 *Created By Pavan on 18/12/18
 *
 */

public class PayRollAPIConstants {
    //PayRoll
    public static final String PAYROLL="/payroll";
    public static final String DELETE_PAYROLL="/payroll/{payRollId}";
    public static final String UPDATE_PAYROLL="/payroll/{payRollId}";
    public static final String GET_PAYROLL_BY_ID="/payroll/{payRollId}";

    //Bank
    public static final String BANK="/bank";
    public static final String DELETE_BANK="/bank/{bankId}";
    public static final String UPDATE_BANK="/bank/{bankId}";
    public static final String GET_BANK_BY_ID="/bank/{bankId}";
    public static final String STAFF_BANK_DETAILS="/staff/{staffId}/staff_bank_details";
    public static final String ORGANIZATION_BANK_DETAILS="/organization_bank_details";

    // Pension provider
    public static final String PENSION_PROVIDER="/pension_provider";
    public static final String DELETE_PENSION_PROVIDER="/pension_provider/{pensionProviderId}";
    public static final String UPDATE_PENSION_PROVIDER="/pension_provider/{pensionProviderId}";
    public static final String GET_PENSION_PROVIDER_BY_ID="/pension_provider/{pensionProviderId}";
}
