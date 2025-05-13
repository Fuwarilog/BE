package com.skuniv.fuwarilog.service;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleChalendarService {
    // app 이름
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

    // 데이터 저장 경로
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credencials/calendar-java-quickstart");

    // 데이터 저장소 팩토리
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    // Json 팩토리
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // HTTP 통신 Transport
    private static HttpTransport HTTP_TRANSPORT;

    // 필요한 Google calendar API 범위 설정
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

    // credential 경로
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static Credential getCredentials() throws IOException, GeneralSecurityException {
        // Load client secrets.
        try (InputStream in = GoogleChalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
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
            System.out.println("Error reading credentials file." + e.getMessage());
            throw e;
        }
    }

    // Google Calendar API 사용을 위한 Service 객체 생성
    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
