package org.example;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;

public class SlackNotifier {

    private static final String WEBHOOK_URL = AppConfig.get("SLACK_WEBHOOK_URL");

    public static void sendNewMinimum(String time) {
        String message = "🏆 New minimum commute recorded: *" + time + "*";
        send(message);
    }

    private static void send(String message) {
        String body = "{\"text\": \"" + message + "\"}";

        HttpResponse<String> response = Unirest.post(WEBHOOK_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .asString();

        if (response.getStatus() == 200) {
            System.out.println("✅ Slack notification sent.");
        } else {
            System.err.println("❌ Failed to send Slack notification: " + response.getBody());
        }
    }
}