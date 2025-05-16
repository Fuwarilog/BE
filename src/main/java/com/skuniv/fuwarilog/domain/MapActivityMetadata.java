package com.skuniv.fuwarilog.domain;

import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Document(collection = "map_activity_metadata")
public class MapActivityMetadata {
    private List<Coordinate> visitedRouts;
    private List<Tag> tags;
    private List<String> bookmarks;
}