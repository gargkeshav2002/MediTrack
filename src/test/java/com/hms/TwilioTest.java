package com.hms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioTest {
    // Replace with your real credentials from Twilio Console
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    public static final String TWILIO_NUMBER = "";  // Your Twilio number
    public static final String RECEIVER_NUMBER = ""; // Your verified personal number

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber(RECEIVER_NUMBER), // to
                new PhoneNumber(TWILIO_NUMBER),   // from
                "Hello from Hospital Management System! Your SMS is working 🚑"
        ).create();

        System.out.println("Message SID: " + message.getSid());
    }
}
