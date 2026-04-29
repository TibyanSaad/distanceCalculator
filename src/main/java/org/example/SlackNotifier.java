package org.example;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public class SlackNotifier {

    private static final String WEBHOOK_URL = AppConfig.get("SLACK_WEBHOOK_URL");

    public static void sendNewMinimum(String origin, String destination, String distance, String time) {
        String message = "\uD83D\uDE97 *Commute Alert — Best Time to Leave!*\n"
                +  origin   + "    ➡    "  + destination + "\n"
                + "📏 *Distance:* " + distance    + "\n"
                + "🕐 *Time:* "     + time +" (with current traffic)"+ "\n"+
                "🏆 *This is your NEW best commute time today!*\n"+
                "\uD83D\uDC49 Consider leaving NOW for the smoothest ride! \uD83D\uDFE2";

        send(message);
    }

    private static void send(String message) {
        try {
            String body = "{\"text\": \"" + message + "\"}";

            HttpResponse<String> response = Unirest.post(WEBHOOK_URL)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asString();

            if (response.getStatus() == 200) {
                System.out.println("✅ New minimum recorded! Slack notification sent.");
            } else {
                System.err.println("❌ Slack API error - Status: " + response.getStatus());
                System.err.println("   Response: " + response.getBody());
            }

        } catch (UnirestException e) {
            System.err.println("❌ Network error: Could not reach Slack API.");
            System.err.println("   Details: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while sending Slack notification.");
            System.err.println("   Details: " + e.getMessage());
        }
    }
}