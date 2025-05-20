package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.DiaryContent;
import com.skuniv.fuwarilog.domain.DiaryList;
import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.dto.DiaryContentRequest;
import com.skuniv.fuwarilog.dto.DiaryListResponse;
import com.skuniv.fuwarilog.dto.DiaryResponse;
import com.skuniv.fuwarilog.dto.TripResponse;
import com.skuniv.fuwarilog.repository.DiaryContentRepository;
import com.skuniv.fuwarilog.repository.DiaryListRepository;
import com.skuniv.fuwarilog.repository.DiaryRepository;
import com.skuniv.fuwarilog.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryContentRepository diaryContentRepository;
    private final DiaryListRepository diaryListRepository;
    private final DiaryRepository diaryRepository;
    private final TripRepository tripRepository;

    /**
     * @implSpec 다이어리 폴더 조회
     * @param userId 사용자 고유 번호
     */
    public List<TripResponse.TripInfoDTO> getAllDiaries(Long userId) {
        List<Trip> trips = tripRepository.findAllByUserId(userId);

        return trips.stream()
                .map(trip -> {
                    return TripResponse.TripInfoDTO.builder()
                            .tripId(trip.getId())
                            .title(trip.getTitle())
                            .country(trip.getCountry())
                            .eventId(trip.getGoogleEventId())
                            .description(trip.getDescription())
                            .startDate(trip.getStartDate())
                            .endDate(trip.getEndDate())
                            .diaries(trip.getDiaries().stream()
                                    .map(DiaryResponse.DiaryResDTO::from)
                                    .collect(Collectors.toList()))
                            .build();
                }).collect(Collectors.toList());
    }

    /**
     * @implSpec 다이어리 폴더내 리스트 조회
     * @param userId 사용자 고유 번호
     */
    public List<DiaryListResponse.DiaryListResDTO> getAllDiaryList(Long userId, Long diaryId) {

        List<DiaryList> diaryList = diaryListRepository.findAllByDiaryId(diaryId);
        return diaryList.stream()
                .map(DiaryListResponse.DiaryListResDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * @implSpec 다이어리 내용 저장 및 수정
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     * @param dto 다이어리 고유번호 내용
     */
    public DiaryContent saveOrUpdateDiaryContent(DiaryContentRequest.ContentDTO dto, Long diaryListId, Long userId, MultipartFile image) {
        Optional<DiaryContent> existing = diaryContentRepository.findByUserIdAndDiaryListId(userId, diaryListId);

        String originalContent = dto.getContent();
        DiaryList list = diaryListRepository.findById(diaryListId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

        DiaryContent contentDoc = existing.orElse(
                DiaryContent.builder()
                        .userId(userId)
                        .diaryListId(diaryListId)
                        .content(originalContent)
                        .build()
        );

        contentDoc.setDiaryListId(diaryListId);
        contentDoc.setContent(originalContent);
        list.setUpdatedAt(LocalDateTime.now());

        return diaryContentRepository.save(contentDoc);
    }


    /**
     * @implSpec 다이어리 내용 조회
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     */
    public DiaryContent getDiaryContent(Long userId, Long diaryListId) {
        return diaryContentRepository.findByUserIdAndDiaryListId(userId, diaryListId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));
    }

    /**
     * @implSpec 다이어리 내용 안의 태그 삭제
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 일정
     * @param tag 특정 String 태그
     */
    @Transactional
    public void removeTagFromContent(Long userId, Long diaryListId, String tag) {
        DiaryContent contentDoc = diaryContentRepository
                .findByUserIdAndDiaryListId(userId, diaryListId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));

        String currentContent = contentDoc.getContent();
        DiaryList list = diaryListRepository.findById(contentDoc.getDiaryListId())
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

        String tagToRemove = "#" + tag.replaceAll("\\s+", "");
        String updatedContent = currentContent.replace(tagToRemove, "").replaceAll("(?m)^\\s*$[\r\n]+", "");
        contentDoc.setContent(updatedContent.trim());
        list.setUpdatedAt(LocalDateTime.now());
        diaryContentRepository.save(contentDoc);
    }
}
