package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class GoogleMapsService {

    public static void distanceAndTimeCalculator() {

        String apiKey = AppConfig.get("API_KEY");
        String baseURL = AppConfig.get("BASE_URL");
        String origin = AppConfig.get("ORIGIN");
        String destination = AppConfig.get("DESTINATION");

        String originName = "Home";
        String destinationName = "Codeline";

        HttpResponse<String> response = Unirest.get(baseURL)
                .queryString("origins", origin)
                .queryString("destinations", destination)
                .queryString("key", apiKey)
                .asString();

        JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();

        // extracting elements from json obj
        JsonObject element = json.getAsJsonArray("rows")
                .get(0).getAsJsonObject()
                .getAsJsonArray("elements")
                .get(0).getAsJsonObject();

        //getting distance and duration as string value
        String distance = element.getAsJsonObject("distance").get("text").getAsString();
        String time = element.getAsJsonObject("duration").get("text").getAsString();

        System.out.println("\uD83D\uDCCD Distance : " + distance);
        System.out.println("\uD83D\uDD50 Travel Time : " + time);
        System.out.println("From     : " + originName);
        System.out.println("To       : " + destinationName);

        Unirest.shutDown();
    }
}
//java -jar /mnt/c/Users/MOBPC/Desktop/New\ folder/distanceCalculator/target/distanceCalculator-1.0-SNAPSHOT.jar