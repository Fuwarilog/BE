package com.skuniv.fuwarilog.dto.VisitedRouteDocument;

public class VisitedRouteDocumentResponse {
    public record RouteDTO (String distanceText, String durationText, String lat, String lon) {};

}
