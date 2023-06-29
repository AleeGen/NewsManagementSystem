package ru.clevertec.news.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.news.dto.users.UserFilter;
import ru.clevertec.news.dto.users.UserRequest;
import ru.clevertec.news.dto.users.UserResponse;

import java.util.List;

@RestController
@Tag(name = "User", description = "The Users Api")
public interface UserOpenApi {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users",
            produces = "application/json"
    )
    @Operation(
            operationId = "findAll",
            tags = "User",
            summary = "Find all users with pagination and filter from another API.",
            parameters = {
                    @Parameter(name = "username", example = "leo"),
                    @Parameter(name = "firstname", example = "alexey"),
                    @Parameter(name = "lastname", example = "leonenko"),
                    @Parameter(name = "email", example = "leshaleonenko@mail.ru"),
                    @Parameter(name = "role", example = "ADMIN"),
                    @Parameter(name = "page", example = "1"),
                    @Parameter(name = "size", example = "2"),
                    @Parameter(name = "sort", example = "firstname")
            })
    @ApiResponse(
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)),
                    examples = @ExampleObject("""
                            [
                               {
                                  "username": "leon",
                                  "roles": [
                                  "ADMIN"
                                  ],
                                  "firstname": "alexey",
                                  "lastname": "leonenko",
                                  "email": "leshaleonenko@mail.ru"
                               },
                               {
                                  "username": "maks",
                                  "roles": [
                                  "JOURNALIST"
                                  ],
                                  "firstname": "maksim",
                                  "lastname": "maksimov",
                                  "email": "maksmaksimov@gmail.com"
                               }
                            ]
                            """)))
    ResponseEntity<List<UserResponse>> findAll(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token,
            UserFilter filter,
            Pageable pageable);

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "get",
            tags = "User",
            summary = "Find User by id."
    )
    @ApiResponse(
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {
                              "username": "maks",
                              "roles": [
                                  "JOURNALIST"
                              ],
                              "firstname": "maksim",
                              "lastname": "maksimov",
                              "email": "maksmaksimov@gmail.com"
                            }
                            """)))
    ResponseEntity<UserResponse> get(
            @Parameter(name = "token", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id);

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/users/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "put",
            tags = "User",
            summary = "Update User"
    )
    @ApiResponse(
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class),
                    examples = @ExampleObject("""
                            {
                              "username": "Leo2",
                              "roles": [
                                  "ADMIN"
                              ],
                              "firstname": "newFirstname",
                              "lastname": "newLastname",
                              "email": "newEmail@mail.ru"
                            }
                            """)))
    ResponseEntity<UserResponse> put(
            @Parameter(name = "token", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(name = "id", required = true, in = ParameterIn.PATH, example = "201") @PathVariable("id") Long id,
            @Parameter(name = "request", required = true,
                    example = """
                            {
                              "firstname": "newFirstname",
                              "lastname": "newLastname",
                              "email": "newEmail@mail.ru"
                            }
                            """) @RequestBody @Valid UserRequest request);

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/users/{id}",
            produces = "application/json"
    )
    @Operation(
            operationId = "delete",
            tags = "User",
            summary = "Delete User by id."
    )
    @ApiResponse(
            responseCode = "204",
            description = "No Content")
    ResponseEntity<Void> delete(
            @Parameter(name = "token", required = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id);

}