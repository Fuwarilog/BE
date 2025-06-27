package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.dto.DiaryContent.DiaryContentRequest;
import com.skuniv.fuwarilog.dto.DiaryList.DiaryListResponse;
import com.skuniv.fuwarilog.dto.Diary.DiaryResponse;
import com.skuniv.fuwarilog.dto.Trip.TripResponse;
import com.skuniv.fuwarilog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryContentRepository diaryContentRepository;
    private final DiaryListRepository diaryListRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * @param userId 사용자 고유 번호
     * @implSpec 다이어리 폴더 조회
     */
    public List<TripResponse.TripInfoDTO> getAllDiaries(Long userId) {
        try {
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
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @param userId 사용자 고유 번호
     * @return result 다이어리 리스트 반환
     * @implSpec 다이어리 폴더내 리스트 조회
     */
    public List<DiaryListResponse.DiaryListResDTO> getAllDiaryList(Long userId, Long diaryId, Boolean isPublic) {
        try {
            List<DiaryListResponse.DiaryListResDTO> result;

            List<DiaryList> diaryList = diaryListRepository.findAllByDiaryIdOrderByDateAsc(diaryId);


            if (isPublic != null) {
                result = diaryList.stream()
                        .filter(diaryList1 -> diaryList1.getIsPublic() == isPublic)
                        .map(diaryList1 -> {
                            return DiaryListResponse.DiaryListResDTO.builder()
                                    .id(diaryList1.getId())
                                    .diaryId(diaryList1.getDiary().getId())
                                    .title(diaryList1.getDiary().getTitle())
                                    .date(diaryList1.getDate())
                                    .isPublic(diaryList1.getIsPublic())
                                    .build();
                        })
                        .collect(Collectors.toList());
            } else {
                result = diaryList.stream()
                        .map(DiaryListResponse.DiaryListResDTO::from)
                        .collect(Collectors.toList());
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @param userId      사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     * @param dto         다이어리 고유번호 내용
     * @implSpec 다이어리 내용 작성 기능
     */
    public DiaryContent createDiaryContent(DiaryContentRequest.ContentDTO dto, Long diaryListId, Long userId, MultipartFile image) {
        try {
            if (diaryContentRepository.findByUserIdAndDiaryListId(userId, diaryListId).isPresent()) {
                throw new BadRequestException(ErrorResponseStatus.ALREADY_EXIST_CONTENT);
            }

            DiaryList list = diaryListRepository.findById(diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

            DiaryContent content = DiaryContent.builder()
                    .userId(userId)
                    .diaryListId(diaryListId)
                    .tripDate(list.getDate())
                    .content(dto.getContent())
                    .build();

            if (image != null && !image.isEmpty()) {
                String imageUrl = storeDiaryImage(image);
                content.setImageUrls(List.of(imageUrl));
            } else {
                content.setImageUrls(null);
            }

            return diaryContentRepository.save(content);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }


    /**
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     * @param dto 다이어리 고유번호 내용
     * @implSpec 다이어리 내용 수정 기능
     */
    public DiaryContent editDiaryContent(DiaryContentRequest.ContentDTO dto, Long diaryListId, Long userId, MultipartFile image, String tag, Boolean isPublic) {
        try {
            DiaryContent content = diaryContentRepository.findByDiaryListId(diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

            // 1. 공개여부 설정
            if (isPublic != null) {
                isPublicDiary(content, isPublic);
            }

            // 2. 태그 수정 설정
            if (tag != null && !tag.isEmpty()) {
                removeTagFromContent(content, tag);
            }

            // 3. 이미지 수정 설정
            if (image != null && !image.isEmpty()) {
                String imageUrl = storeDiaryImage(image);
                content.setImageUrls(List.of(imageUrl));
            }

            content.setContent(dto.getContent());

            return diaryContentRepository.save(content);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }

    private String storeDiaryImage(MultipartFile image) {
        try {
            String uploadDir = "uploads/diary/";
            File profile_dir = new File(uploadDir);
            if (!profile_dir.exists()) profile_dir.mkdir();

            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            Path filePath = Paths.get(uploadDir + filename);
            Files.write(filePath, image.getBytes());

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/static/diary/")
                    .path(filename)
                    .toUriString();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SVAE_DIARY_IMAGE_ERROR);
        }
    }

    private void removeTagFromContent(DiaryContent contentDoc, String tag) {
        String currentContent = contentDoc.getContent() != null ? contentDoc.getContent() : "";

        // 태그 정규화
        String normalizedTag = tag.replaceAll("\\s+", "").replaceFirst("^#", "");
        String tagToRemove = "#" + normalizedTag;

        // 본문 내용에서 태그 문자열 제거
        String updatedContent = currentContent.replace(tagToRemove, "")
                .replaceAll("(?m)^\\s*$[\r\n]+", "")
                .trim();
        contentDoc.setContent(updatedContent);

        // tags 배열에서 해당 태그 삭제
        List<LocationTag> tags = contentDoc.getTags();
        if (tags != null && !tags.isEmpty()) {
            tags.removeIf(t -> {
                if (t.getTagText() == null) return false;
                String dbTag = t.getTagText().replaceAll("\\s+", "").replaceFirst("^#", "");
                return dbTag.equalsIgnoreCase(normalizedTag);
            });

            // 수정된 태그 리스트 설정
            contentDoc.setTags(tags);
        }
    }

    private void isPublicDiary(DiaryContent content, Boolean isPublic) {
        try {
            DiaryList diaryList = diaryListRepository.findById(content.getDiaryListId())
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

            if (diaryList.getIsPublic().equals(isPublic)) {
                log.info("다이어리 상태가 동일함으로 변경하지 않음");
                return;
            }

            Post existingPost = postRepository.findByDiaryList(diaryList);

            if (!isPublic) {
                if (existingPost != null) {
                    postRepository.delete(existingPost);
                    log.info("Post 삭제 완료. diaryListId: {}", diaryList.getId());
                }
            } else {
                if (existingPost == null) {
                    Post post = Post.builder()
                            .diaryList(diaryList)
                            .build();
                    postRepository.save(post);
                    log.info("새로운 Post 생성 완료");
                } else {
                    log.info("이미 존재하는 Post 입니다.");
                }
            }

            diaryList.setIsPublic(isPublic);
            diaryListRepository.save(diaryList);
            log.info(diaryList.toString());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }


    /**
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     * @implSpec 다이어리 내용 조회
     */
    public DiaryContent getDiaryContent(Long userId, Long diaryListId) {
        try {
            return diaryContentRepository.findByDiaryListId(diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }


    /**
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 일정
     * @param isPublic 공개/비공개 설정 값
     * @implSpec 다이어리 공개/비공개 설정
     */
    public DiaryListResponse.isPublicDiaryDTO isPublicDiaryContent(Long diaryListId, Long userId, Boolean isPublic) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            DiaryList diaryList = diaryListRepository.findById(diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

            Post post = postRepository.findByDiaryList(diaryList);

            if (!isPublic) {
                postRepository.delete(post);
            } else {
                post = Post.builder()
                        .diaryList(diaryList)
                        .build();
                postRepository.save(post);
            }

            diaryList.setIsPublic(isPublic);
            diaryListRepository.save(diaryList);
            log.info(diaryList.toString());

            return DiaryListResponse.isPublicDiaryDTO.from(diaryList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }
}
