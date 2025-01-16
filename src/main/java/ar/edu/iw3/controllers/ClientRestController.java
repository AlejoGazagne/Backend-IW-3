package ar.edu.iw3.controllers;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.interfaces.IClientBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_CLIENTS)
@Tag(description = "API para la gestión de clientes", name = "Client")
public class ClientRestController {

    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IClientBusiness clientBusiness;

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/countClients")
    public ResponseEntity<?> dashboardClientsAndOrdersFinished(){
        try {
            return new ResponseEntity<>(clientBusiness.countClients(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
