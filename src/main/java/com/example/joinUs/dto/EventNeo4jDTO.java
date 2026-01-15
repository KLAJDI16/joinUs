package com.example.joinUs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNeo4jDTO {

    private String eventId;
    private String eventName;
    private String description;
    private String eventTime;
    private String eventUrl;

    private String feeDescription;
    private String feeAccepts;
    private String feeIsRequired;
    private String feeAmount;

    private String venueCity;
    private String venueCountry;
    private String venueState;
    private String venueZip;
    private String venueLocalizedCountryName;
    private String venueLatitude;
    private String venueLongitude;
    private String venueCityId;
    private String venueDistance;
}

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
