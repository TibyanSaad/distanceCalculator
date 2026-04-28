package org.example;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class GoogleMapsService {


    static final String API_KEY = "api key";

    static final String ORIGIN = "23.5879,58.4472";
    static final String originName = "Home";
    static final String DESTINATION = "25.282307,55.390013";
    static final String destinationName = "Dubai Residential Oasis";

    public static void distance() {
        HttpResponse<String> response = Unirest.get("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryString("origins",      ORIGIN)
                .queryString("destinations", DESTINATION)
                .queryString("key",          API_KEY)
                .asString();

        String json = response.getBody();

        String distance = extractValue(json, "\"distance\"");
        String duration = extractValue(json, "\"duration\"");

        System.out.println("From     : " + originName);
        System.out.println("To       : " + destinationName);
        System.out.println("Distance : " + distance);
        System.out.println("Duration : " + duration);

        Unirest.shutDown();
    }

    // Extracts the "text" field from a named block e.g. "distance" or "duration"
    static String extractValue(String json, String blockName) {
        int blockIndex = json.indexOf(blockName);
        if (blockIndex == -1) return "N/A";
        int textIndex = json.indexOf("\"text\"", blockIndex);
        if (textIndex == -1) return "N/A";
        int start = json.indexOf("\"", textIndex + 7) + 1;
        int end   = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
//java -jar /mnt/c/Users/MOBPC/Desktop/New\ folder/distanceCalculator/target/distanceCalculator-1.0-SNAPSHOT.jar