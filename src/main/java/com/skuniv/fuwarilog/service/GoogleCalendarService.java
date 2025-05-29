package com.skuniv.fuwarilog.service;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.gson.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.JsonObject;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final UserRepository userRepository;
    private static final String CALENDAR_API_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events";

    /**
     * @implSpec Google Calendar에 일정을 추가
     * @param title 일정 제목 & 다이어리 제목 (연동되어 동일)
     * @param description 다이어리 간단 설명
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 마지막 날짜
     */
    public String addEvent(String userEmail, String title, String description, String startDate, String endDate) throws IOException {
        String googleAccessToken = getGoogleAccessToken(userEmail);

        Map<String, Object> event = new HashMap<>();
        event.put("summary", title);
        event.put("description", description);

        Map<String, String> start = new HashMap<>();
        start.put("date", startDate); // "yyyy-MM-dd"
        start.put("timeZone", "Asia/Seoul");

        Map<String, String> end = new HashMap<>();
        end.put("date", endDate); // "yyyy-MM-dd"
        end.put("timeZone", "Asia/Seoul");

        event.put("start", start);
        event.put("end", end);

        HttpRequestFactory requestFactory = new NetHttpTransport()
                .createRequestFactory(request -> {
                    request.getHeaders().setAuthorization("Bearer " + googleAccessToken);
                    request.getHeaders().setAccept("application/json");
                    request.getHeaders().setContentType("application/json");
                });

        HttpContent content = new JsonHttpContent(new JacksonFactory(), event);

        HttpRequest request = requestFactory.buildPostRequest(
                new GenericUrl(CALENDAR_API_URL),
                content
        );

        log.info("Request: " + request.getUrl());

        HttpResponse response = request.execute();
        JsonObject json =  new JsonParser().parse(new InputStreamReader(response.getContent())).getAsJsonObject();
        String value = json.get("id").getAsString();
        log.info("Add event response: {}", value);
        return value;
    }

    /**
     * @implSpec Google Calendar에 일정을 삭제
     * @param eventId 특정 일정 id
     */
    public void deleteEvent(String userEmail, String eventId) throws IOException {
        String googleAccessToken = getGoogleAccessToken(userEmail);

        HttpRequestFactory requestFactory = new NetHttpTransport()
                .createRequestFactory(request -> {
                    request.getHeaders().setAuthorization("Bearer " + googleAccessToken);
                });

        String url = CALENDAR_API_URL + "/" + eventId;

        HttpRequest request = requestFactory.buildDeleteRequest(new GenericUrl(url));
        request.execute();

        log.info("Deleted event: {}", eventId);
    }

    /**
     * @implSpec Google Calendar에 일정을 조회
     */
    public String listEvents(String userEmail, LocalDate date) throws IOException {
        String googleAccessToken = getGoogleAccessToken(userEmail);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toString();
        String end = date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toString();

        String url = UriComponentsBuilder.fromHttpUrl(CALENDAR_API_URL)
                .queryParam("timeMin", start)
                .queryParam("timeMax", end)
                .queryParam("singleEvents", "true")
                .queryParam("orderBy", "startTime")
                .build()
                .toUriString();

        HttpRequestFactory requestFactory = new NetHttpTransport()
                .createRequestFactory(request -> {
                    request.getHeaders().setAuthorization("Bearer " + googleAccessToken);
                });

        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        HttpResponse response = request.execute();
        String responseString = response.parseAsString();

        log.info("Events fetched: {}", responseString);
        return responseString;
    }

    /**
     * DB에서 Google accessToken 조회 유틸
     */
    private String getGoogleAccessToken(String userEmail) {
        String accessToken =  userRepository.findByEmail(userEmail)
                .map(User::getGoogleAccessToken)
                .orElseThrow(() -> new RuntimeException("Google access token not found for user: " + userEmail));

        return accessToken;
    }
}
