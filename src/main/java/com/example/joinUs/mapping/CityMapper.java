package com.example.joinUs.mapping;

import com.example.joinUs.dto.CityDTO;
import com.example.joinUs.model.mongodb.City;
import org.mapstruct.Mapper;


@Mapper(config = CentralMappingConfig.class)
public interface CityMapper {

    CityDTO toDTO(City city);

    City toEntity(CityDTO dto);

}
