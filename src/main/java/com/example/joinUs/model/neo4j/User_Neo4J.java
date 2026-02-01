package com.example.joinUs.model.neo4j;


import com.example.joinUs.dto.CityDTO;
import com.example.joinUs.dto.UserNeo4jDTO;
import com.example.joinUs.model.mongodb.Topic;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Node("Member")
public class User_Neo4J {

    @Id
    @Property(name = "member_id")
    private String memberId;

    @Property(name = "member_name")
    private String memberName;

    @Property(name = "city")
    private String cityName;

 //    private static final List<String> memberProperties=List.of("member_id", "city", "member_name");
}

