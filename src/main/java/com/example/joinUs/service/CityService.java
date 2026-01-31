package com.example.joinUs.service;

import com.example.joinUs.Utils;
import com.example.joinUs.dto.CityDTO;
import com.example.joinUs.mapping.CityMapper;
import com.example.joinUs.model.mongodb.City;
import com.example.joinUs.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void parseCityDTO(CityDTO cityDTO){
        if (!Utils.isNullOrEmpty(cityDTO)) {
            if (!Utils.isNullOrEmpty(cityDTO.getId())) {
                String cityId = cityDTO.getId();
                City city = cityRepository.findByCityId(cityId);
                if (city != null) cityDTO = cityMapper.toDTO(city);
            } else if (!Utils.isNullOrEmpty(cityDTO.getName())) {
                String cityName = cityDTO.getName();
                City city = cityRepository.findByName(cityName);
                if (city != null) cityDTO = cityMapper.toDTO(city);
            }
        }
    }

}

