package com.example.joinUs.model.mongodb;

import com.example.joinUs.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection = "members")
public class User implements UserDetails {

//    @Id

    @Field("member_id")
    private String member_id;


    @Field("member_name")
    private String member_name;

    @Field("city")
    private City city;

    @Field("member_status")
    private String member_status;

    @Field("bio")
    private String bio;

    @Field("topics")
    private List<Topic> topics;

    @Field("event_count")
    private Double event_count;

    @Field("group_count")
    private Double group_count;

    @Field("upcoming_events")
    private List<Event> upcoming_events;

    @Field("password")
    private String password;

    @Field("groups_organizer")
    private List<String> created_groups;

    @Field("isAdmin")
//    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean isAdmin;



public List<String> getRoles(){
    List<String> roles = new ArrayList<>();
    if (this.isAdmin) roles.add("ADMIN");
    for (String str:getCreated_groups()){
        roles.add("ORGANIZE_"+str);
    }
    return roles;
}


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//
//        if (isAdmin) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        }
//
//        for (String groupId : created_groups) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_ORGANIZE_" + groupId));
//        }
//
//        return authorities;
//    }
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<String> list = new ArrayList<>();

    if (getIsAdmin()) list.add("ROLE_ADMIN");

    return list.stream().map(e -> new SimpleGrantedAuthority(e)).toList();
}

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return getMember_name();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();

        dto.setMember_id(this.member_id);
        dto.setMember_name(this.member_name);

        if (this.city != null) {
            dto.setCity(this.city.toDTO());
        }

        dto.setMember_status(this.member_status);
        dto.setBio(this.bio);

        dto.setTopics(this.topics);

        dto.setEvent_count(this.event_count);
        dto.setGroup_count(this.group_count);

        if (this.upcoming_events != null) {
            dto.setUpcoming_events(
                    this.upcoming_events.stream()
                            .map(Event::toDTO)
                            .toList()
            );
        }

        return dto;
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) return null;

        User user = new User();

        user.setMember_id(dto.getMember_id());
        user.setMember_name(dto.getMember_name());

        if (dto.getCity() != null) {
            user.setCity(City.fromDTO(dto.getCity()));
        }

        user.setMember_status(dto.getMember_status());
        user.setBio(dto.getBio());

        user.setTopics(dto.getTopics());

        user.setEvent_count(dto.getEvent_count());
        user.setGroup_count(dto.getGroup_count());

        if (dto.getUpcoming_events() != null) {
            user.setUpcoming_events(
                    dto.getUpcoming_events().stream()
                            .map(Event::fromDTO)
                            .toList()
            );
        }

        return user;
    }

}
