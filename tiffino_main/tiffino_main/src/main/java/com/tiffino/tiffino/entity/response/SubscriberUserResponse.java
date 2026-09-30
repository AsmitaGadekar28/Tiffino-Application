package com.tiffino.tiffino.entity.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SubscriberUserResponse {
    private String username;
    private Double finalPrice;
    private String durationType; // plan type

    public SubscriberUserResponse(String username, Double finalPrice, String durationType) {
        this.username = username;
        this.finalPrice = finalPrice;
        this.durationType = durationType;
    }

}
