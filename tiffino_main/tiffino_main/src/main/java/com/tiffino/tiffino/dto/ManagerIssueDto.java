package com.tiffino.tiffino.dto;

import lombok.Data;

@Data
public class ManagerIssueDto {

    private String userName;
    private String phoneNumber;
    private String address;
    private Long orderId;
    private String issueType;
    private String photoUrl;
}
