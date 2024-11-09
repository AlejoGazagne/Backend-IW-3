package ar.edu.iw3.controllers;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
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

    @Operation(operationId = "conciliation", summary = "Genera un archivo PDF con la conciliación de la orden")
    @Parameter(in = ParameterIn.PATH, name = "externalOrderId", description = "Id externo de la orden", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conciliación generada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "409", description = "La orden no se encuentra en estado de conciliación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/order/{externalOrderId}")
    public ResponseEntity<byte[]> conciliation(@PathVariable String externalOrderId){
        try {
            byte[] pdf = orderBusiness.conciliationPdf(externalOrderId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "conciliation-order" + externalOrderId + ".pdf");
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (BusinessException e) {
            String errorMessage = e.getMessage();
            return new ResponseEntity<>(errorMessage.getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            String errorMessage = e.getMessage();
            return new ResponseEntity<>(errorMessage.getBytes(), HttpStatus.NOT_FOUND);
        } catch (StateException e) {
            String errorMessage = e.getMessage();
            return new ResponseEntity<>(errorMessage.getBytes(), HttpStatus.CONFLICT);
        }
    }
}
