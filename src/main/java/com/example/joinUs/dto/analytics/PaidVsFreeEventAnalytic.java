package com.example.joinUs.dto.analytics;

import lombok.Data;

@Data
public class PaidVsFreeEventAnalytic {

 public   Integer eventsCount;
    public    Integer totalAttendance;

    public    Integer avgAttendance;

    public   String type;
}
