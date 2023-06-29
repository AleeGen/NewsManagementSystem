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
import ru.clevertec.news.dto.news.NewsFilter;
import ru.clevertec.news.dto.news.NewsRequest;
import ru.clevertec.news.dto.news.NewsResponse;
import ru.clevertec.news.dto.news.NewsWithoutCommentResponse;
import ru.clevertec.news.util.patch.PatchRequest;

import java.util.List;

@RestController
@Tag(name = "News", description = "The News Api")
public interface NewsOpenApi {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/news",
            produces = "application/json"
    )
    @Operation(
            operationId = "findAll",
            tags = "News",
            summary = "Find all News with pagination and filter, but without comments.",
            parameters = {
                    @Parameter(name = "id", example = "14"),
                    @Parameter(name = "time", example = "2023-09-07T05:56:15.949469"),
                    @Parameter(name = "title", example = "Title-14"),
                    @Parameter(name = "text", example = "gydpycamdaowlrtoqwqsqhppbivhwfrdipdipseylelkwcdkbjol"),
                    @Parameter(name = "page", example = "1"),
                    @Parameter(name = "size", example = "2"),
                    @Parameter(name = "sort", example = "time")
            })
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = NewsResponse.class)),
                    examples = @ExampleObject("""
                            [
                              {
                                "id": 11,
                                "time": "2023-04-19T21:49:16:283",
                                "title": "Title-11",
                                "text": "jgzkbgzinbfutwthqafpjpraelewircnvqgyvyshxgwdyurzdoxpuegukcwacnubnpg"
                              },
                              {
                                "id": 12,
                                "time": "2023-02-11T03:32:37:618",
                                "title": "Title-12",
                                "text": "jfcvjpsxhtsdjzmnpajlgoqswiznbtjmsekdwrriqtfkrpdjwsixpgtrvbplpbijygk"
                              }
                            ]
                            """)))
    ResponseEntity<List<NewsWithoutCommentResponse>> findAll(
            @NotNull @Parameter(name = "filter", in = ParameterIn.PATH) NewsFilter filter,
            @NotNull @Parameter(name = "pageable", in = ParameterIn.PATH) Pageable pageable);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/news/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "get",
            tags = "News",
            summary = "Find Comment by id with pageable for comments.",
            parameters = {
                    @Parameter(name = "id_comment", example = "78"),
                    @Parameter(name = "time", example = "2023-11-22T17:24:57.073106"),
                    @Parameter(name = "username", example = "username-41"),
                    @Parameter(name = "text", example = "qnwipagtbjubgbchmwzdsknqhlcvqebawzlocilpr"),
                    @Parameter(name = "newsId", example = "8"),
                    @Parameter(name = "page", example = "1"),
                    @Parameter(name = "size", example = "2"),
                    @Parameter(name = "sort", example = "time")
            })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 14,
                                      "time": "2023-09-07T05:56:15:949",
                                      "title": "Title-14",
                                      "text": "gydpycamdaowlrtoqwqsqhppbivhwfrdipdipseylelkwcdkbjol",
                                      "comments": [
                                        {
                                          "id": 136,
                                          "time": "2023-11-03T11:11:20:917",
                                          "username": "username-22",
                                          "text": "jhdpldlhbdcwnrzkxraqlwcbqkgmjginyen",
                                          "newsId": 14
                                        },
                                        {
                                          "id": 135,
                                          "time": "2023-11-24T10:47:03:625",
                                          "username": "username-41",
                                          "text": "mjfjlfzufydrdpqmfmitfnfqxdacyyehdevsjtxh",
                                          "newsId": 14
                                        }
                                      ]
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
                                              "message": "News with id = 21 not found"
                                            }
                                            """))
                    })
    })
    ResponseEntity<NewsResponse> get(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id,
            @NotNull @Parameter(name = "pageable", in = ParameterIn.PATH) Pageable pageable);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/news",
            produces = "application/json"
    )
    @Operation(
            operationId = "post",
            tags = "News",
            summary = "Save News"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 21,
                                      "time": "2023-06-09T03:35:29:148",
                                      "title": "newTitle",
                                      "text": "something",
                                      "comments": []
                                    }
                                    """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject("[title: 'не должно быть пустым']"),
                                    @ExampleObject("[text: 'не должно быть пустым']")
                            }))
    })
    ResponseEntity<NewsResponse> post(
            @Parameter(name = "request", required = true,
                    example = """
                            {
                              "title": "newTitle",
                              "text": "something"
                            }
                            """) @RequestBody @Valid NewsRequest request);

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/news/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "put",
            tags = "News",
            summary = "Update News and returns with paginated comments"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsResponse.class),
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
                                      "message": "News with id = 999 not found"
                                    }
                                    """)))
    })
    ResponseEntity<NewsResponse> put(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH, example = "21") @PathVariable("id") Long id,
            @Parameter(name = "request", required = true,
                    example = """
                            {
                              "username": "updatedName",
                              "text": "updatedSomething"
                            }
                            """) @RequestBody @Valid NewsRequest request,
            @NotNull @Parameter(name = "pageable", in = ParameterIn.PATH) Pageable pageable);

    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/news/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "patch",
            tags = "News",
            summary = "Update News property and returns with paginated comments"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NewsResponse.class),
                            examples = @ExampleObject("""
                                    {
                                      "id": 21,
                                      "time": "2023-06-09T03:50:10:965",
                                      "title": "newTitle",
                                      "text": "updated only text",
                                      "comments": []
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
    ResponseEntity<NewsResponse> patch(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id,
            @Parameter(name = "patchRequest", required = true,
                    example = """
                            {
                               "field": "text",
                               "value": "\\"updated only text\\""
                            }
                            """
            ) @Valid @RequestBody PatchRequest patchRequest,
            @NotNull @Parameter(name = "pageable", in = ParameterIn.PATH) Pageable pageable);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/news/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "delete",
            tags = "News",
            summary = "Delete News by id."
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
                                              "message": "News with id = 201 not found"
                                            }
                                            """))
                    })
    })
    ResponseEntity<Void> delete(
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id);

}