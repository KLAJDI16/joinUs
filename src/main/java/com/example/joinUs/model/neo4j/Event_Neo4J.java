package com.example.joinUs.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Node("Event")
public class Event_Neo4J {

    @Id
    @Property(name = "event_id")
    private String eventId;
    @Property(name = "event_name")
    private String eventName;

    @Property(name = "group_name")
    private String groupName;

    @Property(name = "group_id")
    private String groupId;

    @Property(name = "description")
    private String description;

    @Property(name = "event_time")
    private OffsetDateTime eventTime;

    @Property(name = "event_url")
    private String eventUrl;

    //    private static final List<String> eventProperties=List.of("event_id", "event_name",
    //    "event_time", "description",
    //    "fee_amount","venue_city","group_name","group_id","venue_address_1");

    @Property(name = "fee_amount")
    private String feeAmount;

//     Venue/City fields
    @Property(name = "venue_city")
    private String cityName;

    @Property(name = "venue_address_1")
    private String venueAddress1;


}

