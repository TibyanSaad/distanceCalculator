package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GoogleMapsService {
    public static void startPolling() throws InterruptedException {

        int intervalMinutes = Integer.parseInt(AppConfig.get("POLLING_INTERVAL_MINUTES"));
        long intervalMillis = intervalMinutes * 60 * 1000L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        System.out.println("🕐 " + LocalTime.now().format(formatter));

        while (true) {
            distanceAndTimeCalculator();
            Thread.sleep(intervalMillis);
        }
    }

    public static void distanceAndTimeCalculator() {

        String apiKey       = AppConfig.get("API_KEY");
        String baseURL      = AppConfig.get("BASE_URL");
        String origin       = AppConfig.get("ORIGIN");
        String destination  = AppConfig.get("DESTINATION");

        String originName      = "Home";
        String destinationName = "Codeline";

        HttpResponse<String> response;
        try {
            response = Unirest.get(baseURL)
                    .queryString("origins",             origin)
                    .queryString("destinations",        destination)
                    .queryString("mode",                "driving")
                    .queryString("routing_preference",  "TRAFFIC_AWARE")
                    .queryString("departure_time",      "now")
                    .queryString("key",                 apiKey)
                    .asString();
        } catch (UnirestException e) {
            // no internet DNS failure connection timeout
            System.err.println("❌ Network error: Could not reach Google Maps API.");
            System.err.println("   → Check your internet connection and try again.");
            System.err.println("   Details: " + e.getMessage());
            return;
        }

        if (response.getStatus() != 200) {
            System.err.println("❌ API request failed with HTTP status: " + response.getStatus());
            System.err.println("   Response: " + response.getBody());
            return;
        }

        //json api failure
        JsonObject json;
        try {
            json = JsonParser.parseString(response.getBody()).getAsJsonObject();
        } catch (Exception e) {
            System.err.println("❌ Failed to parse API response as JSON.");
            System.err.println("   Raw response: " + response.getBody());
            return;
        }

        // google api failure
        String apiStatus = json.get("status").getAsString();
        if (!apiStatus.equals("OK")) {
            switch (apiStatus) {
                case "REQUEST_DENIED":
                    System.err.println("❌ API request denied. Check your API key.");
                    break;
                case "INVALID_REQUEST":
                    System.err.println("❌ Invalid request. Check your origin/destination values.");
                    break;
                case "OVER_DAILY_LIMIT":
                    System.err.println("❌ API key over daily limit or billing issue.");
                    break;
                case "OVER_QUERY_LIMIT":
                    System.err.println("❌ Too many requests. You have exceeded your quota.");
                    break;
                case "UNKNOWN_ERROR":
                    System.err.println("❌ Unknown server error. Try again later.");
                    break;
                default:
                    System.err.println("❌ API returned status: " + apiStatus);
            }
            return;
        }

        JsonObject element;
        try {
            element = json.getAsJsonArray("rows")
                    .get(0).getAsJsonObject()
                    .getAsJsonArray("elements")
                    .get(0).getAsJsonObject();
        } catch (Exception e) {
            System.err.println("❌ Unexpected response structure from API.");
            return;
        }

        String elementStatus = element.get("status").getAsString();
        if (!elementStatus.equals("OK")) {
            switch (elementStatus) {
                case "NOT_FOUND":
                    System.err.println("❌ Origin or destination could not be found.");
                    break;
                case "ZERO_RESULTS":
                    System.err.println("❌ No route found between origin and destination.");
                    break;
                case "MAX_ROUTE_LENGTH_EXCEEDED":
                    System.err.println("❌ Route is too long to be processed.");
                    break;
                default:
                    System.err.println("❌ Element status: " + elementStatus);
            }
            return;
        }

        try {
            String distance       = element.getAsJsonObject("distance").get("text").getAsString();
            String time           = element.getAsJsonObject("duration").get("text").getAsString();
            String timeInTraffic  = element.getAsJsonObject("duration_in_traffic").get("text").getAsString();

            System.out.println("📍 From                      : " + originName);
            System.out.println("📍 To                        : " + destinationName);
            System.out.println("📏 Distance                  : " + distance);
            System.out.println("🕐 Travel Time (no traffic)  : " + time);
            System.out.println("🕐 Travel Time (in traffic)  : " + timeInTraffic);
            System.out.println("------------------------------------------------------------");

        } catch (Exception e) {
            System.err.println("❌ Could not extract distance/duration from response.");
            System.err.println("   Details: " + e.getMessage());
        } finally {
            Unirest.shutDown();
        }
    }
}