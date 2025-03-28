package com.picit.iam.controller;

import com.picit.iam.controller.documentation.AdminControllerDocumentation;
import com.picit.iam.dto.responsetype.MessageResponse;
import com.picit.mongoutils.ExportAndImportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminControllerDocumentation {

    private final ExportAndImportService exportAndImportService;

    @GetMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> backupDataCsv(@RequestParam("collection") String collectionName) {
        return exportAndImportService.backupDataCsv(collectionName);
    }
}
