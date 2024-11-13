package com.picit.post.controller;

import com.picit.post.controller.documentation.LikeControllerDocumentation;
import com.picit.post.services.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController implements LikeControllerDocumentation {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Void> likePost(Authentication authentication, @RequestParam("postId") String postId) {
        return this.likeService.likePost(authentication.getName(), postId);
    }

    @DeleteMapping
    public ResponseEntity<Void> unlikePost(Authentication authentication, @RequestParam("postId") String postId) {
        return this.likeService.unlikePost(authentication.getName(), postId);
    }
}
