package ar.edu.iw3.controllers;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.URL_ALARMS)
@Tag(description = "API para la gestión de alarmas", name = "Alarm")
public class AlarmRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<?> countAlarms() throws BusinessException {
        try {
            return ResponseEntity.ok(alarmBusiness.countAlarms());
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @PostMapping("/accepted")
    public ResponseEntity<?> acceptedAlarm(HttpEntity<String> httpEntity) throws BusinessException, NotFoundException, JsonProcessingException {
        String json = httpEntity.getBody();

        try {
            alarmBusiness.acceptedAlarm(json);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e){
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/")
    public ResponseEntity<?> getAlarms(
            @RequestParam("page") int currentPage,
            @RequestParam("size") int pageSize ) {
        try {
            Page<String> alarms = alarmBusiness.getAlarms(currentPage, pageSize);
            return new ResponseEntity<>(alarms, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/count/month")
    public ResponseEntity<?> getAlarmsReport() {
        try {
            return new ResponseEntity<>(alarmBusiness.countAlarmsByMonthAndProduct(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_OPERATOR') or hasRole('ROLE_ADMIN')")
    @GetMapping("/order/{externalIdOrder}")
    public ResponseEntity<?> getAlarmsByOrder(@PathVariable String externalIdOrder) {
        try {
            System.out.println("externalIdOrder: " + externalIdOrder);
            return new ResponseEntity<>(alarmBusiness.getAlarmsByOrder(externalIdOrder), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}