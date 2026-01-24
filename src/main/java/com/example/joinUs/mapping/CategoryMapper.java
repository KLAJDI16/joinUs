package com.example.joinUs.mapping;

import com.example.joinUs.dto.CategoryDTO;
import com.example.joinUs.model.mongodb.Category;
import org.mapstruct.Mapper;


@Mapper(config = CentralMappingConfig.class)
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO dto);

}
