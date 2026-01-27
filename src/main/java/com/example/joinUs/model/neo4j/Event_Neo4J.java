package com.example.joinUs.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;
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

    @Property(name = "description")
    private String description;

    @Property(name = "event_time")
    private ZonedDateTime eventTime;

    @Property(name = "event_url")
    private String eventUrl;

//    private static final List<String> eventProperties=List.of("event_id", "event_name",
//    "event_time", "description", "event_url", "fee");

//    // Fee fields
//    private String fee_description;
//    private String fee_accepts;
//
//    @Property(name = "fee_isRequired")
//    private String fee_isRequired;
//
//    @Property(name = "fee_amount")
//    private String fee_amount;
//
//    // Venue/City fields
//    @Property(name = "venue_city_city")
//    private String venue_city;
//
//    @Property(name = "venue_city_country")
//    private String venue_country;
//
//    @Property(name = "venue_city_state")
//    private String venue_state;
//
//    @Property(name = "venue_city_zip")
//    private String venue_zip;
//
//    @Property(name = "venue_city_localized_country_name")
//    private String venue_localized_country_name;
//
//    @Property(name = "venue_city_latitude")
//    private String venue_latitude;
//
//    @Property(name = "venue_city_longitude")
//    private String venue_longitude;
//
//    @Property(name = "venue_city_city_id")
//    private String venue_city_id;
//
//    @Property(name = "venue_city_distance")
//    private String venue_distance;
//
//    public EventNeo4jDTO toDTO() {
//        return EventNeo4jDTO.builder()
//                .eventId(this.eventId)
//                .eventName(this.eventName)
//                .description(this.description)
//                .eventTime(this.eventTime)
//                .eventUrl(this.eventUrl)
//                .feeDescription(this.fee_description)
//                .feeAccepts(this.fee_accepts)
//                .feeIsRequired(this.fee_isRequired)
//                .feeAmount(this.fee_amount)
//                .venueCity(this.venue_city)
//                .venueCountry(this.venue_country)
//                .venueState(this.venue_state)
//                .venueZip(this.venue_zip)
//                .venueLocalizedCountryName(this.venue_localized_country_name)
//                .venueLatitude(this.venue_latitude)
//                .venueLongitude(this.venue_longitude)
//                .venueCityId(this.venue_city_id)
//                .venueDistance(this.venue_distance)
//                .build();
//    }



//SEVERE: Servlet.service() for servlet [dispatcherServlet] in context with path [] threw
// exception [Request processing failed: org.springframework.data.mapping.MappingException:
// Error mapping Record<{event_Neo4J: {fee_description: "per person",
// fee_accepts: "others", __elementId__: "4:0a049a51-e7a2-40ae-80b6-02a7a305e077:6",
// venue_city_state: "CA",
// description: "Solve the murder case while eating a 3 course meal at 3 different
// http://www.meetup.com/SanFranciscoHostel/events/222306617/</a> EVENT INFO:<a href=http://www.sfhostelparty.com/>www.sfhostelparty.com We eat a different course at each place - we stay at each restaurant for about 40 minutes before heading off to the next one. RSVP is *required* for planning purposes. You *must* meet us at the first place between 7:30PM and 7:45PM if you are planning to attend - please do not try and join us mid-way into the tour.",
// venue_city_city: "San Francisco", venue_city_country: "us",
// venue_city_longitude: "-122.41999800", venue_city_zip: "94101",
// venue_city_distance: "31.839",
// event_url: "https://www.meetup.com/SanFranciscoStartupFun/events/153868222/",
// __nodeLabels__: ["Event"], fee_isRequired: "false",
// fee_amount: "0.0", event_id: "snxrwlywdbhb",
// venue_city_city_id: "94101",
// event_name: "Murder Mystery Dinner Crawl. Dine at 3 Restaurants & solve the murder",
// venue_city_localized_country_name: "USA", event_time: "Fri Oct 30 02:30:00 CET 2026",
// venue_city_latitude: "37.77999900"}}>] with root cause

}

