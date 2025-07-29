    package com.gradation.zmnnoory.domain.comment.service;

    import com.gradation.zmnnoory.domain.comment.dto.request.CommentCreateRequest;
    import com.gradation.zmnnoory.domain.comment.dto.request.CommentUpdateRequest;
    import com.gradation.zmnnoory.domain.comment.dto.response.CommentResponse;
    import com.gradation.zmnnoory.domain.comment.entity.Comment;
    import com.gradation.zmnnoory.domain.comment.exception.CommentAccessDeniedException;
    import com.gradation.zmnnoory.domain.comment.exception.CommentNotFoundException;
    import com.gradation.zmnnoory.domain.comment.repository.CommentRepository;
    import com.gradation.zmnnoory.domain.member.entity.Member;
    import com.gradation.zmnnoory.domain.member.exception.MemberNotFoundException;
    import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
    import com.gradation.zmnnoory.domain.video.entity.Video;
    import com.gradation.zmnnoory.domain.video.exception.VideoNotFoundException;
    import com.gradation.zmnnoory.domain.video.repository.VideoRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    @Transactional
    public class CommentService {

        private final CommentRepository commentRepository;
        private final VideoRepository videoRepository;
        private final MemberRepository memberRepository;

        public CommentResponse createComment(Long videoId, Long memberId, CommentCreateRequest request) {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new VideoNotFoundException(videoId));

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("맴버를 찾을 수 없습니다."));

            Comment comment = Comment.builder()
                    .video(video)
                    .member(member)
                    .content(request.content())
                    .build();

            Comment savedComment = commentRepository.save(comment);
            log.info("댓글 생성 완료. Comment ID: {}, Video ID: {}, Member ID: {}",
                    savedComment.getId(), videoId, memberId);

            return CommentResponse.of(savedComment);
        }

        @Transactional(readOnly = true)
        public List<CommentResponse> getCommentsByVideo(Long videoId) {
            if (!videoRepository.existsById(videoId)) {
                throw new VideoNotFoundException(videoId);
            }

            return commentRepository.findByVideoIdOrderByCreatedAtAsc(videoId).stream()
                    .map(CommentResponse::of)
                    .toList();
        }

        public CommentResponse updateComment(Long commentId, Long memberId, CommentUpdateRequest request) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException(commentId));

            if (!comment.getMember().getId().equals(memberId)) {
                throw new CommentAccessDeniedException(commentId);
            }

            comment.updateContent(request.content());
            log.info("댓글 수정 완료. Comment ID: {}, Member ID: {}", commentId, memberId);

            return CommentResponse.of(comment);
        }

        public void deleteComment(Long commentId, Long memberId) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException(commentId));

            if (!comment.getMember().getId().equals(memberId)) {
                throw new CommentAccessDeniedException(commentId);
            }

            commentRepository.delete(comment);
            log.info("댓글 삭제 완료. Comment ID: {}, Member ID: {}", commentId, memberId);
        }

        @Transactional(readOnly = true)
        public long getCommentCount(Long videoId) {
            if (!videoRepository.existsById(videoId)) {
                throw new VideoNotFoundException(videoId);
            }

            return commentRepository.countByVideoId(videoId);
        }
    }
