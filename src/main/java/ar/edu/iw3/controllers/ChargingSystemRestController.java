package ar.edu.iw3.controllers;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.serializers.OrderJsonSerializer;
import ar.edu.iw3.util.IStandartResponseBusiness;
import ar.edu.iw3.util.JsonUtiles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(Constants.URL_CHARGING_SYSTEM)
@Tag(description = "API del servicio de sistema de carga", name = "Charging System")
public class ChargingSystemRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;

    @Operation(operationId = "ValidarOrden", summary = "Validar la contraseña de la orden")
    @Parameter(in = ParameterIn.QUERY, name = "password", description = "Contraseña de la orden", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden validada"),
            @ApiResponse(responseCode = "400", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden de compra se encuentra en un estado que no permite realizar esta operación."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_CHARGING_SYSTEM') or hasRole('ROLE_ADMIN')")
    @PostMapping("/validate-password")
    public ResponseEntity<?> validatePassword(@RequestParam Integer password) {
        try {
            Order response = orderBusiness.validatePassword(password);
            ObjectMapper mapper = JsonUtiles.getObjectMapper(Order.class, new OrderJsonSerializer(Order.class), null);
            String rspJson = mapper.writeValueAsString(response);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/load-truck/validate-password");
            return new ResponseEntity<>(rspJson, responseHeaders, HttpStatus.OK);
        } catch (BusinessException | JsonProcessingException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException | PasswordException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @Operation(operationId = "CargaCamión", summary = "Cargar los datos de la carga del camión y cambiar el estado de la orden")
    @Parameter(in = ParameterIn.PATH, name = "orderId", description = "Id de la orden", required = true)
    @Parameter(in = ParameterIn.QUERY, name = "loadData", description = "Datos de la carga del camión", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga de los datos de la carga del camión."),
            @ApiResponse(responseCode = "404", description = "Error de los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_CHARGING_SYSTEM') or hasRole('ROLE_ADMIN')")
    @PostMapping("/load-truck/{orderId}")
    @SendTo("/topic/load-truck")
    public ResponseEntity<?> loadTruck(@PathVariable long orderId, @RequestBody LoadData loadData) {
        try {
            orderBusiness.beginTruckLoading(orderId, loadData);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/load-truck/" + orderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException | TruckloadException | StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @Operation(operationId = "FinalizarCargaCamión", summary = "Finalizar la carga del camión")
    @Parameter(in = ParameterIn.PATH, name = "orderId", description = "Id de la orden", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Finalización de la carga del camión."),
            @ApiResponse(responseCode = "404", description = "Error de los datos"),
            @ApiResponse(responseCode = "409", description = "La orden de compra se encuentra en un estado que no permite realizar esta operación."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_CHARGING_SYSTEM') or hasRole('ROLE_ADMIN')")
    @PostMapping("/finish/{orderId}")
    public ResponseEntity<?> finishTruckLoading(@PathVariable long orderId) {
        try {
            orderBusiness.finishTruckLoading(orderId);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/load-truck/finish/" + orderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
