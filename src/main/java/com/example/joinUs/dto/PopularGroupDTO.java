package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularGroupDTO {
    private String groupId;
    private String groupName;
    private long memberCount;
}
