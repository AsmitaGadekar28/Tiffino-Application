package com.tiffino.tiffino.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueReportDto {
    private Long id;
    private String userEmail;
    private String issueType;
    private String extraChoice;
    private String message;
    private String photoUrl;
    private String status;
    private boolean managerNotified;
    private String createdAt;
}
