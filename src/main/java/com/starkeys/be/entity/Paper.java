package com.starkeys.be.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "paper")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paper {

    @Id
    private String id;

    @Field("keywords")
    private List<String> keywords;

    @Field("link")
    private String link;

    @Field("pmc_id")
    private String pmcId;

    @Field("title")
    private String title;
}
