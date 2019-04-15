package com.planner.util.executor.runnables;

import com.kairos.planner.vrp.taskplanning.routes.Route;
import com.planner.domain.tomtomResponse.AtoBRoute;
import com.planner.repository.locationRepository.AtoBRouteRepository;
import com.planner.service.tomtomService.TomTomService;

import java.io.IOException;

public class TomtomRouteRunnable implements Runnable {
    private TomTomService tomTomService;
    private AtoBRouteRepository atoBRouteRepository;
    private AtoBRoute atoBRoute;

    public TomtomRouteRunnable(TomTomService tomTomService, AtoBRouteRepository atoBRouteRepository, AtoBRoute atoBRoute) {
        this.tomTomService = tomTomService;
        this.atoBRouteRepository = atoBRouteRepository;
        this.atoBRoute = atoBRoute;
    }

    @Override
    public void run() {
        Route route = null;
        try {
            route = tomTomService.submitToTomtomForRoute(atoBRoute.getFirstLatitude(), atoBRoute.getFirstLongitude(), atoBRoute.getSecondLattitude(), atoBRoute.getSecondLongitude());
        } catch (IOException e) {

        }
        atoBRoute.setRoute(route);
        atoBRouteRepository.saveEntity(atoBRoute);
    }
}
