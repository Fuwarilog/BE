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
     * @implSpec 다이어리 폴더 조회
     * @param userId 사용자 고유 번호
     */
    public List<TripResponse.TripInfoDTO> getAllDiaries(Long userId) {
        try{
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
        } catch (Exception e){
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @implSpec 다이어리 폴더내 리스트 조회
     * @param userId 사용자 고유 번호
     * @return result 다이어리 리스트 반환
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
        } catch (Exception e){
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @implSpec 다이어리 내용 작성 기능
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     * @param dto 다이어리 고유번호 내용
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
        } catch (Exception e){
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }


    /**
     * @implSpec 다이어리 내용 수정 기능
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     * @param dto 다이어리 고유번호 내용
     */
    public DiaryContent editDiaryContent(DiaryContentRequest.ContentDTO dto, Long diaryListId, Long userId, MultipartFile image) {
        try {
            DiaryContent content = diaryContentRepository.findByUserIdAndDiaryListId(userId, diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

            content.setContent(dto.getContent());

            if (image != null && !image.isEmpty()) {
                String imageUrl = storeDiaryImage(image);
                content.setImageUrls(List.of(imageUrl));
            }

            return diaryContentRepository.save(content);
        } catch (Exception e){
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


    /**
     * @implSpec 다이어리 내용 조회
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 번호
     */
    public DiaryContent getDiaryContent(Long userId, Long diaryListId) {
        try {
            return diaryContentRepository.findByUserIdAndDiaryListId(userId, diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));
        } catch (Exception e){
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
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

    /**
     * @implSpec 다이어리 공개/비공개 설정
     * @param userId 사용자 고유 번호
     * @param diaryListId 다이어리 고유 일정
     * @param isPublic 공개/비공개 설정 값
     */
    public DiaryListResponse.isPublicDiaryDTO isPublicDiary(Long diaryListId, Long userId, Boolean isPublic) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            DiaryList diaryList = diaryListRepository.findById(diaryListId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

            Post post = postRepository.findByDiaryList(diaryList);

            if(!isPublic) {
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
        } catch (Exception e){
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }
}
