package ar.edu.iw3.controllers;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.PasswordException;
import ar.edu.iw3.model.business.exceptions.StateException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(Constants.URL_WEIGHING)
@Tag(description = "API del servicio de pesaje", name = "Weighing")
public class WeighingRestController extends BaseRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;

    @Operation(operationId = "FirstWeighing", summary = "Cargar el primer pesaje, creación de password y cambio de estado de la orden")
    @Parameter(in = ParameterIn.PATH, name = "externalOrderId", description = "Id externo de la orden", required = true)
    @Parameter(in = ParameterIn.QUERY, name = "tare", description = "Tara (Peso del camión vacío)", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga del pesaje, generación de password y cambio de estado exitosos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden de compra se encuentra en un estado que no permite realizar esta operación o no se pudo generar la password de la orden."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_WEIGHING') or hasRole('ROLE_ADMIN')")
    @PostMapping("/first/{externalOrderId}")
    public ResponseEntity<?> firstWeighing(@PathVariable String externalOrderId, @RequestParam float tare) {
        try {
            orderBusiness.firstWeighing(externalOrderId, tare);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/first/" + externalOrderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException | PasswordException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @Operation(operationId = "SecondWeighing", summary = "Cargar pesaje final y cambio de estado")
    @Parameter(in = ParameterIn.PATH, name = "externalOrderId", description = "Id esterno de la orden", required = true)
    @Parameter(in = ParameterIn.QUERY, name = "finalWeight", description = "Peso final del camión", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga del pesaje y cambio de estado exitosos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden de compra se encuentra en un estado que no permite realizar esta operación."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_WEIGHING') or hasRole('ROLE_ADMIN')")
    @PostMapping("/final/{externalOrderId}")
    public ResponseEntity<?> finalWeighing(@PathVariable String externalOrderId, @RequestParam float finalWeight) {
        try {
            Map<String, Object> response = orderBusiness.finalWeighing(externalOrderId, finalWeight);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/final/" + externalOrderId);
            return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
