package com.skuniv.fuwarilog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Chalendar(Trip) API", description = "여행일정 관련(캘린더) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trips")
public class TripController {
}
