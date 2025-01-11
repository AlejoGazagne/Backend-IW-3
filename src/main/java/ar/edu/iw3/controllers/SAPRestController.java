package ar.edu.iw3.controllers;

import ar.edu.iw3.auth.model.business.interfaces.IUserBusiness;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.*;
import ar.edu.iw3.model.business.interfaces.IClientBusiness;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.business.interfaces.IProductBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_SAP)
@Tag(description = "API del servicio SAP (Carga de la orden)", name = "SAP")
public class SAPRestController extends BaseRestController {

    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;

    @Autowired
    private IProductBusiness productBusiness;

    @Autowired
    private IClientBusiness clientBusiness;

    @Autowired
    private IUserBusiness userBusiness;

    @Operation(operationId = "CreateOrder", summary = "Crea una orden de carga")
    @Parameter(in = ParameterIn.DEFAULT, name = "order", description = "Orden de carga", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de carga creada"),
            @ApiResponse(responseCode = "400", description = "Error en la deserialización de la orden de carga"),
            @ApiResponse(responseCode = "404", description = "No se encontró alguna entidad relacionada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "302", description = "Orden de carga ya existente")
    })
    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
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

    // todo: mover a otro controllador

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id){
        try {
            return new ResponseEntity<>(orderBusiness.find(id), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/order/{id}/details")
    public ResponseEntity<?> getDetailsOrder(@PathVariable String id){
        try {
            return new ResponseEntity<>(orderBusiness.getDetailsOrder(id), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (StateException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(
            @RequestParam("page") int currentPage,
            @RequestParam("size") int pageSize,
            @RequestParam(name = "filter", required = false) Order.State state) {
        try {
            Page<Order> orders = orderBusiness.getOrders(currentPage, state, pageSize);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @PostMapping("/product")
    public ResponseEntity<?> add(HttpEntity<String> httpEntity) throws JsonProcessingException {
        String json = httpEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(json, Product.class);

        // Imprime el objeto Product para verificar
        System.out.println("Producto recibido: " + product);

        try{
            Product response = productBusiness.add(product);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", Constants.URL_SAP + "/product/" + response.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch(BusinessException e){
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(FoundException e){
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }

//    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
//    @PostMapping("/product")
//    public ResponseEntity<?> add(HttpEntity<String> httpEntity) {
//        System.out.println(httpEntity.getBody());
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/orders/count")
    public ResponseEntity<?> count(){
        try {
            return new ResponseEntity<>(orderBusiness.countOrders(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/countClients")
    public ResponseEntity<?> dashboardClientsAndOrdersFinished(){
        try {
            return new ResponseEntity<>(clientBusiness.countClients(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/orders/totalFinished")
    public ResponseEntity<?> totalFinished(){
        try {
            return new ResponseEntity<>(orderBusiness.countOrders(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/orders/all-orders-count")
    public ResponseEntity<?> allOrdersCount(){
        try {
            return new ResponseEntity<>(orderBusiness.countOrdersByMonth(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/orders/products-count")
    public ResponseEntity<?> productsCount(){
        try {
            return new ResponseEntity<>(orderBusiness.countProducts(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/orders/count-all-clients")
    public ResponseEntity<?> countAllClients(){
        try {
            return new ResponseEntity<>(orderBusiness.countAllClients(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(){
        try {
            return new ResponseEntity<>(productBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_SAP') or hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        try {
            return new ResponseEntity<>(userBusiness.getAdminsAndOperators(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/user")
    public ResponseEntity<?> editUser(HttpEntity<String> httpEntity) throws JsonProcessingException {
        String json = httpEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        try{
            userBusiness.editUser(jsonNode);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/user")
    public ResponseEntity<?> addUser(HttpEntity<String> httpEntity) throws JsonProcessingException {
        String json = httpEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        try{
            userBusiness.createUser(jsonNode);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        }
    }
}