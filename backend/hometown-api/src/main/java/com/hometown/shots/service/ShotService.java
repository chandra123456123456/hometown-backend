package com.hometown.shots.service;

import com.hometown.common.security.CurrentUser;
import com.hometown.common.web.ApiException;
import com.hometown.shots.ShotProperties;
import com.hometown.shots.domain.Shot;
import com.hometown.shots.domain.ShotComment;
import com.hometown.shots.domain.ShotLike;
import com.hometown.shots.dto.*;
import com.hometown.shots.repo.ShotCommentRepository;
import com.hometown.shots.repo.ShotLikeRepository;
import com.hometown.shots.repo.ShotRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ShotService {

    private final ShotRepository shotRepo;
    private final ShotLikeRepository likeRepo;
    private final ShotCommentRepository commentRepo;
    private final ShotProperties props;

    public ShotService(ShotRepository shotRepo,
                       ShotLikeRepository likeRepo,
                       ShotCommentRepository commentRepo,
                       ShotProperties props) {
        this.shotRepo = shotRepo;
        this.likeRepo = likeRepo;
        this.commentRepo = commentRepo;
        this.props = props;
    }

    public List<ShotResponse> list() {
        return shotRepo.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ShotResponse upload(MultipartFile file, String title, String caption) {
        try {
            Path dir = Paths.get(props.getDir());
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.'))
                    : ".mp4";
            String filename = UUID.randomUUID() + ext;

            Files.write(dir.resolve(filename), file.getBytes());

            Shot shot = new Shot();
            shot.setTitle(title);
            shot.setCaption(caption);
            shot.setVideoUrl("/api/shots/media/" + filename);
            shot.setUploadedBy(CurrentUser.id());
            shot = shotRepo.save(shot);
            return toResponse(shot);
        } catch (IOException e) {
            throw ApiException.badRequest("Failed to store video: " + e.getMessage());
        }
    }

    @Transactional
    public LikeResponse toggleLike(Long shotId) {
        Long uid = CurrentUser.id();
        boolean liked;
        if (likeRepo.existsByShotIdAndUserId(shotId, uid)) {
            likeRepo.findByShotIdAndUserId(shotId, uid).ifPresent(likeRepo::delete);
            liked = false;
        } else {
            likeRepo.save(new ShotLike(shotId, uid));
            liked = true;
        }
        return new LikeResponse(liked, likeRepo.countByShotId(shotId));
    }

    public List<CommentResponse> comments(Long shotId) {
        return commentRepo.findByShotIdOrderByIdAsc(shotId)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Transactional
    public CommentResponse addComment(Long shotId, CommentRequest req) {
        Long uid = CurrentUser.id();
        String email = CurrentUser.email();
        String name = (email != null && email.contains("@"))
                ? email.substring(0, email.indexOf('@'))
                : "User";

        ShotComment comment = new ShotComment();
        comment.setShotId(shotId);
        comment.setUserId(uid);
        comment.setUserName(name);
        comment.setParentId(req.parentId());
        comment.setText(req.text());
        comment = commentRepo.save(comment);
        return toCommentResponse(comment);
    }

    private ShotResponse toResponse(Shot shot) {
        long likeCount = likeRepo.countByShotId(shot.getId());
        long commentCount = commentRepo.countByShotId(shot.getId());
        boolean likedByMe = false;
        try {
            Long uid = CurrentUser.id();
            likedByMe = likeRepo.existsByShotIdAndUserId(shot.getId(), uid);
        } catch (Exception ignored) {}
        return new ShotResponse(
                shot.getId(),
                shot.getTitle(),
                shot.getCaption(),
                shot.getVideoUrl(),
                shot.getUploadedBy(),
                shot.getCreatedAt(),
                likeCount,
                likedByMe,
                commentCount
        );
    }

    private CommentResponse toCommentResponse(ShotComment c) {
        return new CommentResponse(
                c.getId(),
                c.getShotId(),
                c.getUserName(),
                c.getParentId(),
                c.getText(),
                c.getCreatedAt()
        );
    }
}
