package com.tiffino.tiffino.util;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class GiftCardUtil {

    // 6-digit numeric code
    public static String generateGiftCardCode() {
        int number = 100000 + ThreadLocalRandom.current().nextInt(900000);
        return String.valueOf(number);
    }

    // Random description template with discount
    public static String generateDescription(String durationType, double discountPercent) {
        String[] templates = new String[] {
                "🎁 Welcome Back! Flat %d%% off on your next %s plan",
                " Loyalty Reward: %d%% off for your next %s plan",
                "🎉 Surprise Discount: %d%% off on next %s plan",
                " Renewal Bonus: %d%% off for your next %s plan"
        };
        int pick = ThreadLocalRandom.current().nextInt(templates.length);
        return String.format(templates[pick], (int) discountPercent, durationType);
    }

    // Random discount between 10% to 30%
    public static double generateRandomDiscount() {
        return 10 + ThreadLocalRandom.current().nextDouble(21); // 10% to 30%
    }
}
