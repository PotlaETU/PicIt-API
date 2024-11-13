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
import com.picit.post.repository.LikesRepository;
import com.picit.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
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

    public List<PostDto> getPostsByUser(String username, String hobby) {
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var query = new Query(PostCriteria.postsByUserId(userId));
        if (hobby != null) {
            query.addCriteria(PostCriteria.postsByHobby(hobby));
        }

        return mongoTemplate.find(query, Post.class)
                .stream()
                .map(postMapper::postToPostDto)
                .toList();
    }

    public List<PostDto> getPosts(String username, String hobby) {
        var userProfile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));

        var query = new Query(PostCriteria.postsVisibility(userProfile.getFollows()));
        if (hobby != null) {
            query.addCriteria(PostCriteria.postsByHobby(hobby));
        }
        return mongoTemplate.find(query, Post.class)
                .stream()
                .map(postMapper::postToPostDto)
                .toList();
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
}
