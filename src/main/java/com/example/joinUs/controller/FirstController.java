package com.example.joinUs.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("")
public class FirstController {


//    @ApiIgnore
    @RequestMapping("/")
    public void redirect(HttpServletResponse response){

        try {
            response.sendRedirect("/swagger-ui/index.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
