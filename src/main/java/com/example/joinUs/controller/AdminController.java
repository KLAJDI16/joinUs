package com.example.joinUs.controller;


import org.bson.json.JsonObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("")
    public JsonObject getMetrics() {
        JsonObject jsonObject = new JsonObject("{\"result\":\"Successfully hit GET /admin \"}");
        return jsonObject;
    }
}
