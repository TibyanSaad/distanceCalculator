package org.example;

import java.time.format.DateTimeFormatter;

public class CommuteMonitor {

    private String minimumTime         = null;
    private int    minimumTimeMinutes  = Integer.MAX_VALUE;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void check(String timeInTraffic, String distance) {

        int currentMinutes = parseMinutes(timeInTraffic);


        if (minimumTime == null) {
            minimumTime        = timeInTraffic;
            minimumTimeMinutes = currentMinutes;
            return;
        }

        if (currentMinutes < minimumTimeMinutes) {
            minimumTime        = timeInTraffic;
            minimumTimeMinutes = currentMinutes;
            System.out.println("🏆 Best Time so Far: " + minimumTime);
            SlackNotifier.sendNewMinimum(GoogleMapsService.originName,GoogleMapsService.destinationName,minimumTime,distance);

        } else if (currentMinutes > minimumTimeMinutes) {
            System.out.println("🔴 Traffic increased: " + timeInTraffic
                    + " (minimum is " + minimumTime + ")");

        } else {
            System.out.println(" No change: " + timeInTraffic);
        }
    }

    // if hours and minutes used it changes it to minutes
    private int parseMinutes(String time) {
        int total = 0;
        if (time.contains("hour")) {
            String[] parts = time.split("hour");
            total += Integer.parseInt(parts[0].trim()) * 60;
            if (parts.length > 1 && parts[1].contains("min")) {
                total += Integer.parseInt(parts[1].replace("mins", "").replace("min", "").trim());
            }
        } else if (time.contains("min")) {
            total = Integer.parseInt(time.replace("mins", "").replace("min", "").trim());
        }
        return total;
    }
}