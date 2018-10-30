package com.kairos;

import com.kairos.service.fls_visitour.dynamic_change.FLSVisitourChangeService;
import com.kairos.service.payroll_system.PayRollSystemService;
import com.kairos.service.phase.PhaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Creates below mentioned bootstrap data(if Not Available)
 * 1. User
 * 2. Role
 * 3. Organization
 * 4. Units
 */
@Component
public class AppBootstrapListener implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    FLSVisitourChangeService flsVisitourChangeService;
    @Autowired
    private PhaseService phaseService;
    @Inject
    private PayRollSystemService payRollSystemService;

    /**
     * Executes on application ready event
     * Check's if data exists & calls createUsersAndRolesData
     */

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        flsVisitourChangeService.registerReceiver("visitourChange");
        payRollSystemService.createDefaultPayRollSystemList();

    }
}
