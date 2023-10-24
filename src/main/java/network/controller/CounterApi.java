package network.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import network.dto.ErrorModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Validated
public interface CounterApi {
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Получение количества сообщений", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Number.class)))),
        @ApiResponse(responseCode = "400", description = "Невалидные данные ввода"),
        @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
        @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))),
        @ApiResponse(responseCode = "503", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorModel.class))) })
    @RequestMapping(value = "/counter", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<Number> counterGet(Principal principal);
}

