package com.example.joinUs.model.mongodb;


import com.example.joinUs.dto.EventDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
//import org.springframework.data.mongodb.core;


import java.util.Date;
import java.util.List;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "events")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

    @Id
    @Field("_id")
    private ObjectId id;

    @Field("event_id")
    private String event_id;

    @Field("description")
    private String description;

    @Field("event_url")
    private String event_url;

    @Field("event_name")
    private String event_name;

    @Field("event_status")
    private String event_status;

    @Field("created")
    private Date created;

    @Field("event_time")
    private Date event_time;

    @Field("updated")
    private Date updated;

    @Field("duration")
    private Long duration;

    @Field("utc_offset")
    private Long utc_offset;

    @Field("categories")
    private List<Category> categories;

    @Field("member_count")
    private Double member_count;

//     @Field("venue")
//     private Venue venue;

    @Field("creator_group")
    private Group creator_group;

//        public EventDTO toDTO() {
//            EventDTO dto = new EventDTO();
//
//            dto.setEventId(this.event_id);
//            dto.setEventName(this.event_name);
//            dto.setEventUrl(this.event_url);
//            dto.setDescription(this.description);
//            dto.setEventStatus(this.event_status);
//
//            dto.setCreated(this.created);
//            dto.setEventTime(this.event_time);
//            dto.setUpdated(this.updated);
//
//            dto.setDuration(this.duration);
//            dto.setUtc_offset(this.utc_offset);
//
//            dto.setMember_count(this.member_count);
//
//            dto.setCategories(this.categories);
//
//            if (this.creator_group!=null) {
//                dto.setCreator_group(this.creator_group.toDTO());
//            }
////            dto.setVenue(this.venue);
//            return dto;
//        }


//    public static Event fromDTO(EventDTO dto) {
//        if (dto == null) return null;
//
//        Event event = new Event();
//
//        event.setEvent_id(dto.getEvent_id());
//        event.setEvent_name(dto.getEvent_name());
//        event.setEvent_url(dto.getEvent_url());
//        event.setDescription(dto.getDescription());
//        event.setEvent_status(dto.getEvent_status());
//
//        event.setCreated(dto.getCreated());
//        event.setEvent_time(dto.getEvent_time());
//        event.setUpdated(dto.getUpdated());
//
//        event.setDuration(dto.getDuration());
//        event.setUtc_offset(dto.getUtc_offset());
//
//        event.setMember_count(dto.getMember_count());
//        event.setCategories(dto.getCategories());
//
//        if (dto.getCreator_group() != null) {
//            event.setCreator_group(Group.fromDTO(dto.getCreator_group()));
//        }
//
//        // If you have venue mapping in the future:
//        // event.setVenue(Venue.fromDTO(dto.getVenue()));
//
//        return event;
//    }


}

