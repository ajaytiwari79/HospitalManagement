package com.kairos.persistence.model.agreement_template;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class AgreementSubSection extends AgreementSection {

    @ManyToOne
    @JoinColumn(name = "agreementSection_id")
    private AgreementSection agreementSection;

    public AgreementSection getAgreementSection() {
        return agreementSection;
    }

    public void setAgreementSection(AgreementSection agreementSection) {
        this.agreementSection = agreementSection;
    }

    public AgreementSubSection(){ }




}
