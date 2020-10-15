package com.kairos.service.protected_day_off;

import com.kairos.persistence.repository.protected_day_off.ProtectedDaysOffRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ProtectedDaysOffService {

    @Inject
    private ProtectedDaysOffRepository protectedDaysOffRepository;
}
