package com.tiffino.tiffino.entity.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String userName;
    private String address;
    private String phoneNo;
    private String mealPreference;
    private String dietaryNeeds;

}
