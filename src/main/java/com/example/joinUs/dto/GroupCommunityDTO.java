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
        private GroupDTO group1;
       private GroupDTO group2;
        private int sharedMembers;
    }

