package com.tiffino.tiffino.service;

import com.tiffino.tiffino.dto.SubscriptionDto;
import com.tiffino.tiffino.entity.*;
import com.tiffino.tiffino.entity.request.SubscriptionRequest;
import com.tiffino.tiffino.entity.response.GiftCardResponse;
import com.tiffino.tiffino.entity.response.SubscriptionResponse;
import com.tiffino.tiffino.repository.*;
import com.tiffino.tiffino.util.GiftCardUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SubscriptionService {

    private final SubscriptionRepo subscriptionRepository;
    private final GiftCardRepo giftCardRepository;
    private final UserRepo userRepository;

    public SubscriptionService(SubscriptionRepo subscriptionRepository,
                               GiftCardRepo giftCardRepository,
                               UserRepo userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.giftCardRepository = giftCardRepository;
        this.userRepository = userRepository;
    }

    // ✅ Assign subscription to user
 /**   @Transactional
    public SubscriptionResponse assignSubscriptionToUser(Long userId, SubscriptionRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ User not found"));

        List<Subscription> allSubs = subscriptionRepository.findByUser(user);

        // Deactivate expired subscriptions
        allSubs.stream()
                .filter(sub -> sub.isActive() && sub.getEndDate().isBefore(LocalDateTime.now()))
                .forEach(sub -> {
                    sub.setActive(false);
                    subscriptionRepository.save(sub);
                });

        // Refresh list
        allSubs = subscriptionRepository.findByUser(user);

        // Prevent same active plan
        boolean hasActiveSamePlan = allSubs.stream()
                .anyMatch(sub -> sub.isActive()
                        && sub.getEndDate().isAfter(LocalDateTime.now())
                        && sub.getDurationType().equalsIgnoreCase(req.getPlanType()));

        if (hasActiveSamePlan) {
            return new SubscriptionResponse(List.of(), "❌ You already have an active " + req.getPlanType() + " plan.");
        }

        boolean isFirstSubscription = allSubs.isEmpty();

        // Create subscription
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setDurationType(req.getPlanType());
        sub.setCaloriesPerMeal(req.getCaloriesPerMeal());
        sub.setMealTimes(req.getMealTimes() != null ? String.join(",", req.getMealTimes()) : "");
        sub.setAllergies(req.getAllergies() != null ? String.join(",", req.getAllergies()) : "");
        sub.setStartDate(LocalDateTime.now());
        sub.setEndDate(LocalDateTime.now().plusMinutes(2)); // 🔹 Testing expiry
        sub.setActive(true);

        // Calculate price
        double price = 100.0;
        if (req.getMealTimes() != null) {
            for (String meal : req.getMealTimes()) {
                switch (meal.toUpperCase()) {
                    case "BREAKFAST" -> price += 50;
                    case "LUNCH" -> price += 80;
                    case "DINNER" -> price += 70;
                }
            }
        }
        if (req.getCaloriesPerMeal() > 1500) price += 100;
        if (req.getCaloriesPerMeal() > 2000) price += 200;
        if (req.getAllergies() != null && !req.getAllergies().isEmpty()) price += 50;

        sub.setSubscriptionPrice(price);
        sub.setAppliedDiscountPercent(0);
        sub.setFinalPrice(price);
        sub.setSubscriptionDate(LocalDateTime.now());

        subscriptionRepository.save(sub);

        // 🔹 Apply GiftCard or Generate new one
        if (!isFirstSubscription) {
            if (req.getGiftCardCodeInput() != null && !req.getGiftCardCodeInput().isBlank()) {
                GiftCard card = giftCardRepository.findByCodeAndUser(req.getGiftCardCodeInput(), user);

                if (card == null || !card.isActive()) {
                    return new SubscriptionResponse(List.of(), "❌ Invalid or expired GiftCard");
                }

                if (!card.getValidForPlan().equalsIgnoreCase(req.getPlanType())) {
                    return new SubscriptionResponse(List.of(),
                            "❌ GiftCard only valid for " + card.getValidForPlan() + " plan");
                }

                sub.setAppliedDiscountPercent(card.getDiscountPercent());
                sub.setFinalPrice(price - (price * card.getDiscountPercent() / 100.0));

                card.setActive(false);
                giftCardRepository.save(card);
                subscriptionRepository.save(sub);
            } else {
                // Generate new GiftCard after previous expiry
                boolean lastExpired = allSubs.stream()
                        .anyMatch(s -> !s.isActive() && s.getEndDate().isBefore(LocalDateTime.now()));

                if (lastExpired) {
                    giftCardRepository.deleteByUser(user);

                    GiftCard next = new GiftCard();
                    next.setCode(GiftCardUtil.generateGiftCardCode());
                    double discount = GiftCardUtil.generateRandomDiscount();
                    next.setDiscountPercent(discount);
                    next.setValidForPlan(req.getPlanType());
                    next.setDescription(GiftCardUtil.generateDescription(req.getPlanType(), discount));
                    next.setActive(true);
                    next.setGeneratedDate(LocalDate.now());
                    next.setUser(user);

                    giftCardRepository.save(next);
                }
            }
        }

        SubscriptionDto dto = new SubscriptionDto(
                sub.getAppliedDiscountPercent(),
                sub.getSubscriptionPrice(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getDurationType(),
                sub.getCaloriesPerMeal(),
                sub.getId(),
                sub.getFinalPrice()
        );

        return new SubscriptionResponse(List.of(dto), "✅ Subscribed Successfully");
    }

    // ✅ Get all GiftCards for logged-in user
    public List<GiftCardResponse> getAllGiftCardsOfUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Subscription> latestSubscriptionOpt = subscriptionRepository.findTopByUserOrderByEndDateDesc(user);
        if (latestSubscriptionOpt.isEmpty()) {
            return List.of(); // no subscription → no gift card
        }

        Subscription latestSubscription = latestSubscriptionOpt.get();

        // Subscription still active → no giftcard yet
        if (latestSubscription.getEndDate().isAfter(LocalDateTime.now())) {
            return List.of();
        }

        Optional<GiftCard> giftCardOpt = giftCardRepository.findFirstByUserAndActiveTrue(user);
        GiftCard activeGiftCard;
        String planForGiftCard = latestSubscription.getDurationType();

        if (giftCardOpt.isEmpty()) {
            double discount = GiftCardUtil.generateRandomDiscount();

            activeGiftCard = GiftCard.builder()
                    .code(GiftCardUtil.generateGiftCardCode())
                    .discountPercent(discount)
                    .validForPlan(planForGiftCard)
                    .description(GiftCardUtil.generateDescription(planForGiftCard, discount))
                    .active(true)
                    .generatedDate(LocalDate.now())
                    .user(user)
                    .build();

            giftCardRepository.save(activeGiftCard);
        } else {
            activeGiftCard = giftCardOpt.get();

            // Update if plan changed
            if (!activeGiftCard.getValidForPlan().equals(planForGiftCard)) {
                double discount = GiftCardUtil.generateRandomDiscount();
                activeGiftCard.setCode(GiftCardUtil.generateGiftCardCode());
                activeGiftCard.setDiscountPercent(discount);
                activeGiftCard.setValidForPlan(planForGiftCard);
                activeGiftCard.setDescription(GiftCardUtil.generateDescription(planForGiftCard, discount));
                activeGiftCard.setGeneratedDate(LocalDate.now());
                activeGiftCard.setActive(true);

                giftCardRepository.save(activeGiftCard);
            }
        }

        GiftCardResponse response = new GiftCardResponse(
                activeGiftCard.getId(),
                activeGiftCard.getCode(),
                activeGiftCard.getDiscountPercent(),
                activeGiftCard.getValidForPlan(),
                activeGiftCard.getDescription(),
                activeGiftCard.isActive(),
                activeGiftCard.getGeneratedDate()
        );

        return List.of(response);
    }**/
 @Transactional
 public SubscriptionResponse assignSubscriptionToUser(Long userId, SubscriptionRequest req) {

     User user = userRepository.findById(userId)
             .orElseThrow(() -> new RuntimeException("❌ User not found"));

     List<Subscription> allSubs = subscriptionRepository.findByUser(user);

     // Deactivate expired subscriptions
     allSubs.stream()
             .filter(sub -> sub.isActive() && sub.getEndDate().isBefore(LocalDateTime.now()))
             .forEach(sub -> {
                 sub.setActive(false);
                 subscriptionRepository.save(sub);
             });

     // Refresh list
     allSubs = subscriptionRepository.findByUser(user);

     // Prevent same active plan
     boolean hasActiveSamePlan = allSubs.stream()
             .anyMatch(sub -> sub.isActive()
                     && sub.getEndDate().isAfter(LocalDateTime.now())
                     && sub.getDurationType().equalsIgnoreCase(req.getPlanType()));

     if (hasActiveSamePlan) {
         return new SubscriptionResponse(List.of(), "❌ You already have an active " + req.getPlanType() + " plan.");
     }

     boolean isFirstSubscription = allSubs.isEmpty();

     // Create subscription
     Subscription sub = new Subscription();
     sub.setUser(user);
     sub.setDurationType(req.getPlanType());
     sub.setCaloriesPerMeal(req.getCaloriesPerMeal());
     sub.setMealTimes(req.getMealTimes() != null ? String.join(",", req.getMealTimes()) : "");
     sub.setAllergies(req.getAllergies() != null ? String.join(",", req.getAllergies()) : "");
     sub.setStartDate(LocalDateTime.now());
     sub.setEndDate(LocalDateTime.now().plusMinutes(4)); // 🔹 Testing expiry
     sub.setActive(true);

     // Calculate price
     double price = 100.0;
     if (req.getMealTimes() != null) {
         for (String meal : req.getMealTimes()) {
             switch (meal.toUpperCase()) {
                 case "BREAKFAST" -> price += 50;
                 case "LUNCH" -> price += 80;
                 case "DINNER" -> price += 70;
             }
         }
     }
     if (req.getCaloriesPerMeal() > 1500) price += 100;
     if (req.getCaloriesPerMeal() > 2000) price += 200;
     if (req.getAllergies() != null && !req.getAllergies().isEmpty()) price += 50;

     sub.setSubscriptionPrice(price);
     sub.setAppliedDiscountPercent(0);
     sub.setFinalPrice(price);
     sub.setSubscriptionDate(LocalDateTime.now());

     subscriptionRepository.save(sub);

     // 🔹 Apply GiftCard or Generate new one (skip for first subscription)
     if (!isFirstSubscription) {
         if (req.getGiftCardCodeInput() != null && !req.getGiftCardCodeInput().isBlank()) {
             GiftCard card = giftCardRepository.findByCodeAndUser(req.getGiftCardCodeInput(), user);

             if (card == null || !card.isActive()) {
                 return new SubscriptionResponse(List.of(), " Invalid or expired GiftCard");
             }

             if (!card.getValidForPlan().equalsIgnoreCase(req.getPlanType())) {
                 return new SubscriptionResponse(List.of(),
                         " GiftCard only valid for " + card.getValidForPlan() + " plan");
             }

             sub.setAppliedDiscountPercent(card.getDiscountPercent());
             sub.setFinalPrice(price - (price * card.getDiscountPercent() / 100.0));

             card.setActive(false);
             giftCardRepository.save(card);
             subscriptionRepository.save(sub);
         } else {
             // Generate new GiftCard after previous expiry (not first subscription)
             boolean lastExpired = allSubs.stream()
                     .anyMatch(s -> !s.isActive() && s.getEndDate().isBefore(LocalDateTime.now()));

             if (lastExpired) {
                 giftCardRepository.deleteByUser(user);

                 GiftCard next = new GiftCard();
                 next.setCode(GiftCardUtil.generateGiftCardCode());
                 double discount = GiftCardUtil.generateRandomDiscount();
                 next.setDiscountPercent(discount);
                 next.setValidForPlan(req.getPlanType());
                 next.setDescription(GiftCardUtil.generateDescription(req.getPlanType(), discount));
                 next.setActive(true);
                 next.setGeneratedDate(LocalDate.now());
                 next.setUser(user);

                 giftCardRepository.save(next);
             }
         }
     }

     SubscriptionDto dto = new SubscriptionDto(
             sub.getAppliedDiscountPercent(),
             sub.getSubscriptionPrice(),
             sub.getStartDate(),
             sub.getEndDate(),
             sub.getDurationType(),
             sub.getCaloriesPerMeal(),
             sub.getId(),
             sub.getFinalPrice()
     );

     return new SubscriptionResponse(List.of(dto), "✅ Subscribed Successfully");
 }
    public List<GiftCardResponse> getAllGiftCardsOfUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> allSubs = subscriptionRepository.findByUser(user);

        // If first subscription only, return empty list → no gift card yet
        if (allSubs.size() <= 1) {
            return List.of();
        }

        // Get latest subscription
        Optional<Subscription> latestSubscriptionOpt = subscriptionRepository.findTopByUserOrderByEndDateDesc(user);
        if (latestSubscriptionOpt.isEmpty()) {
            return List.of();
        }

        Subscription latestSubscription = latestSubscriptionOpt.get();

        // If subscription still active → no gift card yet
        if (latestSubscription.getEndDate().isAfter(LocalDateTime.now())) {
            return List.of();
        }

        Optional<GiftCard> giftCardOpt = giftCardRepository.findFirstByUserAndActiveTrue(user);
        GiftCard activeGiftCard;
        String planForGiftCard = latestSubscription.getDurationType();

        if (giftCardOpt.isEmpty()) {
            double discount = GiftCardUtil.generateRandomDiscount();

            activeGiftCard = GiftCard.builder()
                    .code(GiftCardUtil.generateGiftCardCode())
                    .discountPercent(discount)
                    .validForPlan(planForGiftCard)
                    .description(GiftCardUtil.generateDescription(planForGiftCard, discount))
                    .active(true)
                    .generatedDate(LocalDate.now())
                    .user(user)
                    .build();

            giftCardRepository.save(activeGiftCard);
        } else {
            activeGiftCard = giftCardOpt.get();

            // Update if plan changed
            if (!activeGiftCard.getValidForPlan().equals(planForGiftCard)) {
                double discount = GiftCardUtil.generateRandomDiscount();
                activeGiftCard.setCode(GiftCardUtil.generateGiftCardCode());
                activeGiftCard.setDiscountPercent(discount);
                activeGiftCard.setValidForPlan(planForGiftCard);
                activeGiftCard.setDescription(GiftCardUtil.generateDescription(planForGiftCard, discount));
                activeGiftCard.setGeneratedDate(LocalDate.now());
                activeGiftCard.setActive(true);

                giftCardRepository.save(activeGiftCard);
            }
        }

        GiftCardResponse response = new GiftCardResponse(
                activeGiftCard.getId(),
                activeGiftCard.getCode(),
                activeGiftCard.getDiscountPercent(),
                activeGiftCard.getValidForPlan(),
                activeGiftCard.getDescription(),
                activeGiftCard.isActive(),
                activeGiftCard.getGeneratedDate()
        );

        return List.of(response);
    }

    public boolean hasActiveSubscription(User user) {
        List<Subscription> subs = subscriptionRepository.findByUser(user);
        return subs.stream().anyMatch(s -> s.isActive() && s.getEndDate().isAfter(LocalDateTime.now()));
    }

}
