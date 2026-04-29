package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GoogleMapsService.startPolling();
        System.out.println("\uD83D\uDD04 Checking commute...");
        GoogleMapsService.distanceAndTimeCalculator();
    }
}