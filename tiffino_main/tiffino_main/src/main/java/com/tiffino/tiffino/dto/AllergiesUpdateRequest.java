package com.tiffino.tiffino.dto;

import lombok.Data;
import java.util.List;

@Data
public class AllergiesUpdateRequest {
    private List<String> add;
    private List<String> remove;
}
