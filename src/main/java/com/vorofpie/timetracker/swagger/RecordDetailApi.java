package com.vorofpie.timetracker.swagger;

import com.vorofpie.timetracker.dto.error.AppError;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Record Detail API", description = "Record detail management API")
public interface RecordDetailApi {

    @Operation(summary = "Create a new record detail", description = "Create a new record detail. A regular user can only create a record in a project he/she is a member of. An administrator can create an entry for any project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Record detail created successfully", content = @Content(schema = @Schema(implementation = RecordDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    RecordDetailResponse createRecordDetail(@RequestBody RecordDetailRequest recordDetailRequest);

    @Operation(summary = "Update an existing record detail by ID", description = "Update a record detail by its ID. A regular user can only update a record in a project he/she is a member of. An administrator can update an entry for any project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record detail updated successfully", content = @Content(schema = @Schema(implementation = RecordDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "404", description = "Record detail not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    RecordDetailResponse updateRecordDetail(@PathVariable Long id, @RequestBody RecordDetailRequest recordDetailRequest);

    @Operation(summary = "Delete a record detail by ID", description = "Delete a record detail by its ID. A regular user can only delete a record in a project he/she is a member of. An administrator can delete an entry for any project.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Record detail deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Record detail not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    void deleteRecordDetail(@PathVariable Long id);

    @Operation(summary = "Retrieve a record detail by ID", description = "Get a record detail by its ID. Only users in the project group can get task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record detail retrieved successfully", content = @Content(schema = @Schema(implementation = RecordDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Record detail not found", content = @Content(schema = @Schema(implementation = AppError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = AppError.class)))
    })
    RecordDetailResponse getRecordDetailById(@PathVariable Long id);

    @Operation(summary = "Retrieve all record details", description = "Get a list of all record details. Regular user gets all records from project he/she participates in.")
    @ApiResponse(responseCode = "200", description = "Record details retrieved successfully", content = @Content(schema = @Schema(implementation = RecordDetailResponse.class)))
    List<RecordDetailResponse> getAllRecordDetails();
}
