package com.picit.mongoutils;

import com.picit.iam.entity.User;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Post;
import com.picit.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportAndImportService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    private static final String STATIC_DIR = "src/main/resources/static";

    public ResponseEntity<Void> backupDataCsv(String collectionName) {
        if ("users".equals(collectionName)) {
            return writeUsersToCsv();
        } else if ("posts".equals(collectionName)) {
            return writePostsToCsv();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<Void> writeUsersToCsv() {
        List<User> users = userRepository.findAll();
        String filePath = Paths.get(STATIC_DIR, "users.csv").toString();
        try (FileOutputStream fos = new FileOutputStream(filePath);
             CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(fos), CSVFormat.DEFAULT)) {
            for (User user : users) {
                printer.printRecord(user.getId(), user.getUsername(),
                        user.getEmail(), user.getPassword(), user.getRole());
            }
            printer.flush();
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write users to csv file", e);
        }
    }

    private ResponseEntity<Void> writePostsToCsv() {
        List<Post> posts = postRepository.findAll();
        String filePath = Paths.get(STATIC_DIR, "users.csv").toString();
        try (FileOutputStream fos = new FileOutputStream(filePath);
             CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(fos), CSVFormat.DEFAULT)) {
            for (Post post : posts) {
                printer.printRecord(post.getId(), post.getContent(),
                        post.getComments(), post.getHobby(), post.getIsPublic(), post.getUserId(),
                        post.getLikes(), post.getCreatedAt(), post.getUpdatedAt());
            }
            printer.flush();
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write posts to csv file", e);
        }

    }
}
