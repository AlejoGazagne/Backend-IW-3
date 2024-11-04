package ar.edu.iw3.controllers;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.StateException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_CONCILIATION)
@Tag(description = "API del servicio de conciliación", name = "Conciliation")
public class ConciliationRestController extends BaseRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<byte[]> conciliation(@PathVariable long orderId){
        try {
            byte[] pdf = orderBusiness.conciliationPdf(orderId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "conciliation-order" + orderId + ".pdf");
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (BusinessException e) {
            String errorMessage = e.getMessage();
            return new ResponseEntity<>(errorMessage.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            String errorMessage = e.getMessage();
            return new ResponseEntity<>(errorMessage.getBytes(), HttpStatus.NOT_FOUND);
        } catch (StateException e) {
            String errorMessage = e.getMessage();
            return new ResponseEntity<>(errorMessage.getBytes(), HttpStatus.BAD_REQUEST);
        }
    }
}
