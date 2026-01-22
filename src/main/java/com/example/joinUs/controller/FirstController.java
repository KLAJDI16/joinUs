package com.example.joinUs.controller;

import com.example.joinUs.dto.LoginForm;
import com.example.joinUs.dto.UserDTO;
import com.example.joinUs.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Security;
import java.util.Collection;
import java.util.List;

@RestController
public class FirstController {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UserService userService;

    @Autowired
     AuthenticationManager authManager;
//    private final UserRepository userRepo;
//    private final PasswordEncoder encoder;

//    @ApiIgnore
    @RequestMapping("/")
    public void redirect(HttpServletResponse response){

        try {
//            response.sendRedirect("/login");
            response.sendRedirect("/swagger-ui/index.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginForm req,HttpServletRequest request) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username, req.password
                )
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

//        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",context);
        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out");
    }

//    @PostMapping("/register")
//    public ResponseEntity<UserDTO> register(HttpServletRequest request,@RequestBody UserDTO userDTO) {
//
//        userService.createUser(userDTO);
//
//        Authentication auth = authManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        userDTO.getMember_name(),
//                )
//        );
//
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        context.setAuthentication(auth);
//        SecurityContextHolder.setContext(context);
//
//        return ResponseEntity.ok("Logged out");
//    }

}
