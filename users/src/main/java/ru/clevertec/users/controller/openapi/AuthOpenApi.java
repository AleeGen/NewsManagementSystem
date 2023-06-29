package ru.clevertec.users.controller.openapi;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.users.dto.auth.AuthDto;
import ru.clevertec.users.dto.auth.LoginDto;
import ru.clevertec.users.dto.auth.RegisterDto;
import ru.clevertec.users.dto.auth.TokenDto;
import ru.clevertec.users.dto.users.UserResponse;

@RestController
@Tag(name = "Auth", description = "The Auth Api")
public interface AuthOpenApi {

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth",
            produces = "application/json"
    )
    @Operation(
            operationId = "auth",
            tags = "Auth",
            summary = "Get authentication by token.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AuthDto.class)),
                    examples = @ExampleObject("""
                            {
                              "username":"Leo",
                              "authorities":[
                              "JOURNALIST"
                              ]
                            }
                            """)))
    ResponseEntity<AuthDto> auth(
            @Parameter(name = "token", in = ParameterIn.HEADER, example = """
                    Bearer eyJhbGciOiJIUzI1NiJ9
                           .eyJzdWIiOiJMZW8yIiwiaWF0IjoxNjg3Njk5NzU2LCJleHAiOjE2ODc3MDMzNTZ9
                           .RhnZejjjr4x-a9bNo-QXmtaHm_Y8lyYVnOOKVHVoEX4
                    """) @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth/login",
            produces = "application/json"
    )
    @Operation(
            operationId = "login",
            tags = "Auth",
            summary = "Get token by username and password.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TokenDto.class)),
                    examples = @ExampleObject("""
                            {
                                "token": "eyJhbGciOiJIUzI1NiJ9
                                .eyJzdWIiOiJMZW8yIiwiaWF0IjoxNjg3Njk5NzU2LCJleHAiOjE2ODc3MDMzNTZ9
                                .RhnZejjjr4x-a9bNo-QXmtaHm_Y8lyYVnOOKVHVoEX4"
                            }
                            """)))
    ResponseEntity<TokenDto> login(
            @Parameter(name = "request", required = true, example = """
                    {
                      "username": "Leo",
                      "password": "pass"
                    }
                    """) @RequestBody @Valid LoginDto request);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth/register",
            produces = "application/json"
    )
    @Operation(
            operationId = "register",
            tags = "Auth",
            summary = "Get token after register user.")
    @ApiResponse(
            responseCode = "201",
            description = "Create",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TokenDto.class)),
                    examples = @ExampleObject("""
                            {
                              "token": "eyJhbGciOiJIUzI1NiJ9
                              .eyJzdWIiOiJMZW8yIiwiaWF0IjoxNjg3Njk5NzU2LCJleHAiOjE2ODc3MDMzNTZ9
                              .RhnZejjjr4x-a9bNo-QXmtaHm_Y8lyYVnOOKVHVoEX4"
                            }
                            """)))
    ResponseEntity<TokenDto> register(
            @Parameter(name = "request", required = true, example = """
                    {
                      "username": "Leo",
                      "password": "pass",
                      "firstname": "Alexey",
                      "lastname": "Leonenko",
                      "email": "leshaleonenko@mail.ru
                    }
                    """) @RequestBody @Valid RegisterDto request);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/auth/about_me",
            produces = "application/json"
    )
    @Operation(
            operationId = "aboutMe",
            tags = "Auth",
            summary = "Get info about current user.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)),
                    examples = @ExampleObject("""
                            {
                              "username": "Leo",
                              "roles": [
                                  "JOURNALIST"
                              ],
                              "firstname": "Alexey",
                              "lastname": "Leonenko",
                              "email": "leshaleonenko@mail.ru"
                            }
                            """)))
    ResponseEntity<UserResponse> aboutMe(
            @Parameter(name = "token", required = true, example = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiJ9
                      .eyJzdWIiOiJMZW8yIiwiaWF0IjoxNjg3Njk5NzU2LCJleHAiOjE2ODc3MDMzNTZ9
                      .RhnZejjjr4x-a9bNo-QXmtaHm_Y8lyYVnOOKVHVoEX4"
                    }
                    """) @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token);

}