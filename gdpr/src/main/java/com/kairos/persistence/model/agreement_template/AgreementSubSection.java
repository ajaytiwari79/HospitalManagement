package com.kairos.persistence.model.agreement_template;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AgreementSubSection extends AgreementSection {

    @ManyToOne
    @JoinColumn(name = "agreementSection_id")
    private AgreementSection agreementSection;

}
