package com.kairos.persistence.model.agreement_template;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AgreementSubSection extends AgreementSection {

    @ManyToOne
    @JoinColumn(name = "agreementSection_id")
    private AgreementSection agreementSection;

}
