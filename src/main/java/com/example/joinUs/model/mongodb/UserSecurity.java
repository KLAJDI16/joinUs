package com.example.joinUs.model.mongodb;

import com.example.joinUs.service.UserService;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSecurity implements UserDetails {
    User user;
    List<String> roles;

    public UserSecurity(User user,List<String> roles ){
        this.user=user;
        this.roles=roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
     Collection<String> list = new ArrayList<>();

     if (user.getIsAdmin()) roles.add("ROLE_ADMIN");

     for (String str:this.roles){
         list.add(str);
     }
        return list.stream().map(e -> new SimpleGrantedAuthority(e)).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getMember_name();
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
