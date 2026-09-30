package com.tiffino.tiffino.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper service that builds a full subscription message:
 *  - step-by-step subscription guide
 *  - price calculation using project's rules
 *  - gift-card rules and generation notes
 *  - detects mealTimes, calories, allergies from free-text prompts
 *
 * Designed to be called from AiService when subscription-related question is detected.
 */
@Service
public class SubscriptionInfoService {

    // Base pricing rules (match your SubscriptionService)
    private static final double BASE_PRICE = 100.0;
    private static final double BREAKFAST_PRICE = 50.0;
    private static final double LUNCH_PRICE = 80.0;
    private static final double DINNER_PRICE = 70.0;
    private static final double CAL_OVER_1500 = 100.0;
    private static final double CAL_OVER_2000 = 200.0;
    private static final double ALLERGY_CHARGE = 10.0;

    // Gift card rules summary (based on your code)
    public String giftCardRulesText() {
        return "Gift Card rules:\n" +
                "• First subscription: No gift card is issued.\n" +
                "• After a previous subscription expires (and is not active), the system may generate a Gift Card for you.\n" +
                "• Gift cards are typically valid for a specific plan type (e.g., MONTHLY) and will show the discount percent.\n" +
                "• To use a gift card, provide its code during subscription; expired/invalid codes will be rejected.\n";
    }

    // Step-by-step subscription template
    public String subscriptionStepsText() {
        return "Subscription Steps:\n" +
                "Step 1: Open the Tiffino app and go to the 'Subscription' section.\n" +
                "Step 2: Choose a plan type (DAILY / WEEKLY / MONTHLY / QUARTERLY).\n" +
                "Step 3: Select meal times (BREAKFAST, LUNCH, DINNER) you want included.\n" +
                "Step 4: Enter desired calories per meal and any allergies (optional).\n" +
                "Step 5: (Optional) Enter a Gift Card code to apply a discount.\n" +
                "Step 6: Review final price and confirm payment.\n" +
                "Step 7: Your subscription will start immediately; you will see start and end dates in your profile.\n";
    }

    // Offer day text (2nd Wednesday)
    public String offerDayText() {
        return "Offer Day: Tiffino's official Offer Day is the 2nd Wednesday of every month — on that day typical discounts (e.g., 15%) may be applied.\n";
    }

    // Utility: parse meals from free text (breakfast/lunch/dinner)
    public List<String> parseMealTimes(String text) {
        if (text == null) return Collections.emptyList();
        String lower = text.toLowerCase();
        List<String> result = new ArrayList<>();
        if (lower.contains("breakfast")) result.add("BREAKFAST");
        if (lower.contains("lunch")) result.add("LUNCH");
        if (lower.contains("dinner")) result.add("DINNER");
        // also accept "all three", "all"
        if (result.isEmpty() && (lower.contains("all") || lower.contains("all three") || lower.contains("all meals"))) {
            result = Arrays.asList("BREAKFAST", "LUNCH", "DINNER");
        }
        return result;
    }

    // Utility: parse calories number (e.g., "70kg" or "calories 1800")
    public Integer parseCalories(String text) {
        if (text == null) return null;
        // capture "1800" if preceded by "cal", "calorie", or standalone "1800"
        Pattern p = Pattern.compile("\\b(\\d{3,4})\\b");
        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (Exception ignored) {}
        }
        return null;
    }

    // Utility: parse allergies as comma-separated tokens if user mentions "allergy: peanuts"
    public List<String> parseAllergies(String text) {
        if (text == null) return Collections.emptyList();
        // look for keywords "allerg" and take nearby words
        String lower = text.toLowerCase();
        if (!lower.contains("allerg")) return Collections.emptyList();

        // crude extraction: take words after "allergy" or "allergies"
        Pattern p = Pattern.compile("(?:allergy|allergies)[:\\s]+([a-zA-Z0-9,\\s]+)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            String group = m.group(1).trim();
            // split by comma or 'and'
            String[] parts = group.split(",| and | & ");
            return Arrays.stream(parts).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        }

        // fallback: return empty
        return Collections.emptyList();
    }

    // Price calculation using the same logic as your SubscriptionService
    public double calculatePriceEstimate(List<String> mealTimes, Integer caloriesPerMeal, List<String> allergies) {
        double price = BASE_PRICE;
        if (mealTimes != null) {
            for (String meal : mealTimes) {
                switch (meal.toUpperCase()) {
                    case "BREAKFAST" -> price += BREAKFAST_PRICE;
                    case "LUNCH"     -> price += LUNCH_PRICE;
                    case "DINNER"    -> price += DINNER_PRICE;
                }
            }
        }
        if (caloriesPerMeal != null) {
            if (caloriesPerMeal > 1500) price += CAL_OVER_1500;
            if (caloriesPerMeal > 2000) price += CAL_OVER_2000;
        }
        if (allergies != null && !allergies.isEmpty()) price += ALLERGY_CHARGE;
        return price;
    }

    // Build full single message combining steps + price estimate + gift card rules + offer day
    public String buildFullSubscriptionMessage(String userPrompt) {
        List<String> meals = parseMealTimes(userPrompt);
        Integer calories = parseCalories(userPrompt);
        List<String> allergies = parseAllergies(userPrompt);

        StringBuilder sb = new StringBuilder();
        sb.append(subscriptionStepsText()).append("\n");

        sb.append("Pricing Rules (summary):\n");
        sb.append(String.format("• Base price: ₹%.2f\n", BASE_PRICE));
        sb.append(String.format("• Breakfast: +₹%.2f\n", BREAKFAST_PRICE));
        sb.append(String.format("• Lunch: +₹%.2f\n", LUNCH_PRICE));
        sb.append(String.format("• Dinner: +₹%.2f\n", DINNER_PRICE));
        sb.append("• Calories surcharge: +₹100 (if >1500), +₹200 extra (if >2000)\n");
        sb.append(String.format("• Allergies surcharge: +₹%.2f (if any)\n\n", ALLERGY_CHARGE));

        if (!meals.isEmpty() || calories != null || !allergies.isEmpty()) {
            sb.append("Estimated Price for your inputs:\n");
            if (!meals.isEmpty()) sb.append("• Selected meals: ").append(String.join(", ", meals)).append("\n");
            if (calories != null) sb.append("• Calories per meal: ").append(calories).append("\n");
            if (!allergies.isEmpty()) sb.append("• Allergies: ").append(String.join(", ", allergies)).append("\n");
            double estimate = calculatePriceEstimate(meals, calories, allergies);
            sb.append(String.format("=> Estimated subscription price: ₹%.2f\n\n", estimate));
        } else {
            sb.append("Tip: Provide meal choices (breakfast/lunch/dinner), calories (e.g., 1800), or allergies in your question to get a price estimate.\n\n");
        }

        sb.append(giftCardRulesText()).append("\n");
        sb.append(offerDayText()).append("\n");

        // Helpful quick example for user
        sb.append("Example: \"I want MONTHLY subscription, breakfast + lunch, calories 1800, allergy: peanuts\"\n");
        sb.append("If you want, reply with your meal choices and calories and I will give a price estimate and next steps.\n");

        // Add small note about actual subscription action
        sb.append("\nTo subscribe now, go to the Subscription screen in the app and follow the steps above. ");
        sb.append("When ready, provide planType (DAILY/WEEKLY/MONTHLY/QUARTERLY), mealTimes, caloriesPerMeal and any GiftCard code.\n");

        return sb.toString();
    }

    // Helper: is today 2nd wednesday (if needed)
    public boolean isSecondWednesday(LocalDate date) {
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        while (firstOfMonth.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            firstOfMonth = firstOfMonth.plusDays(1);
        }
        LocalDate secondWednesday = firstOfMonth.plusDays(7);
        return date.equals(secondWednesday);
    }
}

 
