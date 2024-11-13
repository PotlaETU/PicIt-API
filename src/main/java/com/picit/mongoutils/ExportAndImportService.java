package com.picit.mongoutils;

import com.picit.iam.entity.User;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Post;
import com.picit.post.repository.PostRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportAndImportService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ResponseEntity<InputStreamResource> backupDataCsv(String filePath, String collectionName) {
        if ("users".equals(collectionName)) {
            return writeUsersToCsv(filePath);
        } else if ("posts".equals(collectionName)) {
            writePostsToCsv(filePath);
            return writePostsToCsv(filePath);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<InputStreamResource> writeUsersToCsv(String filePath) {
        List<User> users = userRepository.findAll();
        try (FileWriter out = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            for (User user : users) {
                printer.printRecord(user.getId(), user.getUsername(),
                        user.getEmail(), user.getPassword(), user.getRole());
            }
            return getInputStreamResourceResponseEntity(out, printer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write users to csv file");
        }
    }

    private ResponseEntity<InputStreamResource> writePostsToCsv(String filePath) {
        List<Post> posts = postRepository.findAll();
        try (FileWriter out = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            for (Post post : posts) {
                printer.printRecord(post.getId(), post.getContent(),
                        post.getComments(), post.getHobby(), post.getIsPublic(), post.getUserId(),
                        post.getLikes(), post.getCreatedAt(), post.getUpdatedAt());
            }
            return getInputStreamResourceResponseEntity(out, printer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write posts to csv file");
        }
    }

    @NotNull
    private ResponseEntity<InputStreamResource> getInputStreamResourceResponseEntity(FileWriter out, CSVPrinter printer) throws IOException {
        printer.flush();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}
