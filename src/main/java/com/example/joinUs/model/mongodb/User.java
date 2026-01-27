package com.example.joinUs.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "members")
public class User implements UserDetails {

    @Id
    @Field("_id")
    private String id;

    @Field("member_id")
    private String memberId;

    @Field("member_name")
    private String memberName;

    @Field("city")
    private City city;

    @Field("member_status")
    private String memberStatus;

    @Field("bio")
    private String bio;


    @Field("topics")
    private List<Topic> topics;

    @Field("event_count")
    private Integer eventCount;

    @Field("group_count")
    private Integer groupCount;

    @Field("upcoming_events")
    private List<Event> upcomingEvents;

    @Field("password")
    private String password;


    @Field("isAdmin")
    private Boolean isAdmin;

//    public List<String> getRoles(){
//        List<String> roles = new ArrayList<>();
//        if (this.isAdmin) roles.add("ADMIN");
//        for (String str:getCreated_groups()){
//            roles.add("ORGANIZE_"+str);
//        }
//        return roles;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<String> list = new ArrayList<>();

        if (isAdmin) list.add("ROLE_ADMIN"); // TODO also include groups?

        return list.stream().map(e -> new SimpleGrantedAuthority(e)).toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.memberName;
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

}
