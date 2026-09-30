package com.tiffino.tiffino.entity.response;

import com.tiffino.tiffino.dto.SubscriptionDto;
import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponse {
    private List<SubscriptionDto> subscription;
    private String message;
}
