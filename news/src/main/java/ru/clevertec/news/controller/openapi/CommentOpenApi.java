package ru.clevertec.news.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.BanResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.NotFoundElementResponse;
import ru.clevertec.exceptionhandlerstarter.handler.response.impl.PatchResponse;
import ru.clevertec.news.dto.comment.CommentFilter;
import ru.clevertec.news.dto.comment.CommentRequest;
import ru.clevertec.news.dto.comment.CommentResponse;
import ru.clevertec.news.util.patch.PatchRequest;

import java.util.List;

@RestController
@Tag(name = "Comment", description = "The Comment Api")
public interface CommentOpenApi {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/comments",
            produces = "application/json"
    )
    @Operation(
            operationId = "findAll",
            tags = "Comment",
            summary = "Find all Comment with pagination and filter.",
            parameters = {
                    @Parameter(name = "id", example = "78"),
                    @Parameter(name = "time", example = "2023-11-22T17:24:57.073106"),
                    @Parameter(name = "username", example = "username-41"),
                    @Parameter(name = "text", example = "qnwipagtbjubgbchmwzdsknqhlcvqebawzlocilpr"),
                    @Parameter(name = "newsId", example = "8"),
                    @Parameter(name = "page", example = "1"),
                    @Parameter(name = "size", example = "2"),
                    @Parameter(name = "sort", example = "time")
            })
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommentResponse.class)),
                    examples = @ExampleObject("""
                            [
                               {
                                 "id": 78,
                                 "time": "2023-11-22T17:24:57:073",
                                 "username": "username-41",
                                 "text": "qnwipagtbjubgbchmwzdsknqhlcvqebawzlocilpr",
                                 "newsId": 8
                               },
                               {
                                 "id": 118,
                                 "time": "2023-03-25T01:23:13:214",
                                 "username": "username-41",
                                 "text": "omklexwvbiwvinwxemzssv",
                                 "newsId": 12
                               }
                            ]
                            """)))
    ResponseEntity<List<CommentResponse>> findAll(
            @NotNull @Parameter(name = "filter", in = ParameterIn.PATH) CommentFilter filter,
            @NotNull @Parameter(name = "pageable", in = ParameterIn.PATH) Pageable pageable);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/comments/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "get",
            tags = "Comment",
            summary = "Find Comment by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 17,
                                      "time": "2023-11-03T11:04:50:970",
                                      "username": "username-3",
                                      "text": "xlnicpzobxocwqatzzgqrnnlpctfjxeaglucxukwru",
                                      "newsId": 2
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundElementResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                              "message": "Comment with id = 201 not found"
                                            }
                                            """))
                    })
    })
    ResponseEntity<CommentResponse> get(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/comments",
            produces = "application/json"
    )
    @Operation(
            operationId = "post",
            tags = "Comment",
            summary = "Save Comment"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 201,
                                      "time": "2023-06-09T01:36:48:681",
                                      "username": "newName",
                                      "text": "something",
                                      "newsId": 20
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject("[newsId: 'должно быть больше 0']"),
                                    @ExampleObject("[newsId: 'не должно равняться null']"),
                                    @ExampleObject("[text: 'не должно быть пустым']"),
                                    @ExampleObject("[username: 'не должно быть пустым']")
                            }))
    })
    ResponseEntity<CommentResponse> post(
            @Parameter(name = "commentRequest", required = true,
                    example = """
                            {
                              "username": "newName",
                              "text": "something",
                              "newsId": 20
                            }
                            """) @RequestBody @Valid CommentRequest commentRequest);

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/comments/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "put",
            tags = "Comment",
            summary = "Update Comment"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 201,
                                      "time": "2023-06-09T02:29:20:497",
                                      "username": "updatedName",
                                      "text": "updatedSomething",
                                      "newsId": 20
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject("[text: 'не должно быть пустым']"),
                                    @ExampleObject("[username: 'не должно быть пустым']")
                            })),
            @ApiResponse(
                    responseCode = "406",
                    description = "Not Acceptable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotFoundElementResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "message": "Comment with id = 999 not found"
                                    }
                                    """)))
    })
    ResponseEntity<CommentResponse> put(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH, example = "201") @PathVariable("id") Long id,
            @Parameter(name = "request", required = true,
                    example = """
                            {
                              "username": "updatedName",
                              "text": "updatedSomething",
                              "newsId": 20
                            }
                            """) @RequestBody @Valid CommentRequest request);


    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/comments/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "patch",
            tags = "Comment",
            summary = "Update Comment property"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 201,
                                      "time": "2023-06-09T02:42:03:772",
                                      "username": "updatedName",
                                      "text": "updated only text",
                                      "newsId": 20
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatchResponse.class),
                            examples = {
                                    @ExampleObject("[field: 'не должно быть пустым']"),
                                    @ExampleObject("[value: 'не должно равняться null']"),
                                    @ExampleObject("""
                                            {
                                              "message": "Invalid json: updated only text"
                                            }
                                            """)
                            })
                    }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BanResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "message": "You cannot edit the field 'time' yourself"
                                    }
                                    """))
                    }),
            @ApiResponse(
                    responseCode = "406",
                    description = "Not Acceptable",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatchResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "message": "Element 'nonExistProperty' not found."
                                    }
                                    """))
                    })
    })
    ResponseEntity<CommentResponse> patch(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id,
            @Parameter(name = "patchRequest", required = true,
                    example = """
                            {
                              "field": "text",
                              "value": "\\"updated only text\\""
                            }
                            """) @Valid @RequestBody PatchRequest patchRequest
    );

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/comments/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "delete",
            tags = "Comment",
            summary = "Delete Comment by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content"),
            @ApiResponse(
                    responseCode = "406",
                    description = "Not Acceptable",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundElementResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                              "message": "Comment with id = 201 not found"
                                            }
                                            """))
                    })
    })
    ResponseEntity<Void> delete(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id);

}