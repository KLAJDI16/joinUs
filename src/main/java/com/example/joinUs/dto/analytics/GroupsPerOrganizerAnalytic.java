package com.example.joinUs.dto.analytics;

import lombok.Data;


@Data
public class GroupsPerOrganizerAnalytic {

    //            "{ $project: { memberId: '$_id', organizerName: 1, groupsOrganized: 1, _id: 0 } }"
    public String userId;
    public String organizerName;
    public Integer groupsOrganized;
}
