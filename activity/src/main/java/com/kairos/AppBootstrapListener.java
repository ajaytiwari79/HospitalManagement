package com.kairos;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Creates below mentioned bootstrap data(if Not Available)
 * 1. User
 * 2. Role
 * 3. Organization
 * 4. Units
 */
@Component
public class AppBootstrapListener implements ApplicationListener<ApplicationReadyEvent> {



    /**
     * Executes on application ready event
     * Check's if data exists & calls createUsersAndRolesData
     */

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        //flsVisitourChangeService.registerReceiver("visitourChange");
       // payRollSystemService.createDefaultPayRollSystemList();

    }
}
