package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
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

    /**
     * @implSpec 구글켈린더에 맞는 서버 Trip 데이터 생성
     * @param title 일정 제목 & 다이어리 제목 (연동되어 동일)
     * @param description 다이어리 간단 설명
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 마지막 날짜
     * @param country 여행지 입력
     * @return id 여행일정 Trip 아이디
    * */
    public Long createEvent(String title, String description, String startDate, String endDate, String country) throws Exception {
        String eventId = googleCalendarService.addEvent(title, description, startDate, endDate);

        log.info("eventId: {}", eventId);
        Trip trip = Trip.builder().build();
        trip.setTitle(title);
        trip.setCountry(country);
        trip.setDescription(description);
        trip.setGoogleEventId(eventId);
        trip.setStartDate(LocalDate.parse(startDate));
        trip.setEndDate(LocalDate.parse(endDate));

        return tripRepository.save(trip).getId();
    }

    /**
     * @implSpec 구글 캘린더 일정 삭제 시 서버 Trip 데이터도 같이 삭제됨
     * @param id 다이어리 아이디
     * */
    public void deleteEvent(Long id) throws Exception{
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND));

        googleCalendarService.deleteEvent(trip.getGoogleEventId());
        tripRepository.delete(trip);
    }

    /**
     * @implSpec 특정 날짜의 서버 Trip 데이터 조회
     * @param date String 형식으로 날짜 작성 ex) yyyy-MM-dd
     * @return List<Diary> 날짜에 대한 다이어리 모두 반환
     * */
    public Optional<Trip> getEventsByDate(Long userId, Long tripId, String date) {
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
}
