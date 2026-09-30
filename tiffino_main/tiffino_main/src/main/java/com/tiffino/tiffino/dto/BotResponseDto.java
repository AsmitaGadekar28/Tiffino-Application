package com.tiffino.tiffino.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotResponseDto {
    private String botMessage;
    private List<String> options;
}
