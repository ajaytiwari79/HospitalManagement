package com.planner.domain.tomtomResponse;

/**
 * @author pradeep
 * @date - 8/6/18
 */

public class Summary {
    private int successfulRoutes;
    private int totalRoutes;

    public int getSuccessfulRoutes() {
        return successfulRoutes;
    }

    public void setSuccessfulRoutes(int successfulRoutes) {
        this.successfulRoutes = successfulRoutes;
    }

    public int getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(int totalRoutes) {
        this.totalRoutes = totalRoutes;
    }
}
