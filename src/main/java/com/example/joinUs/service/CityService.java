package com.example.joinUs.service;

import com.example.joinUs.dto.CityDTO;
import com.example.joinUs.dto.EventDTO;
import com.example.joinUs.model.mongodb.Event;
import com.example.joinUs.repository.CityRepository;
import com.example.joinUs.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(e -> e.toDTO())
                .toList();
    }
}

