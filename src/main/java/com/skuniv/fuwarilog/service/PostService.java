package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.dto.PostResponse;
import com.skuniv.fuwarilog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {


    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryListRepository diaryListRepository;
    private final TripRepository tripRepository;
    private final PostRepository postRepository;

    public List<PostResponse.PostListDTO> getPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        Trip trip = tripRepository.findByUserId(userId);

        Diary diary = diaryRepository.findByTripId(trip.getId());

        DiaryList diaryList = diaryListRepository.findByDiaryId(diary.getId());

        Post post = postRepository.findByDiaryList(diaryList);

        List<PostResponse.PostListDTO> result = new ArrayList<>();
        return result.stream()
                .map(PostResponse.PostListDTO::from)
                .collect(Collectors.toList());
    }
}
