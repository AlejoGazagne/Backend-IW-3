package ar.edu.iw3.controllers;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.OrderDeserializationException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_SAP)
@Tag(description = "API del servicio SAP (Carga de la orden)", name = "SAP")
public class SAPRestController extends BaseRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;

    /*@Autowired
    private RealTimeLoadService realTimeLoad;;*/

//    @Operation(operationId = "CreateOrder", summary = "Crea una orden de carga")
//    @Parameter(in = ParameterIn.QUERY, name = "order", description = "Orden de carga", required = true)
//    @PostMapping("/order")
//    public ResponseEntity<?> createOrder(@RequestBody Order order) {
//        try {
//            Order response = orderBusiness.add(order);
//            HttpHeaders responseHeaders = new HttpHeaders();
//            responseHeaders.set("location", Constants.URL_SAP + "/order/" + response.getId());
//            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
//        } catch (BusinessException e){
//            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (FoundException e){
//            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
//        }
//    }

    // todo: si el producto no existe devuelve solamente 500 el mensaje no tiene nada
    @Operation(operationId = "CreateOrder", summary = "Crea una orden de carga")
    @Parameter(in = ParameterIn.DEFAULT, name = "order", description = "Orden de carga", required = true)
    @PostMapping("/order")
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity){
        try {
            Order response = orderBusiness.addExternal(httpEntity.getBody());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_SAP + "/order/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        } catch (OrderDeserializationException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }


    /*@PostMapping("/order/start-loading/{orderId}")
    public ResponseEntity<?> startLoading(@PathVariable long orderId, @RequestBody Integer password) {
        try {
            realTimeLoad.startLoading(orderId, password);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_SAP + "/order/start-loading/" + orderId);
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (PasswordException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }*/


}
