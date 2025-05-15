package com.skuniv.fuwarilog.dto;

import java.util.List;

public class LocationResponse {
    public record CurrentLocationDTO (String placeName, double latitude, double longitude) {};
    public record PlaceDTO (String name, double latitude, double longitude) {};
    public record RouteDTO (String duration,  String lat, List<String> lon) {};

}
