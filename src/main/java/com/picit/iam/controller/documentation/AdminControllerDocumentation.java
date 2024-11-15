package com.picit.iam.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin", description = "Admin operations")
public interface AdminControllerDocumentation {

    @Operation(summary = "Backup data to CSV", description = "Backs up the data of the specified collection to a CSV file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data backed up successfully to src/main/resources/static"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<Void> backupDataCsv(String collectionName);
}
