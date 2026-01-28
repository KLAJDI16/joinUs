package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCommunityDTO {

    private String group1Id;
    private String group1Name;

    private String group2Id;
    private String group2Name;

    private long sharedMembers;
}
