package com.skuniv.fuwarilog.dto;

public class LocationResponse {
    public record CurrentLocationDTO (String placeName, double latitude, double longitude) {};
    public record PlaceDTO (String name, double latitude, double longitude) {};
}
