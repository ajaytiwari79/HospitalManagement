package com.kairos.persistence.model.open_shift;

public class FeatureRules {
   private boolean showCost;
   private boolean allowInstantPayment;
   private boolean allowTimeBank;
   private boolean chatWithPlanner;
   private boolean allowCounterProposal;
   private boolean allowComments;

    public FeatureRules() {
        //Default Constructor
    }

    public boolean isShowCost() {
        return showCost;
    }

    public void setShowCost(boolean showCost) {
        this.showCost = showCost;
    }

    public boolean isAllowInstantPayment() {
        return allowInstantPayment;
    }

    public void setAllowInstantPayment(boolean allowInstantPayment) {
        this.allowInstantPayment = allowInstantPayment;
    }

    public boolean isAllowTimeBank() {
        return allowTimeBank;
    }

    public void setAllowTimeBank(boolean allowTimeBank) {
        this.allowTimeBank = allowTimeBank;
    }

    public boolean isChatWithPlanner() {
        return chatWithPlanner;
    }

    public void setChatWithPlanner(boolean chatWithPlanner) {
        this.chatWithPlanner = chatWithPlanner;
    }

    public boolean isAllowCounterProposal() {
        return allowCounterProposal;
    }

    public void setAllowCounterProposal(boolean allowCounterProposal) {
        this.allowCounterProposal = allowCounterProposal;
    }

    public boolean isAllowComments() {
        return allowComments;
    }

    public void setAllowComments(boolean allowComments) {
        this.allowComments = allowComments;
    }
}
