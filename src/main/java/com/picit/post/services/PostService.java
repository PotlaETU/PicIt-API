package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.exceptions.PostNotFound;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserProfileRepository;
import com.picit.iam.repository.UserRepository;
import com.picit.post.criteria.PostCriteria;
import com.picit.post.dto.PostDto;
import com.picit.post.dto.PostRequestDto;
import com.picit.post.entity.Post;
import com.picit.post.mapper.PostMapper;
import com.picit.post.repository.PostRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;

    public Page<PostDto> getPostsByUser(String username, String hobby, int page) {
        int pageSize = 10;
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var query = new Query(PostCriteria.postsByUserId(userId));
        return getPostDtos(hobby, page, pageSize, query);
    }

    public Page<PostDto> getPosts(String username, String hobby, int page) {
        int pageSize = 10;
        var userProfile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));

        var query = new Query(PostCriteria.postsVisibility(userProfile.getFollows()));
        return getPostDtos(hobby, page, pageSize, query);
    }

    @NotNull
    private Page<PostDto> getPostDtos(String hobby, int page, int pageSize, Query query) {
        if (hobby != null) {
            query.addCriteria(PostCriteria.postsByHobby(hobby));
        }
        long total = mongoTemplate.count(query.skip(-1).limit(-1), Post.class);
        Pageable pageable = PageRequest.of(page, pageSize);

        query.with(pageable);
        List<Post> posts = mongoTemplate.find(query, Post.class);

        return PageableExecutionUtils.getPage(posts.stream()
                .map(postMapper::postToPostDto)
                .toList(), pageable, () -> total);
    }

    public PostDto createPost(String username, PostRequestDto postDto) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var post = postMapper.postRequestDtoToPost(postDto, user.getId());
        postRepository.save(post);
        return postMapper.postToPostDto(post);
    }

    public ResponseEntity<Void> deletePost(String username, String postId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        if (!post.getUserId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        postRepository.delete(post);
        return ResponseEntity.ok().build();
    }

    public PostDto updatePost(String username, String postId, PostRequestDto postDto) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of the post");
        }
        Post updatedPost = postMapper.updatePostFromPostRequestDto(postDto, post);
        postRepository.save(updatedPost);
        return postMapper.postToPostDto(updatedPost);
    }

    public List<PostDto> searchPost(String username, String search) {
        var userProfile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var query = new Query(PostCriteria.postsVisibility(userProfile.getFollows()));
        List<Post> posts = postRepository.findPostsByContentRegex(".*" + search + ".*")
                .orElseThrow(() -> new PostNotFound("Post not found"));
        List<Post> postsForUser = mongoTemplate.find(query, Post.class)
                .stream()
                .filter(posts::contains)
                .toList();
        return postsForUser.stream()
                .map(postMapper::postToPostDto)
                .toList();
    }
}
