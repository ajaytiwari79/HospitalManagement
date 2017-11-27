package com.kairos.service.agreement.cta;

import com.kairos.persistence.repository.user.agreement.cta.CTARuleTemplateGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Transactional
@Service
public class CostTimeAgreementService extends UserBaseService {
private @Autowired UserService userService;
private @Autowired CTARuleTemplateGraphRepository ctaRuleTemplateGraphRepository;

public void CreateDefaultCtaRuleTemplate(Long countryId){


}


}