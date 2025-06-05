package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.dto.Trip.TripRequest;
import com.skuniv.fuwarilog.dto.Trip.TripResponse;
import com.skuniv.fuwarilog.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TripService {

    private final GoogleCalendarService googleCalendarService;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryListRepository diaryListRepository;
    private final DiaryContentRepository diaryContentRepository;

    /**
     * @implSpec 구글켈린더에 월별 일정 조회
     * @param userId 사용자 고유 번호
     * @param year 연도
     * @param month 월
     * @return result 연도, 월의 여행일정 목록 반환
     * */
    public List<TripResponse.TripListDTO> getEventsByMonth(Long userId, int year, int month) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            List<Trip> trips = tripRepository.findAllByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(user, endDate, startDate);

            return trips.stream()
                    .map(TripResponse.TripListDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND);
        }
    }

    /**
     * @implSpec 구글켈린더에 현재 일정 조회
     * @param userId 사용자 고유 번호
     * @return result 오늘로부터 일주일 뒤의 여행일정 최대 3개 반환
     * */
    public List<TripResponse.TripListDTO> getEventsByToday(long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            LocalDate today = LocalDate.now();

            List<Trip> trips = tripRepository.findAllByUserAndToday(user, today);

            return trips.stream()
                    .map(TripResponse.TripListDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND);
        }
    }

    /**
     * @implSpec 구글켈린더에 현재 일정 조회
     * @param userId 사용자 고유 번호
     * @return result 연도, 월의 여행일정 목록 반환
     * */
    public List<TripResponse.TripListDTO> getEventsByNextWeek(long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            LocalDate today = LocalDate.now();

            Pageable pageable = PageRequest.of(0, 3);

            List<Trip> trips = tripRepository.findTop3ByUserAndStartDateOrderByStartDate(user, today, pageable);

            return trips.stream()
                    .map(TripResponse.TripListDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND);
        }
    }

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

        // 여행 일정 생성 시 자동으로 다이어리 생성되게끔 만들어야함
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

            DiaryContent newDiaryContent = DiaryContent.builder()
                    .userId(user.getId())
                    .diaryListId(newDiaries.getId())
                    .tripDate(d)
                    .build();
            diaryContentRepository.save(newDiaryContent);
        }

        return TripResponse.TripInfoDTO.builder()
                .tripId(newTrip.getId())
                .title(newTrip.getTitle())
                .description(newTrip.getDescription())
                .startDate(newTrip.getStartDate())
                .endDate(newTrip.getEndDate())
                .country(newTrip.getCountry())
                .eventId(newTrip.getGoogleEventId())
                .build();
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
     * @implSpec 특정 날짜의 Trip 데이터 조회
     * @param tripId 특정 여행일정 아이디
     * @return List<Trip> 날짜에 대한 여행일정을 반환
     * */
    public List<TripResponse.TripInfoDTO> getEvents(Long userId, Long tripId) {
        // 1. 여행 리스트 객체 생성
        List<Trip> tripList;

        // 2. 사용자 존재 확인
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 3. 변수값 유무에 따라 달라짐
        if (tripId != null) {
            tripList = tripRepository.findAllById(tripId);
        } else {
            tripList = tripRepository.findAllByUser(user);
        }

        return tripList.stream()
                .map(trip -> {
                   return TripResponse.TripInfoDTO.builder()
                           .tripId(trip.getId())
                           .title(trip.getTitle())
                           .country(trip.getCountry())
                           .eventId(trip.getGoogleEventId())
                           .description(trip.getDescription())
                           .startDate(trip.getStartDate())
                           .endDate(trip.getEndDate())
                           .build();
                }).collect(Collectors.toList());

    }

    /**
     * @implSpec 여행 일정 수정
     * @param userId 사용자 아이디
     * @param tripId 여행일정 아이디
     * @param infoDTO 여행 데이터 DTO
     * @return TripResponse.TripInfoDTO 수정된 일정값 반환
     * */
    public TripResponse.TripInfoDTO editEvent(Long userId, Long tripId, TripRequest.TripInfoDTO infoDTO) {
        // 1. 사용자 존재 확인
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 2. 여행일정 존재 확인
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.TRIP_NOT_FOUND));

        // 3. 다이어리 확인
        Diary diary = diaryRepository.findByTrip(trip)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.DIARY_NOT_FOUND));

        // 4. 날짜 변경 시 다이어리 폴더 및 리스트 업데이트
        if(infoDTO.getStartDate().isEqual(trip.getStartDate()) && infoDTO.getEndDate().isEqual(trip.getEndDate())) {
            trip.setTitle(infoDTO.getTitle());
            trip.setDescription(infoDTO.getDescription());
            trip.setCountry(infoDTO.getCountry());
            trip = tripRepository.save(trip);

        } else {

            trip.setTitle(infoDTO.getTitle());
            trip.setDescription(infoDTO.getDescription());
            trip.setStartDate(infoDTO.getStartDate());
            trip.setEndDate(infoDTO.getEndDate());
            trip.setCountry(infoDTO.getCountry());
            trip = tripRepository.save(trip);

            diary.setTrip(trip);
            diary.setTitle(infoDTO.getTitle());
            diary.setStartDate(infoDTO.getStartDate());
            diary.setEndDate(infoDTO.getEndDate());
            diary = diaryRepository.save(diary);

            List<DiaryList> existingLists = diaryListRepository.findByDiary(diary);
            Set<LocalDate> existingDates = existingLists.stream()
                    .map(DiaryList::getDate)
                    .collect(Collectors.toSet());

            Set<DiaryList> existingList = existingLists.stream()
                    .map(diaryList -> DiaryList.builder()
                            .id(diaryList.getId())
                            .date(diaryList.getDate())
                            .build())
                    .collect(Collectors.toSet());

            Set<LocalDate> newDates = new HashSet<>();
            for (LocalDate d = infoDTO.getStartDate(); !d.isAfter(infoDTO.getEndDate()); d = d.plusDays(1)) {
                newDates.add(d);
            }

            Set<LocalDate> toAdd = new HashSet<>(newDates);
            toAdd.removeAll(existingDates);

            Set<LocalDate> toRemove = new HashSet<>(existingDates);
            toRemove.removeAll(newDates);

            Set<DiaryList> toRemoveList = new HashSet<>(existingList);
            toRemoveList.removeAll(newDates);

            // 추가된 일정의 다이어리 생성
            for(LocalDate d : toAdd) {
                DiaryList addDiaryList = DiaryList.builder()
                        .diary(diary)
                        .date(d)
                        .build();
                diaryListRepository.save(addDiaryList);

                diaryContentRepository.save(DiaryContent.builder()
                        .diaryListId(addDiaryList.getId())
                        .userId(userId)
                        .tripDate(d)
                        .build());
            }

            // 삭제
            for (LocalDate d : toRemove) {
                diaryListRepository.deleteByDiaryAndDate(diary, d);
            }

            for (DiaryList i : toRemoveList) {
                diaryContentRepository.deleteByDiaryListIdAndTripDate(i.getId(), i.getDate());
            }
        }

        return TripResponse.TripInfoDTO.builder()
                .tripId(trip.getId())
                .title(trip.getTitle())
                .country(trip.getCountry())
                .eventId(trip.getGoogleEventId())
                .description(trip.getDescription())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .build();
    }
}
