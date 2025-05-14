package com.skuniv.fuwarilog.service;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Value;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class GoogleCalendarService {

    @Value("${google.calendar.credentials}")
    private static String credentialPath;

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    // 데이터 저장 경로 설정
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"),
            ".credentials/calendar-java-quickstart");

    // 데이터 저장소 팩토리
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    // JSON 팩토리
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // HTTP 통신을 위한 Transport
    private static HttpTransport HTTP_TRANSPORT;

    // 필요한 Google Calendar API 범위 설정
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

    static {
        try {
            // HTTP 전송을 위한 트러스트된 Transport 생성
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            // 파일 저장소 팩토리 설정
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static Credential authorize() throws IOException, GeneralSecurityException {
        // Load client secrets.
        try (InputStream in = GoogleCalendarService.class.getResourceAsStream(credentialPath)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + credentialPath);
            }

            // GoogleClientSecrets 객체 생성
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // GoogleAuthorizationCodeFlow 객체 생성
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    clientSecrets,
                    SCOPES
            )
                    .setDataStoreFactory(DATA_STORE_FACTORY)
                    .setAccessType("offline")
                    .build();

            // Credential 객체 생성
            Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
            System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

            return credential;
        } catch (IOException e) {
            // JSON 파일을 읽거나 GoogleClientSecrets 로드 중 문제가 발생했을 때 예외 처리
            System.err.println("Error reading client_secret.json: " + e.getMessage());
            throw e;
        }
    }

    /**
     * @implSpec Google Calendar API 사용을 위한 Service 객체 생성
     */
    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * @implSpec Google Calendar에 일정을 추가
     * @param title 일정 제목 & 다이어리 제목 (연동되어 동일)
     * @param description 다이어리 간단 설명
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 마지막 날짜
     */
    public String addEvent(String title, String description, String startDate, String endDate) throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDate(new DateTime(startDate))
                .setTimeZone("Asia/Seoul");
        event.setEnd(start);

        EventDateTime end = new EventDateTime()
                .setDate(new DateTime(endDate))
                .setTimeZone("Asia/Seoul");
        event.setEnd(end);

        Event createdEvent = service.events().insert("primary", event).execute();
        log.info("Event created: {}", createdEvent.getHtmlLink());

        return createdEvent.getId();
    }

    /**
     * @implSpec Google Calendar에 일정을 삭제
     * @param eventId 특정 일정 id
     */
    public void deleteEvent(String eventId) throws Exception {
        Calendar service = getCalendarService();
        service.events().delete("primary", eventId).execute();
    }

    /**
     * @implSpec Google Calendar에 일정을 조회
     */
    public List<Event> listEvents(LocalDate date) throws Exception {
        Calendar service = getCalendarService();

        DateTime startOfDay = new DateTime(date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli());
        DateTime endOfDay = new DateTime(date.plusDays(1).atStartOfDay(ZoneId.of("Asia.Seoul")).toInstant().toEpochMilli());

        Events events = service.events().list("primary")
                .setTimeMin(startOfDay)
                .setTimeMax(endOfDay)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }
}
