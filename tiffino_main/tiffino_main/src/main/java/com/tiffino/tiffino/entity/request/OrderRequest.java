package com.tiffino.tiffino.entity.request;

import com.tiffino.tiffino.entity.CloudKitchen;
import lombok.Data;

@Data
public class OrderRequest {
    private String address;
    private String city;
    private String state;
    private String pinCode;

}
