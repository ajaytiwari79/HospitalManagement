package com.kairos.persistence.model.user.pay_level;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 27/12/17.
 */
public class PayLevelGlobalDataWrapper {

    private List<PayLevelGlobalData> organizationTypes;
    private Map<String,PaymentUnit> paymentUnits;

    public PayLevelGlobalDataWrapper() {
        //default constructor
    }

    public PayLevelGlobalDataWrapper(List<PayLevelGlobalData> organizationTypes, Map<String, PaymentUnit> paymentUnits) {
        this.organizationTypes = organizationTypes;
        this.paymentUnits = paymentUnits;
    }

    public List<PayLevelGlobalData> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<PayLevelGlobalData> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Map<String, PaymentUnit> getPaymentUnits() {
        return paymentUnits;
    }

    public void setPaymentUnits(Map<String, PaymentUnit> paymentUnits) {
        this.paymentUnits = paymentUnits;
    }
}
