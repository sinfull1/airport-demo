package com.example.demo;

public enum RouteDestination {

    ODD("order-channel-odd"),
    EVEN("order-channel-even");

    private String route;

    RouteDestination(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
