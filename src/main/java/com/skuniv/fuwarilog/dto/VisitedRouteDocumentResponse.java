package com.skuniv.fuwarilog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class VisitedRouteDocumentResponse {
    public record RouteDTO (String distanceText, String durationText, String lat, String lon) {};

}
