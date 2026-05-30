package com.hometown.shots.web;

import com.hometown.shots.dto.*;
import com.hometown.shots.service.ShotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
public class ShotController {

    private final ShotService service;

    public ShotController(ShotService service) {
        this.service = service;
    }

    @GetMapping("/api/shots")
    public List<ShotResponse> list() {
        return service.list();
    }

    @PostMapping(value = "/api/shots/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ShotResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "caption", required = false) String caption) {
        return ResponseEntity.ok(service.upload(file, title, caption));
    }

    @GetMapping("/api/shots/{id}/comments")
    public List<CommentResponse> comments(@PathVariable Long id) {
        return service.comments(id);
    }

    @PostMapping("/api/shots/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest body) {
        return ResponseEntity.ok(service.addComment(id, body));
    }

    @PostMapping("/api/shots/{id}/like")
    public ResponseEntity<LikeResponse> toggleLike(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleLike(id));
    }
}
