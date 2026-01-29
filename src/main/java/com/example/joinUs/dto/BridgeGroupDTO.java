package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BridgeGroupDTO {
    private String groupId;
    private String groupName;
    private long connectedGroups; // how many other groups are connected through shared members
}
