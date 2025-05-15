package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.DiaryList;
import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.TripResponse;
import com.skuniv.fuwarilog.repository.DiaryListRepository;
import com.skuniv.fuwarilog.repository.DiaryRepository;
import com.skuniv.fuwarilog.repository.TripRepository;
import com.skuniv.fuwarilog.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TripService {

    private final GoogleCalendarService googleCalendarService;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryListRepository diaryListRepository;

    /**
     * @implSpec 구글켈린더에 맞는 서버 Trip 데이터 생성
     * @param title 일정 제목 & 다이어리 제목 (연동되어 동일)
     * @param description 다이어리 간단 설명
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 마지막 날짜
     * @param country 여행지 입력
     * @return id 여행일정 Trip 아이디
    * */
    public TripResponse.TripInfoDTO createEvent(String userEmail, String title, String description, String startDate, String endDate, String country) throws Exception {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        String eventId = googleCalendarService.addEvent(userEmail, title, description, startDate, endDate);

        log.info("eventId: {}", eventId);
        Trip newTrip = Trip.builder()
                .user(user)
                .title(title)
                .description(description)
                .country(country)
                .googleEventId(eventId)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .build();
        tripRepository.save(newTrip);

        // ** 여행 일정 생성 시 자동으로 다이어리 생성되게끔 만들어야함
        Diary newDiary = Diary.builder()
                .trip(newTrip)
                .title(title)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .build();
        diaryRepository.save(newDiary);

        // 여행일정 마다 다이어리 생성
        for (LocalDate d=LocalDate.parse(startDate); d.compareTo(LocalDate.parse(endDate)) <= 0; d = d.plusDays(1)) {
            DiaryList newDiaries = DiaryList.builder()
                    .diary(newDiary)
                    .date(d)
                    .build();
            diaryListRepository.save(newDiaries);
        }

        return TripResponse.TripInfoDTO.from(newTrip);
    }

    /**
     * @implSpec 구글 캘린더 일정 삭제 시 서버 Trip 데이터도 같이 삭제됨
     * @param id 일정 아이디
     * */
    public void deleteEvent(String userEmail, Long id) throws Exception{
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND));

        googleCalendarService.deleteEvent(userEmail, trip.getGoogleEventId());
        tripRepository.delete(trip);
    }

    /**
     * @implSpec 특정 날짜의 서버 Trip 데이터 조회
     * @param date String 형식으로 날짜 작성 ex) yyyy-MM-dd
     * @return List<Diary> 날짜에 대한 다이어리 모두 반환
     * */
    public Optional<Trip> getEvents(Long userId, Long tripId, String date) {
        // 1. 여행 리스트 객체 생성
        Optional<Trip> tripList;

        // 2. 사용자 존재 확인
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 3. tripId & date값이 있느냐 없느냐에 따라 달라짐
        if (tripId != null && date != null) {
            tripList = tripRepository.findAllByUser(user);
        } else if (tripId == null && date != null) {
            tripList = tripRepository.findAllByStartDate(LocalDate.parse(date));
        } else if (tripId != null && date == null) {
            tripList = tripRepository.findById(tripId);
        } else {
            tripList = tripRepository.findByIdAndStartDate(tripId, LocalDate.parse(date));
        }

        return tripList;
    }

    public List<Diary> getDiariesByTrip(Long userId, Long tripId) {
        // 1. 사용자 존재 확인
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 2. 여행일정 존재 확인
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND));

        return diaryRepository.findAllByTripId(tripId);
    }
}
