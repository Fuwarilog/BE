package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.repository.DiaryContentRepository;
import com.skuniv.fuwarilog.repository.DiaryListRepository;
import com.skuniv.fuwarilog.repository.LocationRepository;
import com.skuniv.fuwarilog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.CoordinatingEntityNameResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryContentRepository diaryContentRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final DiaryListRepository diaryListRepository;

    /**
     * @implSpec 초기 다이어리에 지도 데이터 저장
     * @param userId 사용자 고유번호
     * @param diaryListId 다이어리 폴더의 포스트 고유번호
     * @param content 다이어리 작성 내용
     * @param date 특정 날짜(오늘 날짜)
     */
    public void saveDiaryWithLocationData(Long userId, Long diaryListId, String content, LocalDate date) {
        // 0. 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        DiaryList diaryList = diaryListRepository.findById(diaryListId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.DIARYLIST_NOT_FOUND));

        // 1. 해당 날짜에 북마크된 장소 가져오기
        List<Location> locate_bookmark = locationRepository.findByUserIdAndBookmarkedAt(userId, date.atStartOfDay(), date.plusDays(1).atStartOfDay());

        // 2. validatedRoute, Tag 가져오기
        List<Coordinate> visitedRoutes = locate_bookmark.stream()
                .map(loc -> new Coordinate(loc.getLatitude(), loc.getLongitude()))
                .collect(Collectors.toList());

        List<Tag> tags = locate_bookmark.stream()
                .filter(Location::isBookmarked)
                .map(loc -> new Tag(loc.getPlaceName()))
                .collect(Collectors.toList());

        // 3. mapActivity에 저장
        MapActivityMetadata mapActivity = new MapActivityMetadata();
        mapActivity.setVisitedRouts(visitedRoutes);
        mapActivity.setTags(tags);
        mapActivity.setBookmarks(locate_bookmark.stream().map(loc -> loc.getPlaceName()).collect(Collectors.toList())); //String으로 변환해서 가져옴

        // 4. 초기 다이어리에 저장됨
        DiaryContent diaryContent = new DiaryContent();
        diaryContent.setDiaryListId(diaryListId);
        diaryContent.setMapActivity(mapActivity);
        diaryContent.setContent(content);
        diaryContent.setCreatedAt(date);
        diaryContent.setUpdatedAt(LocalDate.now());

        diaryContentRepository.save(diaryContent);
    }
}
