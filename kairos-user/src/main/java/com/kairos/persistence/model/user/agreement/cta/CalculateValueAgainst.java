package com.kairos.persistence.model.user.agreement.cta;

public class CalculateValueAgainst {
    private  String calculateValue;
    private float scale;
    private FixedValue fixedValue;
    private  class FixedValue{
        private float amount;
        private Long currencyId;
        private Type type;
        public float getAmount() {
            return amount;
        }

        public void setAmount(float amount) {
            this.amount = amount;
        }

        public Long getCurrencyId() {
            return currencyId;
        }

        public void setCurrencyId(Long currencyId) {
            this.currencyId = currencyId;
        }
        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

    }

    public CalculateValueAgainst() {
        //default constractor
    }



    public String getCalculateValue() {
        return calculateValue;
    }

    public void setCalculateValue(String calculateValue) {
        this.calculateValue = calculateValue;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public FixedValue getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(FixedValue fixedValue) {
        this.fixedValue = fixedValue;
    }

    public  enum Type{
        PER_DAY,PER_ACTIVITY,PER_TASK;
    }


}
