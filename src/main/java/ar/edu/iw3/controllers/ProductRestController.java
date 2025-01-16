package ar.edu.iw3.controllers;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.interfaces.IProductBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_PRODUCTS)
@Tag(description = "API para la gestión de productos", name = "Product")
public class ProductRestController extends BaseRestController {

    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IProductBusiness productBusiness;

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
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

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/")
    public ResponseEntity<?> getProducts(){
        try {
            return new ResponseEntity<>(productBusiness.list(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
