package com.example.joinUs.model.mongodb;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fee {

private String accepts;
private long amount;
private String description;
private boolean isRequired;

    //        "fee": {
//        "accepts": "others",
//                "amount": 0,
//                "description": "per person",
//                "isRequired": false
//    },
}
