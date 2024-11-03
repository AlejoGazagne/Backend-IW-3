package ar.edu.iw3.controllers;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.PasswordException;
import ar.edu.iw3.model.business.exceptions.StateException;
import ar.edu.iw3.model.business.exceptions.TruckloadException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_WEIGHING)
@Tag(description = "API del servicio de pesaje", name = "Weighing")
public class WeighingController extends BaseRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;

    @Operation(operationId = "FirstWeighing", summary = "Cargar el primer pesaje, creación de password y cambio de estado de la orden")
    @Parameter(in = ParameterIn.PATH, name = "orderId", description = "Id de la orden", required = true)
    @Parameter(in = ParameterIn.QUERY, name = "tare", description = "Tara (Peso del camión vacío)", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga del pesaje, generación de password y cambio de estado exitosos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/first/{orderId}")
    public ResponseEntity<?> firstWeighing(@PathVariable long orderId, @RequestParam float tare) {
        try {
            orderBusiness.firstWeighing(orderId, tare);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/first/" + orderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getCause().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // todo: por que tengo que acceder así?
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(operationId = "SecondWeighing", summary = "Cargar pesaje final y cambio de estado")
    @Parameter(in = ParameterIn.PATH, name = "orderId", description = "Id de la orden", required = true)
    @Parameter(in = ParameterIn.QUERY, name = "finalWeight", description = "Peso final del camión", required = true)
    @PostMapping("/final/{orderId}")
    public ResponseEntity<?> finalWeighing(@PathVariable long orderId, @RequestParam float finalWeight) {
        try {
            orderBusiness.finalWeighing(orderId, finalWeight);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/final/" + orderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/load-truck/begin/{orderId}")
    public ResponseEntity<?> loadTruck(@PathVariable long orderId, @RequestBody LoadData loadData) {
        try {
            orderBusiness.beginTruckLoading(orderId, loadData);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/load-truck/" + orderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException | StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (TruckloadException e){
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/load-truck/validate-password/{orderId}")
    public ResponseEntity<?> validatePassword(@PathVariable long orderId, @RequestParam Integer password) {
        try {
            Order order = orderBusiness.validatePassword(orderId, password);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_WEIGHING + "/load-truck/validate-password");
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (PasswordException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/load-truck/finish/{orderId}")
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
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
