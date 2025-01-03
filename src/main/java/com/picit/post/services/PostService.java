package com.picit.post.services;

import com.picit.iam.entity.User;
import com.picit.iam.entity.points.PointDefinition;
import com.picit.iam.exceptions.PostNotFound;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.repository.UserProfileRepository;
import com.picit.iam.repository.UserRepository;
import com.picit.iam.repository.points.PointsRepository;
import com.picit.post.criteria.PostCriteria;
import com.picit.post.dto.PostDto;
import com.picit.post.dto.request.PostImageRequestDto;
import com.picit.post.dto.request.PostRequestDto;
import com.picit.post.entity.Post;
import com.picit.post.entity.postimage.PostImage;
import com.picit.post.mapper.PostMapper;
import com.picit.post.repository.PostRepository;
import com.picit.post.repository.postpic.PostImageRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final UserProfileRepository userProfileRepository;
    private final PostRepository postRepository;
    private final PointsRepository pointsRepository;
    private final PostImageRepository postImageRepository;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Value("${generate-ai-images.uri}")
    private String urlAi;

    @Value("${generate-ai-images.uri-get-images}")
    private String urlAiGetImages;

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
        if (isFirstPostPosted(user.getId())) {
            var points = pointsRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserNotFound("User has no points"));
            points.setPoints(points.getPoints() + PointDefinition.CREATE_POST.getPoints());
            pointsRepository.save(points);
        }
        postRepository.save(post);
        return postMapper.postToPostDto(post);
    }

    public ResponseEntity<String> setPostImage(MultipartFile file, String username, String postId, Boolean aiGenerated, PostImageRequestDto postImageRequestDto) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFound("Post not found"));
        var userId = userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        if (post.getPostImage() != null || (aiGenerated && postImageRequestDto.prompt() == null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        if (!post.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        if (aiGenerated) {
            return generatePostImageAi(postImageRequestDto.prompt());
        }
        try {
            var profilePicture = PostImage.builder()
                    .postId(postId)
                    .userId(userId)
                    .aiGenerated(false)
                    .description(postImageRequestDto.description())
                    .image(new Binary(file.getBytes()))
                    .build();
            postImageRepository.save(profilePicture);
            post.setPostImage(profilePicture);
            postRepository.save(post);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
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

    private Boolean isFirstPostPosted(String userId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return postRepository.existsPostByUserIdAndCreatedAtAfter(userId, startOfDay);
    }

    private ResponseEntity<String> generatePostImageAi(String prompt) {
        urlAi = urlAi + "generate_post_pic";
        var res = restTemplate.postForEntity(urlAi, prompt, String.class);
        if (res.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        var image = restTemplate.getForEntity(urlAiGetImages + "generated_image.png", byte[].class).getBody();
        if (image == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        try {
            var postImage = PostImage.builder()
                    .aiGenerated(true)
                    .image(new Binary(image))
                    .description(prompt)
                    .build();
            postImageRepository.save(postImage);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
