package com.example.joinUs.model.neo4j;

import com.example.joinUs.dto.GroupNeo4jDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Node("Group")
public class Group_Neo4J {
    @Id
    @Property("group_id")
    private String groupId;
    @Property("group_name")
    private String groupName;
    @Property("description")
    private String description;
    @Property("city")
    private String cityName;
    @Property("category_name")
    private String categoryName;

    //    private static final List<String> groupProperties=List.of("group_id", "group_name",
//    "city", "description","category_name");

}

