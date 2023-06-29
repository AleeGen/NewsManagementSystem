package ru.clevertec.news.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.news.dto.auth.LoginDto;
import ru.clevertec.news.dto.auth.RegisterDto;
import ru.clevertec.news.dto.auth.TokenDto;
import ru.clevertec.news.dto.users.UserResponse;

@RestController
@Tag(name = "Authentication", description = "The Authentication Api")
public interface AuthenticationOpenApi {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth/login",
            produces = "application/json"
    )
    @Operation(
            operationId = "login",
            tags = "Authentication",
            summary = "Receives an authentication token by login and password.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TokenDto.class)),
                    examples = @ExampleObject("""
                            Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
                            .eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
                            .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
                            """)))
    ResponseEntity<TokenDto> login(
            @Parameter(name = "request", required = true,
                    example = """
                            {
                               "username": "leon",
                               "password": "pass"
                            }
                            """
            ) @RequestBody LoginDto request);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth/register",
            produces = "application/json"
    )
    @Operation(
            operationId = "register",
            tags = "Authentication",
            summary = "Receives an authentication token after successful registration.")
    @ApiResponse(
            responseCode = "201",
            description = "CREATED",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TokenDto.class)),
                    examples = @ExampleObject("""
                            Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
                            .eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
                            .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
                            """)))
    ResponseEntity<TokenDto> register(
            @Parameter(name = "request", required = true,
                    example = """
                            {
                               "username": "leon",
                               "password": "pass"
                               "firstname": "alexey",
                               "lastname": "leonenko"
                               "email": "leshaleonenko@mail.ru"
                            }
                            """
            ) @RequestBody RegisterDto request);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth/about_me",
            produces = "application/json"
    )
    @Operation(
            operationId = "aboutMe",
            tags = "Authentication",
            summary = "Getting information about the current user.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)),
                    examples = @ExampleObject("""
                            {
                               "username": "leon",
                               "roles": [
                                   "ADMIN"
                               ],
                               "firstname": "alexey",
                               "lastname": "leonenko",
                               "email": "leshaleonenko@mail.ru"
                            }
                            """)))
    ResponseEntity<UserResponse> aboutMe(
            @Parameter(name = "token", required = true, example = """
                    Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
                            .eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
                            .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
                    """) @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token);

}