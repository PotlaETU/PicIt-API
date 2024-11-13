package com.picit.iam.controller;

import com.picit.mongoutils.ExportAndImportService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ExportAndImportService exportAndImportService;

    @GetMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> backupDataCsv(String filePath, @RequestParam("collection") String collectionName) {
        return exportAndImportService.backupDataCsv(filePath, collectionName);
    }
}
