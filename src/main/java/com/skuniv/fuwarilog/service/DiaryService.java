package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.CoordinatingEntityNameResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
    private final TagRepository tagRepository;

    /**
     * @implSpec 초기 다이어리에 지도 데이터 저장
     * @param userId 사용자 고유번호
     * @param date 특정 날짜(오늘 날짜)
     */
    public String buildDiaryContent(Long userId, LocalDate date) {
        // 0. 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        StringBuilder content = new StringBuilder();

        // 1. 해당 날짜에 북마크된 장소 가져오기
        List<PlaceBookmark> places = locationRepository.findByUserIdAndDate(userId, date);
        if(!places.isEmpty()) {
            content.append("### 북마크한 장소\n");
            for (PlaceBookmark place : places) {
                content.append("- ").append(place.getPlaceName()).append(" (")
                        .append(place.getLat()).append(", ")
                        .append(place.getLng()).append(")\n");
            }
        }

        // 2. Tag 가져오기
        List<Tag> tags = tagRepository.findUserIdAndDate(userId, date);
        if(!tags.isEmpty()) {
            content.append("\n### 태그\n");
            for (Tag tag : tags) {
                content.append("#").append(tag.getTagValue()).append(" ");
            }
            content.append("\n");
        }

        // 3. 경로 저장... 보류

        return content.toString();
    }
}
