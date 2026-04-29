package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GoogleMapsService.startPolling();
        GoogleMapsService.distanceAndTimeCalculator();
    }
}